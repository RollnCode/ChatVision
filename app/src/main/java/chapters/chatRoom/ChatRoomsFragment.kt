package chapters.chatRoom

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.content.res.ResourcesCompat
import android.view.MenuItem
import android.view.ViewGroup
import base.BFragment
import chapters.chatRoom.layers.ChatRoomContracts.View
import chapters.chatRoom.layers.ChatRoomPresenter
import chapters.chatRoom.layers.ChatRoomRouter
import chapters.chatRoom.viewModels.ChatRoomViewModel
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModelWithView
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.users.model.QBUser
import com.rollncode.basement.utility.extension.showAlert
import com.rollncode.chatVision.R
import com.tbruyelle.rxpermissions2.RxPermissions
import instruments.onRefreshObservable
import instruments.toLog
import instruments.userName
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_chat_rooms.*
import kotlinx.android.synthetic.main.view_no_dialog.view.*
import storage.Settings
import utils.Contact
import utils.ContactMap
import utils.DialogsList
import utils.FullContact
import utils.OfflineException
import view.ContactItemView
import view.DialogListItemView
import view.NoDialogItemView

/**
 *
 * @author Sviatoslav Koliesnik kolesniksy@gmail.com
 * @since 2017.11.07
 */
class ChatRoomsFragment : BFragment(), View {

    companion object {
        fun instance(): ChatRoomsFragment =
                ChatRoomsFragment().also { ChatRoomPresenter(it) }
    }

