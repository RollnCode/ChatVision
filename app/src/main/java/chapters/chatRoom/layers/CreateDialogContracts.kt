package chapters.chatRoom.layers

import android.arch.lifecycle.LifecycleOwner
import com.quickblox.chat.model.QBChatDialog
import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Observable
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.05.03
 */
interface CreateDialogContracts {

    interface View : AsyncMVPInterface, LifecycleOwner {
        val createObservable: Observable<CreateDialogData>
        fun onDialogCreated()
    }

    interface Interactor {
        fun createDialog(ids: List<Int>, title: String, isPublic: Boolean = false, img: String = ""): Single<QBChatDialog>
    }

    class CreateDialogData(val ids: List<Int>, val title: String, val isPublic: Boolean = false, val img: String = "")
}