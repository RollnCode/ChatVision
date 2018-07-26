package chapters.chatRoom.layers

import base.BPresenter
import com.quickblox.core.exception.QBResponseException
import instruments.toLog
import storage.Settings

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
class LoginPresenter(view: LoginContracts.View) : BPresenter(view) {

    init {
        if (Settings.system.isOnline) {
            val interactor = LoginInteractor()
            execute(interactor.getUser()
                    .flatMap {
                        interactor.logIn(it).toSingleDefault(!it.fullName.isNullOrBlank())
                    },
                    { view.onUserLogIn(it) }, { view.onRequestError(it); toLog("error ${it.message}, ${(it as? QBResponseException)?.errors?.joinToString()}, ${(it as? QBResponseException)?.httpStatusCode}") })
        } else {
            view.onUserLogIn(true)
        }
    }
}