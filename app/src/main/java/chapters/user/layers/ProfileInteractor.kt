package chapters.user.layers

import com.quickblox.users.model.QBUser
import instruments.QBHelper
import io.reactivex.Completable
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
class ProfileInteractor : ProfileContracts.Interactor {

    override fun getProfile(): Single<QBUser> =
            QBHelper.getCurrentUser()

    override fun updateProfile(user: QBUser): Single<QBUser> =
            QBHelper.updateUser(user)

    override fun logout(): Completable {
        return if (QBHelper.isUserLoggedIn) QBHelper.logOut().andThen(QBHelper.signOut())
        else QBHelper.signOut()
    }

    override fun deleteProfile(): Completable {
        return QBHelper.deleteUser()
    }
}