package chapters.chatRoom.layers

import android.arch.lifecycle.LifecycleOwner
import chapters.chatRoom.viewModels.ChatRoomViewModel
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.users.model.QBUser
import com.rollncode.basement.interfaces.AsyncMVPInterface
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import utils.Contact
import utils.ContactMap
import utils.DialogsList
import utils.ObservableMessage
import utils.ObservableMessageStatus

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
interface ChatRoomContracts {

    interface View : AsyncMVPInterface, LifecycleOwner {
        val contacts: List<Contact>
        val rxPermissions: RxPermissions
        val viewModel: ChatRoomViewModel
        val createPrivateDialogObservable: Observable<QBUser>
        val deleteDialogObservable: Observable<String>
        val refreshObservable: Observable<Unit>
        fun onContactsListLoaded(dialogs: DialogsList, users: List<QBUser>, contacts: ContactMap)
        fun onDialogCreated(dialog: QBChatDialog)
        fun updateDialog(dialog: QBChatDialog)
        fun onStatusChanged(drawableRes: Int, dialogId: String)
    }

    interface Interactor {
        fun getDialogs(): Single<out DialogsList>
        fun getUsers(): Single<List<QBUser>>
        fun createPrivateDialog(id: Int): Single<QBChatDialog>
        fun observeIncomingMessages(): Flowable<ObservableMessage>
        fun observeMessagesStatus(): Flowable<ObservableMessageStatus>
        fun getDialog(id: String): Single<QBChatDialog>
        fun deleteDialog(dialogId: String): Completable
    }
}