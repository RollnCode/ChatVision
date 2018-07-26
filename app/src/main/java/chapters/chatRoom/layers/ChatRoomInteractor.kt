package chapters.chatRoom.layers

import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBDialogType.PRIVATE
import com.quickblox.users.model.QBUser
import instruments.QBHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import utils.DialogsList
import utils.ObservableMessage
import utils.ObservableMessageStatus

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
class ChatRoomInteractor : ChatRoomContracts.Interactor {

    override fun getDialogs(): Single<out DialogsList> =
            QBHelper.getDialogs()

    override fun getUsers(): Single<List<QBUser>> =
            QBHelper.getAllUsers()

    override fun createPrivateDialog(id: Int): Single<QBChatDialog> =
            QBHelper.createDialog(listOf(id), "", PRIVATE)

    override fun observeIncomingMessages(): Flowable<ObservableMessage> = QBHelper.observeIncomingMessages()

    override fun getDialog(id: String): Single<QBChatDialog> = QBHelper.getDialog(id)

    override fun observeMessagesStatus(): Flowable<ObservableMessageStatus> = QBHelper.observeMessagesStatus()

    override fun deleteDialog(dialogId: String): Completable =
            QBHelper.deleteDialog(dialogId)
}