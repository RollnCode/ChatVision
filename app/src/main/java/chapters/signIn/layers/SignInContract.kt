package chapters.signIn.layers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.quickblox.users.model.QBUser
import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.06
 */
interface SignInContract {

    interface View : AsyncMVPInterface {
        fun onSignInComplete()
    }

    interface Interactor {
        fun signIn(credential: PhoneAuthCredential): Single<Task<AuthResult>>
        fun checkUser(phone: String?): Single<QBUser>
    }
}