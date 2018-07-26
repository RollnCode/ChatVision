package chapters.signIn.layers

import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Observable
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.06
 */
interface EnterPhoneContract {

    interface View : AsyncMVPInterface {
        val countryTextObservable: Observable<String>
        val phoneTextObservable: Observable<String>

        fun setSignInButtonVisibility(visible: Boolean)
        fun onSignIn(result: SignInResult)
    }

    interface Interactor {
        fun signIn(phone: String): Single<SignInResult>
    }

    sealed class SignInResult {
        class ResultComplete(val credential: PhoneAuthCredential) : SignInResult()
        class ResultCodeSent(val verificationId: String, val token: ForceResendingToken) : SignInResult()
    }
}