package chapters.chatRoom.layers

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import base.BPresenter
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import instruments.DIALOG_MESSAGES_LIMIT
import instruments.QBHelper
import instruments.isIncoming
import instruments.messageDb
import instruments.userDb
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import storage.Settings
import storage.room.Message
import storage.room.User
import utils.NeedToUpdateEvent
import utils.RxBus

@Suppress("MoveLambdaOutsideParentheses")
/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
class DialogPresenter(private val view: DialogContracts.View) : BPresenter(view),
        LifecycleObserver {

    init {
        view.lifecycle.addObserver(this)
    }

    private val interactor: DialogContracts.Interactor by lazy {
        if (Settings.system.isOnline) DialogIneractor() else DialogOfflineInteractor()
    }

    @Suppress("ProtectedInFinal")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun bind() {
        if (view.viewModel.users.isEmpty()) {
            execute(Single.zip(interactor.getDialogUsers(view.dialog.occupants ?: listOf()),
                    interactor.getMessagesList(view.dialog),
                    BiFunction<List<QBUser>, List<QBChatMessage>, Pair<List<QBUser>, List<QBChatMessage>>> { users, msgs -> Pair(users, msgs) }),
                    {
                        messageDb.addAll(*it.second.map { Message(it) }.toTypedArray())
                        userDb.addAll(*it.first.map { User(it) }.toTypedArray())
                        view.onUsersLoaded(it.first)
                        view.onMessagesLoaded(it.second)
                        readUnreadMessages(it.second)
                        view.onMessagesRead()
                        observeMessages()
                    })
        } else {
            val messages = view.viewModel.messages
            val users = view.viewModel.users
            messageDb.addAll(*messages.map { Message(it) }.toTypedArray())
            userDb.addAll(*users.map { User(it) }.toTypedArray())
            view.onUsersLoaded(users)
            view.onMessagesLoaded(messages)
            readUnreadMessages(messages)
            view.onMessagesRead()
            observeMessages()
        }

        view.addDisposable(view.getMoreMessagesTrigger
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .subscribe { getElderMessages() })
        if (Settings.system.isIncognito || !Settings.system.isOnline) {
            view.setSendingFormVisibility(false)
            return
        }
        view.addDisposable(view.sendMessageObservable
                .filter { it.isNotBlank() }
                .subscribe { sendMessage(it) })

        observeMessagesStatus()
    }

    private fun sendMessage(msg: String) {
        view.onMessageSent()
        RxBus.send(NeedToUpdateEvent())

        val message = QBHelper.createMessage(msg)

        execute(interactor.sendMessage(view.dialog, message), {
            if (view.dialog.isPrivate) updateSentMessage(message)
        }, { view.onMessageSendingError(it, msg) })
    }

    private fun updateSentMessage(message: QBChatMessage) {
        execute(interactor.getMessagesList(view.dialog)
            .map { it.first { it.body == message.body } }, {
            messageDb.add(Message(it))
            view.updateSentMessage(it)
        })
    }

    private fun observeMessages() {
        if (!Settings.system.isOnline) return
        view.addDisposable(interactor.observeIncomingMessages(view.dialog)
                .map { it.second }
                .subscribe({
                    messageDb.add(Message(it))
                    view.onNewMessageAdded(it)
                    if (it.isIncoming
                            && !it.readIds.contains(Settings.system.currentUserId)
                            && !Settings.system.isIncognito) view.dialog.readMessage(it)
                    view.onMessagesRead(it)
                    RxBus.send(NeedToUpdateEvent())
                }, { view.onRequestError(it) }))
    }

    private fun getElderMessages() {
        execute(interactor.getMessagesList(view.dialog, view.loadedMsgList.size + DIALOG_MESSAGES_LIMIT), {
            val ids = view.loadedMsgList.map { it.id }
            val list = it.filter { !ids.contains(it.id) }
            messageDb.addAll(*list.map { Message(it) }.toTypedArray())
            view.onMessagesLoaded(list, false)
        })
    }

    @Suppress("ProtectedInFinal")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected fun unBind() {
        view.viewModel.setMessages(view.loadedMsgList)
    }

    private fun observeMessagesStatus() {
        if (!Settings.system.isOnline) return
        view.addDisposable(interactor.observeMessageStatus(view.dialog.dialogId)
            .subscribe ({ view.onMessageStatusChanged(it.first.second) }, { view.onRequestError(it) }))
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    private fun readUnreadMessages(messages: List<QBChatMessage>) {
        if (Settings.system.isIncognito) return
        if (view.dialog?.unreadMessageCount ?: 0 > 0)
            RxBus.send(NeedToUpdateEvent())
        for (unreadMessage in messages.take(view.dialog.unreadMessageCount ?: 0)) {
            if (unreadMessage.isIncoming) {
                view.dialog.readMessage(unreadMessage)
            }
        }
    }
}