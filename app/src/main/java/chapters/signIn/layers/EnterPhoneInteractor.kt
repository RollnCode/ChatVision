package chapters.signIn.layers

import chapters.signIn.layers.EnterPhoneContract.SignInResult
import chapters.signIn.layers.EnterPhoneContract.SignInResult.ResultCodeSent
import chapters.signIn.layers.EnterPhoneContract.SignInResult.ResultComplete
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import instruments.AUTH_PROVIDER_TIMEOUT
import io.reactivex.Single
import storage.Settings
import java.util.concurrent.TimeUnit

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.26
 */
open class EnterPhoneInteractor : EnterPhoneContract.Interactor {

    override fun signIn(phone: String): Single<SignInResult>
            = Single.create<SignInResult> { emitter ->
        val callback = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                emitter.onSuccess(ResultComplete(credential))
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                Settings.system.phoneNumber = phone
                emitter.onSuccess(ResultCodeSent(verificationId, token))
            }

            override fun onVerificationFailed(ex: FirebaseException?) {
                emitter.onError(ex)
            }
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,                      // Phone number to verify
                AUTH_PROVIDER_TIMEOUT,      // Timeout duration
                TimeUnit.SECONDS,           // Unit of timeout
                TaskExecutors.MAIN_THREAD,  // Activity (for callback binding)
                callback)                   // OnVerificationStateChangedCallbacks
    }
}