package chapters.chatRoom.layers

import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import instruments.messageDb
import instruments.userDb
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import utils.ObservableMessage
import utils.ObservableMessageStatus

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.30
 */
class DialogOfflineInteractor : DialogContracts.Interactor {
    override fun sendMessage(dialog: QBChatDialog, msg: QBChatMessage): Completable =
            Completable.complete()

    override fun observeIncomingMessages(dialog: QBChatDialog): Flowable<ObservableMessage> =
            Flowable.empty()

    override fun getMessagesList(dialog: QBChatDialog, limit: Int): Single<out List<QBChatMessage>> =
            messageDb.getDialogMessagesAsync(dialog.dialogId).map {
                it.map { it.toChatMessage() }.sortedByDescending { it.dateSent }.take(limit)
            }

    override fun getDialogUsers(ids: List<Int>): Single<out List<QBUser>> =
        userDb.getUsersByIds(ids).map { it.map { it.toQBUser() } }

    override fun observeMessageStatus(dialogId: String): Flowable<ObservableMessageStatus> =
            Flowable.empty()

}