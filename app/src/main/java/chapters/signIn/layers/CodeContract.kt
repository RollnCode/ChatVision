package chapters.signIn.layers

import chapters.signIn.layers.EnterPhoneContract.SignInResult
import com.google.firebase.auth.PhoneAuthCredential
import com.rollncode.basement.interfaces.AsyncMVPInterface
import io.reactivex.Observable
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.06
 */
interface CodeContract {

    interface View : AsyncMVPInterface {
        val codeTextObservable: Observable<String>

        fun setOkOrResend(isResend: Boolean)
        fun onCheckCode(credential: PhoneAuthCredential)
        fun onResendCode(isSuccess: Boolean)
        fun setMenuVisibility(isCodeValid: Boolean)
    }

    interface Interactor : EnterPhoneContract.Interactor {
        fun checkCode(code: String, verificationId: String): Single<PhoneAuthCredential>
        fun resendCode(): Single<SignInResult>
    }
}