package chapters.signIn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import base.BActivity
import chapters.chatRoom.ChatRoomActivity
import chapters.signIn.layers.SignInRouter
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.rollncode.chatVision.R
import kotlinx.android.synthetic.main.activity_sign_in.*

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
class SignInActivity : BActivity(), SignInRouter {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, SignInActivity::class.java)
            intent.flags += android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags += android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(toolBar)
        start(EnterPhoneFragment.instance(), false)
    }

    override fun getContainerId()
            = R.id.flContainer

    override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
        start(CodeFragment.instance(verificationId), true)
    }

    override fun onGetCredential(credential: PhoneAuthCredential) {
        supportFragmentManager?.beginTransaction()
            ?.add(SignInFragment.instance(credential), "")
            ?.commitAllowingStateLoss()
    }

    override fun onSignIn() {
        ChatRoomActivity.start(this)
    }
}