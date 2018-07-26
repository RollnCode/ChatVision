package chapters.chatRoom

import adapters.ChatDialogAdapter
import android.arch.lifecycle.Lifecycle.Event.ON_START
import android.arch.lifecycle.Lifecycle.Event.ON_STOP
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import base.BFragment
import chapters.chatRoom.layers.ChatRoomRouter
import chapters.chatRoom.layers.DialogContracts
import chapters.chatRoom.layers.DialogPresenter
import chapters.chatRoom.viewModels.ChatRoomViewModel
import chapters.chatRoom.viewModels.DialogViewModel
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import com.rollncode.basement.utility.extension.hide
import com.rollncode.basement.utility.extension.showAlert
import com.rollncode.basement.utility.extension.string
import com.rollncode.chatVision.R
import instruments.onClickObservable
import instruments.toLog
import instruments.userName
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_chat_dialogs.*
import storage.Settings

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
class ChatDialogFragment : BFragment(), DialogContracts.View {

    companion object {
        private const val DIALOG_KEY = "DIALOG_KEY"
        fun instance(dialog: QBChatDialog): ChatDialogFragment {
            toLog("instance")
            val frag = ChatDialogFragment()
            val args = Bundle()
            args.putSerializable(DIALOG_KEY, dialog)
            frag.arguments = args
            return frag.also { DialogPresenter(it) }
        }
    }

    override val dialog by lazy { (arguments?.getSerializable(DIALOG_KEY) as? QBChatDialog) ?: QBChatDialog() }
    override val getMoreMessagesTrigger by lazy { adapter.scrollObservable }
    override val sendMessageObservable: Observable<String>
        get() = btnSend.onClickObservable()
                .map { edtInputMessage.string }
    override val loadedMsgList: List<QBChatMessage>
        get() = adapter.getMessageList()
    override val viewModel by lazy { ViewModelProviders.of(this).get(DialogViewModel::class.java) }
    private val adapter by lazy { ChatDialogAdapter(::showUserInfo) }
    private val registry by lazy { LifecycleRegistry(this) }

    override fun getLayoutRes() =
            R.layout.fragment_chat_dialogs

    override fun getMenuLayout(): Int =
            if (dialog.isPrivate) 0 else R.menu.menu_group_chat

    override fun onViewCreated(v: View, b: Bundle?) {
        super.onViewCreated(v, b)
        registry.handleLifecycleEvent(ON_START)
        activity?.title = dialog.name
        recyclerView.adapter = adapter
    }

    override fun getLifecycle() =
            registry

    override fun onDestroyView() {
        super.onDestroyView()
        registry.handleLifecycleEvent(ON_STOP)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_members) {
            showAlert {
                setItems(viewModel.users.map { it.userName }.toTypedArray()) { _, which ->
                    val user = viewModel.users[which]
                    if (user.id == Settings.system.currentUserId) {
                        showUserInfo(null)
                    } else {
                        showUserInfo(viewModel.users[which])
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setSendingFormVisibility(isVisible: Boolean) {
        if (!isVisible) groupSendingForm.hide()
    }

    override fun onUsersLoaded(list: List<QBUser>) {
        adapter.setUsers(list)
        viewModel.setOccupants(list)
    }

    override fun onMessagesLoaded(list: List<QBChatMessage>, refresh: Boolean) {
        if (refresh) adapter.setData(list).run { recyclerView.scrollToPosition(0) }
        else adapter.addMessages(list)
    }

    override fun onNewMessageAdded(msg: QBChatMessage) {
        adapter.newMessage(msg)
        recyclerView.scrollToPosition(0)
    }

    override fun onMessageSent() {
        edtInputMessage.text = null
    }

    override fun onMessageSendingError(err: Throwable, msg: String) {
        edtInputMessage.setText(msg)
        onRequestError(err)
    }

    private fun showUserInfo(user: QBUser?) {
        (activity as? ChatRoomRouter)?.showUserInfo(user)
    }

    override fun onMessagesRead(message: QBChatMessage?) {
        ViewModelProviders.of(this).get(ChatRoomViewModel::class.java).updateDialog(message)
    }

    override fun onMessageStatusChanged(messageId: String) {
        adapter.setMessageRead(messageId)
    }

    override fun updateSentMessage(message: QBChatMessage) {
        adapter.updateSentMessage(message)
        recyclerView.scrollToPosition(0)
    }

    override fun setProgress(start: Boolean) {
        super.setProgress(start)
        activity?.runOnUiThread { btnSend.isEnabled = !start }
    }
}