    override val contacts: List<Contact> by lazy {
        mutableListOf<Pair<String, String>>().apply {
            activity?.let {
                with(it.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)) {
                    while (this?.moveToNext() == true) {
                        add(Pair(getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                                getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))))
                    }
                    close()
                }
            }

        }
    }
    override val rxPermissions by lazy { RxPermissions(activity!!) }
    override val viewModel by lazy { ViewModelProviders.of(this).get(ChatRoomViewModel::class.java) }
    override val createPrivateDialogObservable by lazy { adapter.createDialogObservable }
    override val refreshObservable: Observable<Unit> get() = swipeLayout.onRefreshObservable()
    override val deleteDialogObservable: Observable<String> = Observable.create { emitter ->
        deleteListener = { emitter.onNext(it) }
    }
    private val router by lazy { activity as? ChatRoomRouter }
    private val adapter = DialogAdapter(listOf(), listOf(), mapOf())
    private var deleteListener: (String) -> Unit = {}

    override fun getLayoutRes() = R.layout.fragment_chat_rooms
    override fun getMenuLayout(): Int = R.menu.menu_info

    override fun onViewCreated(v: android.view.View, b: Bundle?) {
        super.onViewCreated(v, b)
        swipeLayout.setProgressBackgroundColorSchemeResource(R.color.white)
        swipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent)
        activity?.title = getString(R.string.Chat_rooms)
        fab.setOnClickListener {
            showAlert {
                setTitle(R.string.create_chat)
                val users = viewModel.users.filter { it.id != Settings.system.currentUserId }
                val selectedUsers = mutableListOf<QBUser>()
                setMultiChoiceItems(users.map { it.userName }.toTypedArray(), BooleanArray(users.size)) { _, which, isChecked ->
                    val user = users[which]
                    if (isChecked) selectedUsers.add(user) else selectedUsers.remove(user)
                }
                setPositiveButton(android.R.string.ok) { _, _ ->
                    if (selectedUsers.isEmpty()) return@setPositiveButton
                    if (selectedUsers.size == 1) {
                        adapter.openDialog(selectedUsers.first().id)
                    } else {
                        router?.createNewDialog(selectedUsers)
                    }
                }
            }
        }
    }

    override fun onContactsListLoaded(dialogs: DialogsList, users: List<QBUser>, contacts: ContactMap) {
        adapter.setData(dialogs, users, contacts)
        recyclerView.adapter = adapter
        swipeLayout.isRefreshing = false
    }

    override fun onDialogCreated(dialog: QBChatDialog) {
        router?.onDialogSelected(dialog)
    }

    private fun onPhoneContactSelected(contact: FullContact) =
            router?.onPhoneContactSelected(contact)

    override fun updateDialog(dialog: QBChatDialog) {
        adapter.updateDialog(dialog)
    }

    override fun onStatusChanged(drawableRes: Int, dialogId: String) {
        adapter.updateStatusIcon(ResourcesCompat.getDrawable(resources, drawableRes, null), dialogId)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_info) {
            showAlert {
                setTitle(R.string.help)
                setMessage(R.string.help_content)
                setPositiveButton(R.string.btn_ok, null)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class DialogAdapter(list: DialogsList, users: List<QBUser>, contacts: ContactMap) : EpoxyAdapter() {

        private var listener: (QBUser) -> Unit = {}
        val createDialogObservable: Observable<QBUser> = Observable.create { emitter ->
            listener = {
                toLog("listener")
                emitter.onNext(it) }
        }

        init {
            removeAllModels()
            addModels(list.map { DialogModel(it) })
            addModels(users.map { UserModel(it) })
            addModels(contacts.map { ContactModel(it) })
            enableDiffing()
        }

        fun setData(list: DialogsList, users: List<QBUser>, contacts: ContactMap) {
            removeAllModels()
            addModels(list.map { DialogModel(it) })
            addModels(users.map { UserModel(it) })
            addModels(contacts.map { ContactModel(it) })
        }

        fun updateDialog(dialog: QBChatDialog) {
            models.find { (it as? DialogModel)?.dialogId == dialog.dialogId }?.let {
                (it as? DialogModel)?.dialog?.unreadMessageCount = dialog.unreadMessageCount
                (it as? DialogModel)?.dialog?.lastMessage = dialog.lastMessage
                (it as? DialogModel)?.dialog?.lastMessageDateSent = dialog.lastMessageDateSent
                notifyModelChanged(it)
            }
        }

        fun updateStatusIcon(drawable: Drawable?, dialogId: String) {
            models.find { (it as? DialogModel)?.dialogId == dialogId }?.let {
                (it as? DialogModel)?.view?.updateStatusImage(drawable)

                //notifyModelChanged(it)
            }
        }

        fun openDialog(userId: Int) {
            models.forEach {
                if (it is DialogModel) {
                    val dialog = it.dialog
                    if (dialog.isPrivate && dialog.occupants.contains(userId)) {
                        viewModel.setDialog(dialog)
                        onDialogCreated(dialog)
                        return
                    }
                } else if (it is UserModel) {
                    if (it.user.id == userId) {
                        listener(it.user)
                        return
                    }
                }
            }
        }

        inner class DialogModel(var dialog: QBChatDialog) : EpoxyModelWithView<DialogListItemView>() {

            val dialogId: String = dialog.dialogId
            lateinit var view: DialogListItemView

            @SuppressLint("InflateParams")
            override fun buildView(parent: ViewGroup): DialogListItemView = DialogListItemView(parent.context)

            override fun bind(view: DialogListItemView) {
                this.view = view
                view.setData(dialog)
                view.setOnClickListener {
                    viewModel.setDialog(dialog)
                    onDialogCreated(dialog)
                }
                view.setOnLongClickListener {
                    showAlert {
                        setMessage(R.string.btn_delete)
                        setPositiveButton(R.string.btn_ok) { _, _ ->
                            if (Settings.system.isOnline) deleteListener(dialog.dialogId)
                            else onRequestError(OfflineException())
                        }
                    }
                    true
                }
            }
        }

        inner class UserModel(val user: QBUser) : EpoxyModelWithView<NoDialogItemView>() {
            @SuppressLint("InflateParams")
            override fun buildView(parent: ViewGroup): NoDialogItemView =
                    NoDialogItemView(parent.context)

            override fun bind(view: NoDialogItemView) {
                view.tvName.text = user.userName
                view.setOnClickListener {
                    if (Settings.system.isOnline) listener(user)
                    else onRequestError(OfflineException())
                }
            }
        }

        inner class ContactModel(private val contact: FullContact) : EpoxyModelWithView<ContactItemView>() {
            @SuppressLint("InflateParams")
            override fun buildView(parent: ViewGroup): ContactItemView =
                    ContactItemView(parent.context)

            override fun bind(view: ContactItemView) {
                view.setText(contact.key)
                view.setOnClickListener { onPhoneContactSelected(contact) }
            }
        }
    }
}