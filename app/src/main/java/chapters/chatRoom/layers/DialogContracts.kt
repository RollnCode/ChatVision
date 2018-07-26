package chapters.chatRoom.layers

import android.arch.lifecycle.LifecycleOwner
import chapters.chatRoom.viewModels.DialogViewModel
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import com.rollncode.basement.interfaces.AsyncMVPInterface
import instruments.DIALOG_MESSAGES_LIMIT
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import utils.ObservableMessage
import utils.ObservableMessageStatus

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
interface DialogContracts {

    interface View : AsyncMVPInterface, LifecycleOwner {
        val dialog: QBChatDialog
        val getMoreMessagesTrigger: Observable<String>
        val sendMessageObservable: Observable<String>
        val loadedMsgList: List<QBChatMessage>
        val viewModel: DialogViewModel
        fun onUsersLoaded(list: List<QBUser>)
        fun onMessagesLoaded(list: List<QBChatMessage>, refresh: Boolean = true)
        fun onNewMessageAdded(msg: QBChatMessage)
        fun onMessageSent()
        fun onMessageSendingError(err: Throwable, msg: String)
        fun setSendingFormVisibility(isVisible: Boolean)
        fun onMessagesRead(message: QBChatMessage? = null)
        fun onMessageStatusChanged(messageId: String)
        fun updateSentMessage(message: QBChatMessage)
    }

    interface Interactor {
        fun getMessagesList(dialog: QBChatDialog, limit: Int = DIALOG_MESSAGES_LIMIT): Single<out List<QBChatMessage>>
        fun getDialogUsers(ids: List<Int>): Single< out List<QBUser>>
        fun sendMessage(dialog: QBChatDialog, msg: QBChatMessage): Completable
        fun observeIncomingMessages(dialog: QBChatDialog): Flowable<ObservableMessage>
        fun observeMessageStatus(dialogId: String): Flowable<ObservableMessageStatus>
    }
}