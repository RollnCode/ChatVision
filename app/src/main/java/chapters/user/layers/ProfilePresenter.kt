package chapters.user.layers

import application.ChatApp
import base.BPresenter
import com.quickblox.users.model.QBUser
import instruments.USER_NAME_MIN_LENGTH
import storage.Settings

@Suppress("MoveLambdaOutsideParentheses")
/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
class ProfilePresenter(private val view: ProfileContracts.View) : BPresenter(view) {

    private val interactor = ProfileInteractor()

    fun bind() {
        execute(interactor.getProfile(), { view.onProfileLoaded(it) })
        if (Settings.system.isIncognito) return
        view.addDisposable(view.userNameObservable
            .map(::validateName)
            .distinctUntilChanged()
            .subscribe { view.setButtonVisibility(it) })
        view.addDisposable(view.deleteUserObservable
            .subscribe { execute(interactor.deleteProfile(), {
                ChatApp.dataBase.clear()
                view.onLogout()
            }) })
    }

    fun updateProfile(user: QBUser) {
        execute(interactor.updateProfile(user), { view.onProfileLoaded(it) })
    }

    fun logout() {
        execute(interactor.logout(), {
            ChatApp.dataBase.clear()
            view.onLogout()
        })
    }

    private fun validateName(name: String): Boolean =
            name.trim().length >= USER_NAME_MIN_LENGTH
}