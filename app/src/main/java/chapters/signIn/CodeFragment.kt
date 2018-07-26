package chapters.signIn

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import base.BFragment
import chapters.signIn.layers.CodeContract
import chapters.signIn.layers.CodePresenter
import chapters.signIn.layers.SignInRouter
import com.google.firebase.auth.PhoneAuthCredential
import com.rollncode.basement.utility.extension.string
import com.rollncode.chatVision.R
import instruments.textChangeObservable
import kotlinx.android.synthetic.main.fragment_code.*

/**
 *
 * @author Sviatoslav Koliesnik kolesniksy@gmail.com
 * @since 2017.10.27
 */
class CodeFragment : BFragment(), CodeContract.View {

    companion object {
        private const val VERIFICATION_ID_KEY = "VERIFICATION_ID_KEY"
        fun instance(verificationId: String): CodeFragment {
            val args = Bundle()
            args.putString(VERIFICATION_ID_KEY, verificationId)
            val frag = CodeFragment()
            frag.arguments = args
            return frag
        }
    }

    override val codeTextObservable
        get() = edtCode.textChangeObservable()

    private val presenter by lazy { CodePresenter(this) }
    private val signInRouter by lazy { activity as? SignInRouter }
    private var menuOk: MenuItem? = null
    private var menuResend: MenuItem? = null

    override fun getLayoutRes() = R.layout.fragment_code

    override fun getMenuLayout() = R.menu.menu_resend

    override fun onViewCreated(v: View, b: Bundle?) {
        super.onViewCreated(v, b)
        presenter.bind()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menuOk = menu?.findItem(R.id.action_ok)
        menuResend = menu?.findItem(R.id.action_resend)
        hideMenus()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_ok     -> presenter.onCheckCodePressed(edtCode.string, arguments?.getString(VERIFICATION_ID_KEY)?:"")
            R.id.action_resend -> {
                hideMenus()
                presenter.onResendPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setOkOrResend(isResend: Boolean) {
//        menuOk?.isVisible = !isResend
        menuResend?.isVisible = isResend
    }

    override fun onCheckCode(credential: PhoneAuthCredential) {
        signInRouter?.onGetCredential(credential)
    }

    override fun onResendCode(isSuccess: Boolean) { }

    private fun hideMenus() {
        menuOk?.isVisible = false
        menuResend?.isVisible = false
    }

    override fun setMenuVisibility(isCodeValid: Boolean) {
        menuOk?.isVisible = isCodeValid
        if (menuResend?.isVisible == true) menuResend?.isVisible = false
    }
}