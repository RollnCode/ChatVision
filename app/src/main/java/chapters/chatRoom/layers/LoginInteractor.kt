package chapters.chatRoom.layers

import com.quickblox.users.model.QBUser
import instruments.QBHelper
import io.reactivex.Completable
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
class LoginInteractor : LoginContracts.Interactor {

    override fun getUser(): Single<QBUser> =
            QBHelper.getCurrentUser()

    override fun logIn(user: QBUser): Completable =
            QBHelper.logIn(user)
}