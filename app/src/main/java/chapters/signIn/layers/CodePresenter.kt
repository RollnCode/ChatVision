package chapters.signIn.layers

import base.BPresenter
import chapters.signIn.layers.EnterPhoneContract.SignInResult.ResultCodeSent
import instruments.CODE_RESENT_TIMEOUT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@Suppress("MoveLambdaOutsideParentheses")
/**
 *
 * @author Sviatoslav Koliesnik kolesniksy@gmail.com
 * @since 2017.10.26
 */
class CodePresenter(private val view: CodeContract.View) : BPresenter(view) {

    companion object {
        private const val CODE_LENGTH = 6
    }

    private val interactor by lazy { CodeInteractor() }

    fun bind() {
        view.addDisposable(view.codeTextObservable
            .map { validateCode(it) }
            .distinctUntilChanged()
            .subscribe { view.setMenuVisibility(it) })
        startResentCountDown()
    }

    private fun validateCode(code: String)
            = code.length == CODE_LENGTH

    private fun startResentCountDown() {
        view.addDisposable(Observable.merge(Observable.interval(CODE_RESENT_TIMEOUT, TimeUnit.SECONDS).map { true },
                view.codeTextObservable.map { it.isEmpty() })
            .firstOrError()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ view.setOkOrResend(it) }, { }))
    }

    fun onCheckCodePressed(code: String, verificationId: String) {
        execute(interactor.checkCode(code, verificationId), { view.onCheckCode(it) })
    }

    fun onResendPressed() {
        execute(interactor.resendCode(), {
            view.onResendCode(it is ResultCodeSent)
            startResentCountDown()
        })
    }
}