package base

import android.os.Bundle
import com.rollncode.basement.base.BaseActivity
import com.rollncode.basement.utility.Utils
import com.rollncode.basement.utility.extension.hide
import com.rollncode.basement.utility.extension.isVisible
import com.rollncode.basement.utility.extension.show
import com.rollncode.basement.utility.extension.showAlert
import com.rollncode.basement.utility.snackbarFlowAction
import com.rollncode.chatVision.R
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_sign_in.*
import storage.Settings

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
abstract class BActivity : BaseActivity() {

    private var startedProgresses = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(if (Settings.system.isIncognito) R.style.AppTheme_Dark else R.style.AppTheme_Light)
    }

    override fun setProgress(start: Boolean) {
        if (start) startedProgresses++ else startedProgresses--
        runOnUiThread {
            progress ?: return@runOnUiThread
            if (start && !progress.isVisible) {
                progress.show()
                startedProgresses = 1
            } else if (!start && startedProgresses < 1 && progress.isVisible) {
                progress.hide()
            }
        }
    }

    override fun onRequestError(error: Throwable) {
        showAlert {
            setMessage(error.message ?: getString(R.string.error_unknown))
            setPositiveButton(R.string.btn_ok, null)
            setCancelable(false)
        }
    }

    override fun getRetryFlowAction(): Flowable<Boolean> =
            Utils.snackbarFlowAction(currentFocus, R.string.error_connection_fail, R.string.action_retry)

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }
}