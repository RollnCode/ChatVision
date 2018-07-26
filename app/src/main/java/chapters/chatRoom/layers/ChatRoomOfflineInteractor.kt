package chapters.chatRoom.layers

import com.quickblox.chat.model.QBChatDialog
import com.quickblox.users.model.QBUser
import instruments.dialogDb
import instruments.userDb
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import utils.DialogsList
import utils.ObservableMessage
import utils.ObservableMessageStatus

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.27
 */
class ChatRoomOfflineInteractor : ChatRoomContracts.Interactor {

    override fun getDialogs(): Single<out DialogsList> {
        return dialogDb.getDialogsAsync()
            .map { it.map { it.toQBChatDialog() } }
    }

    override fun getUsers(): Single<List<QBUser>> {
        return userDb.getUsersAsync()
            .map { it.map { it.toQBUser() } }
    }

    override fun createPrivateDialog(id: Int): Single<QBChatDialog> {
        return Single.just(QBChatDialog())
    }

    //// methods below mustn't be called in offline mode
    override fun observeIncomingMessages(): Flowable<ObservableMessage> {
        TODO("Method mustn't be called in offline") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observeMessagesStatus(): Flowable<ObservableMessageStatus> {
        TODO("Method mustn't be called in offline") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDialog(id: String): Single<QBChatDialog> {
        TODO("Method mustn't be called in offline") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteDialog(dialogId: String): Completable {
        TODO("Method mustn't be called in offline") //To change body of created functions use File | Settings | File Templates.
    }
}