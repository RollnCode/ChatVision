package chapters.chatRoom.layers

import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import instruments.QBHelper
import instruments.STATE_READ
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import utils.ObservableMessage
import utils.ObservableMessageStatus

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
class DialogIneractor : DialogContracts.Interactor {
    override fun sendMessage(dialog: QBChatDialog, msg: QBChatMessage): Completable =
            QBHelper.sendMessage(dialog, msg)

    override fun observeIncomingMessages(dialog: QBChatDialog): Flowable<ObservableMessage> =
            QBHelper.observeIncomingMessages(dialog)

    override fun getMessagesList(dialog: QBChatDialog, limit: Int): Single<out List<QBChatMessage>> =
            QBHelper.getChatMessages(dialog, limit)

    override fun getDialogUsers(ids: List<Int>): Single<out List<QBUser>> =
            QBHelper.getUsersByIds(ids)

    override fun observeMessageStatus(dialogId: String): Flowable<ObservableMessageStatus> =
            QBHelper.observeMessagesStatus()
                .filter { it.second == dialogId && it.first.first == STATE_READ }
}