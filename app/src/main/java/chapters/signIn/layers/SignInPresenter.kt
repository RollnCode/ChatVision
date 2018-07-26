package chapters.signIn.layers

import base.BPresenter
import com.google.firebase.auth.PhoneAuthCredential
import storage.Settings
import utils.UnknownException

@Suppress("MoveLambdaOutsideParentheses")
/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.31
 */
class SignInPresenter(private val view: SignInContract.View) : BPresenter(view) {

    private val interactor: SignInContract.Interactor = SignInInteractor()

    fun signIn(credential: PhoneAuthCredential?) {
        credential ?: return
        execute(interactor.signIn(credential), {
            if (it.isSuccessful) {
                val user = it.result.user
                execute(interactor.checkUser(user.phoneNumber), {
                    Settings.system.currentUserId = it.id
                    view.onSignInComplete() })
            } else {
                view.onRequestError(it.exception ?: UnknownException())
            }
        })
    }
}