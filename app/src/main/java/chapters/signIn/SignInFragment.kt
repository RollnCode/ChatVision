package chapters.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import base.BFragment
import chapters.signIn.layers.SignInContract
import chapters.signIn.layers.SignInPresenter
import chapters.signIn.layers.SignInRouter
import com.google.firebase.auth.PhoneAuthCredential

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.31
 */
class SignInFragment : BFragment(), SignInContract.View {

    companion object {
        private const val CREDENTIAL_KEY = "CREDENTIAL_KEY"
        fun instance(credential: PhoneAuthCredential): SignInFragment {
            val frag = SignInFragment()
            val args = Bundle()
            args.putParcelable(CREDENTIAL_KEY, credential)
            frag.arguments = args
            return frag
        }
    }

    private val presenter = SignInPresenter(this)
    private val signInRouter by lazy { activity as? SignInRouter }

    override fun getLayoutRes() = 0
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, b: Bundle?): View? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.signIn(arguments?.getParcelable(CREDENTIAL_KEY))
    }

    override fun onSignInComplete() {
        signInRouter?.onSignIn()
    }
}