package chapters.signIn.layers

import android.util.Patterns
import base.BPresenter
import instruments.PHONE_NUMBER_MIN_LENGTH
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
class EnterPhonePresenter(private val view: EnterPhoneContract.View) : BPresenter(view) {

    private val interactor by lazy { EnterPhoneInteractor() }

    fun bind() {
        view.addDisposable(Observable.combineLatest(view.countryTextObservable, view.phoneTextObservable,
                BiFunction<String, String, Pair<String, String>> { code, phone -> Pair(code, phone) })
            .map { it.first.isNotBlank() && validatePhoneNumber(it.second) }
            .distinctUntilChanged()
            .subscribe{ view.setSignInButtonVisibility(it) })
    }

    @Suppress("MoveLambdaOutsideParentheses")
    fun onSighInPressed(code: String, phone: String) {
        val fullPhone = try {
            "${code.split("/")[1]}$phone"
        } catch (ex: Exception) {
            return
        }
        execute(interactor.signIn(fullPhone), { view.onSignIn(it) })
    }

    private fun validatePhoneNumber(phone: String): Boolean =
            Patterns.PHONE.matcher(phone).matches() && phone.trim().filter(Char::isDigit).length > PHONE_NUMBER_MIN_LENGTH
}