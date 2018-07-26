package chapters.user

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import base.BFragment
import chapters.chatRoom.layers.ChatRoomRouter
import chapters.signIn.SignInActivity
import chapters.user.layers.ProfileContracts.View
import chapters.user.layers.ProfilePresenter
import com.quickblox.users.model.QBUser
import com.rollncode.basement.utility.extension.hide
import com.rollncode.basement.utility.extension.showAlert
import com.rollncode.basement.utility.extension.string
import com.rollncode.chatVision.R
import instruments.onClickObservable
import instruments.shortLogin
import instruments.textChangeObservable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_profile.*
import storage.Settings

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
class ProfileFragment : BFragment(), View {

    companion object {
        private const val NEED_TO_FILL_KEY = "NEED_TO_FILL_KEY"
        private const val USER_KEY = "USER_KEY"
        fun instance(isNeededToFill: Boolean = false): ProfileFragment {
            val frag = ProfileFragment()
            val args = Bundle()
            args.putBoolean(NEED_TO_FILL_KEY, isNeededToFill)
            frag.arguments = args
            return frag
        }

        fun instance(user: QBUser?): ProfileFragment {
            val frag = ProfileFragment()
            val args = Bundle()
            args.putSerializable(USER_KEY, user)
            frag.arguments = args
            return frag
        }
    }

    override val userNameObservable: Observable<String>
        get() = edtName.textChangeObservable()
            .startWith("")
    override val deleteUserObservable: Observable<Unit>
        get() = btnDelete.onClickObservable()
            .flatMap {
                var listener: () -> Unit = {}
                showAlert {
                    setTitle(R.string.delete_profile)
                    setMessage(R.string.delete_confirm)
                    setPositiveButton(R.string.btn_ok) { _, _ -> listener() }
                    setNegativeButton(R.string.btn_cancel, null)
                }
                Observable.create<Unit> { listener =  { it.onNext(Unit) } }
            }
    private val presenter by lazy { ProfilePresenter(this) }
    private val qbUser by lazy { arguments?.getSerializable(USER_KEY) as? QBUser }
    private var user = QBUser()
    private var menuApply: MenuItem? = null

    override fun getLayoutRes() =
            R.layout.fragment_profile

    override fun getMenuLayout() =
            if (qbUser == null) R.menu.menu_user else 0

    override fun onViewCreated(v: android.view.View, b: Bundle?) {
        super.onViewCreated(v, b)
        if (qbUser != null || Settings.system.isIncognito) {
            edtName.isEnabled = false
            edtPhone.isEnabled = false
            edtEmail.isEnabled = false
            btnDelete.hide()
        }
        qbUser?.let {
            onProfileLoaded(it)
            return
        }
        presenter.bind()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menuApply = menu?.findItem(R.id.action_ok)
        setButtonVisibility(!Settings.system.isIncognito && edtName.string.isNotBlank())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_ok -> presenter.updateProfile(user.apply {
                fullName = edtName.string
                email = edtEmail.string
                phone = edtPhone.string
            })
            R.id.action_logout -> presenter.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setButtonVisibility(visible: Boolean) {
        menuApply?.isVisible = visible
    }

    override fun onProfileLoaded(user: QBUser) {
        if (arguments?.getBoolean(NEED_TO_FILL_KEY, false) == true && !user.fullName.isNullOrBlank()) {
            (activity as? ChatRoomRouter)?.onUserLogIn(true)
            return
        }
        this.user = user
        activity?.title = user.shortLogin
        edtName.setText(user.fullName)
        edtEmail.setText(user.email)
        edtPhone.setText(user.phone) //todo maybe if null take it from login???
    }

    override fun onLogout() {
        Settings.system.isIncognito = false
        activity?.let { SignInActivity.start(it) } ?: finish()
    }
}