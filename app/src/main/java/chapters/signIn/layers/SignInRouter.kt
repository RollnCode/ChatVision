package chapters.signIn.layers

import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.26
 */
interface SignInRouter {
    fun onCodeSent(verificationId: String, token: ForceResendingToken)
    fun onGetCredential(credential: PhoneAuthCredential)
    fun onSignIn()
}