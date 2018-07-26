package chapters.signIn.layers

import chapters.signIn.layers.EnterPhoneContract.SignInResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import io.reactivex.Single
import storage.Settings

/**
 *
 * @author Sviatoslav Koliesnik kolesniksy@gmail.com
 * @since 2017.10.26
 */
class CodeInteractor : EnterPhoneInteractor(), CodeContract.Interactor {

    override fun checkCode(code: String, verificationId: String): Single<PhoneAuthCredential>
            = Single.just(PhoneAuthProvider.getCredential(verificationId, code))

    override fun resendCode(): Single<SignInResult> {
        return signIn(Settings.system.phoneNumber)
    }
}