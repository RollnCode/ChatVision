package chapters.chatRoom.layers

import android.Manifest
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import base.BPresenter
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import com.rollncode.chatVision.R
import instruments.QBHelper
import instruments.STATE_READ
import instruments.dialogDb
import instruments.shortLogin
import instruments.toLog
import instruments.userDb
import instruments.userName
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import storage.Settings
import storage.room.Dialog
import storage.room.User
import utils.ContactMap
import utils.DialogsList
import utils.NeedToUpdateEvent
import utils.RxBus

@Suppress("MoveLambdaOutsideParentheses")
/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
class ChatRoomPresenter(private val view: ChatRoomContracts.View) : BPresenter(view),
        LifecycleObserver {

    init {
        view.lifecycle.addObserver(this)
    }

    private val interactor: ChatRoomContracts.Interactor by lazy {
        if (Settings.system.isOnline) ChatRoomInteractor() else ChatRoomOfflineInteractor()
    }
    private var isNeedToUpdate = false

    @Suppress("ProtectedInFinal")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun bind() {
        if (view.viewModel.chats.isEmpty() && view.viewModel.users.isEmpty()) {
            updateList()
        } else {
            filterLists(view.viewModel.chats, view.viewModel.users, view.viewModel.contacts)
        }
        view.addDisposable(view.createPrivateDialogObservable
            .subscribe { createDialog(it) })
        view.addDisposable(view.refreshObservable
            .subscribe { updateList() })
        view.addDisposable(view.deleteDialogObservable
            .subscribe { execute(interactor.deleteDialog(it), { updateList() }) })
        RxBus.observeEvent(NeedToUpdateEvent::class.java)
            .filter { !isNeedToUpdate }
            .subscribe { isNeedToUpdate = true }

        observeMessages()
        observeMessagesStatus()
    }

    @Suppress("ProtectedInFinal")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected fun check() {
        if (isNeedToUpdate) {
            updateList()
            isNeedToUpdate = false
        }
    }

    private fun updateList() {
        execute(Single.zip(
                view.rxPermissions.request(Manifest.permission.READ_CONTACTS)
                    .map { if (it) view.contacts else listOf() }
                    .firstOrError()
                    .map { it.groupBy({ it.first }, { it.second.replace(" ", "") }) },
                interactor.getDialogs(),
                interactor.getUsers(),
                Function3<ContactMap, DialogsList, List<QBUser>, Triple<DialogsList, List<QBUser>, ContactMap>> { contacts, dialogs, users ->
                    Triple(dialogs, users, contacts)
                }),
                { filterLists(it.first, it.second, it.third) })
    }

    private fun createDialog(user: QBUser) {
        toLog("createDialog")
        execute(interactor.createPrivateDialog(user.id), {
            isNeedToUpdate = true
            view.onDialogCreated(it)
        })
    }

    private fun filterLists(dialogs: DialogsList, users: List<QBUser>, contacts: ContactMap) {
        view.viewModel.setData(dialogs, users, contacts)
        val usersFromPrivateDialogs = dialogs.filter { it.isPrivate }.flatMap { it.occupants }.distinct()
        val registeredUsers = users.map { it.shortLogin }
        view.onContactsListLoaded(dialogs,
                users.filter { !usersFromPrivateDialogs.contains(it.id) }.sortedBy { it.userName },
                contacts.filter { contact ->
                    !contact.value.any { phone -> registeredUsers.any { it.endsWith(phone) } }
                }.toSortedMap())
        userDb.addAll(*users.map { User(it) }.toTypedArray())
        dialogDb.addAll(*dialogs.map { Dialog(it) }.toTypedArray())
    }

    private fun observeMessages() {
        if (!Settings.system.isOnline) return
        view.addDisposable(interactor.observeIncomingMessages()
            .flatMap {
                interactor.getDialog(it.first).toFlowable()
                    .zipWith(Flowable.just(it.second),
                            BiFunction<QBChatDialog, QBChatMessage, Pair<QBChatDialog, QBChatMessage>> { dialog, msg ->
                                Pair(dialog, msg)
                            })
            }
            .flatMap {
                it.first.deliverMessage(it.second)
                Flowable.just(it.first)
            }
            .subscribe({ view.updateDialog(it) }, { view.onRequestError(it) }))
    }

    private fun observeMessagesStatus() {
        if (!Settings.system.isOnline) return
        view.addDisposable(interactor.observeMessagesStatus()
            .subscribe({
                val image = if (it.first.first == STATE_READ) R.drawable.svg_read else R.drawable.svg_delivered
                QBHelper.getChatMessages(view.viewModel.dialog, 1)
                    .subscribe({

                        val del = it[0].deliveredIds
                        if (del != null && del.isNotEmpty()) {
                            for (id in del) {
                            }
                        }

                        val read = it[0].readIds
                        if (read != null && read.isNotEmpty()) {
                            for (r in read) {
                            }
                        }

                    }, {})
                view.onStatusChanged(image, it.second)
            }, { view.onRequestError(it) })
                          )
    }
}