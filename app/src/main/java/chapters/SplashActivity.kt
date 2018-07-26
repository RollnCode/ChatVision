package chapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.animation.OvershootInterpolator
import chapters.chatRoom.ChatRoomActivity
import chapters.signIn.SignInActivity
import com.rollncode.basement.utility.extension.showAlert
import com.rollncode.chatVision.R
import instruments.QBHelper
import kotlinx.android.synthetic.main.activity_splash.*
import storage.Settings

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        imgLogo.run {
            alpha = 0.3F
            scaleX = 0F
            scaleY = 0F
        }
        tvTitle.alpha = 0F
        animateLogo()
    }

    private fun animateLogo() {
        imgLogo.animate()
            .alpha(1F)
            .scaleX(1F)
            .scaleY(1F)
            .setDuration(1200)
            .setStartDelay(100)
            .setInterpolator(OvershootInterpolator())
            .start()
        tvTitle.animate()
            .alpha(1F)
            .setDuration(1200)
            .setStartDelay(100)
            .setInterpolator(OvershootInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    Handler().postDelayed({ startApp() }, 1000)
                }
            })
            .start()
    }

    private fun startApp() {
        when {
            !Settings.system.isPolicyConfirmed -> {
                showAlert {
                    setTitle(R.string.privacy_title)
                    setMessage(R.string.privacy_policy)
                    setPositiveButton(R.string.btn_accept) { _, _ ->
                        Settings.system.isPolicyConfirmed = true
                        startApp()
                    }
                    setNegativeButton(R.string.btn_decline) { _, _ -> finish() }
                    setNeutralButton(R.string.btn_read) { _, _ -> startActivityForResult(Intent(this@SplashActivity, PrivacyPolicyActivity::class.java), 0) }
                }
            }
            QBHelper.isUserSignIn || !Settings.system.isOnline -> ChatRoomActivity.start(this)
            else -> SignInActivity.start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) startApp()
    }
}