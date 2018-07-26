package chapters.user.layers

import com.quickblox.users.model.QBUser
import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
interface ProfileContracts {

    interface View : AsyncMVPInterface {
        val userNameObservable: Observable<String>
        val deleteUserObservable: Observable<Unit>
        fun setButtonVisibility(visible: Boolean)
        fun onProfileLoaded(user: QBUser)
        fun onLogout()
    }

    interface Interactor {
        fun getProfile(): Single<QBUser>
        fun updateProfile(user: QBUser): Single<QBUser>
        fun logout(): Completable
        fun deleteProfile(): Completable
    }
}