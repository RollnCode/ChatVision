package chapters.chatRoom.layers

import com.quickblox.users.model.QBUser
import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Completable
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
interface LoginContracts {

    interface View : AsyncMVPInterface {
        fun onUserLogIn(isProfileFilled: Boolean)
    }

    interface Interactor {
        fun getUser(): Single<QBUser>
        fun logIn(user: QBUser): Completable
    }
}