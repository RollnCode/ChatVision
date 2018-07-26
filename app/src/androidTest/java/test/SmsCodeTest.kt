package test

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.Toolbar
import chapters.signIn.CodeFragment
import chapters.signIn.SignInActivity
import com.rollncode.chatVision.R
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 *
 * @author Sviatoslav Koliesnik kolesniksy@gmail.com
 * @since 2017.11.06
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SmsCodeTest {

    @get:Rule
    val activityRule = ActivityTestRule<SignInActivity>(SignInActivity::class.java) // activityRule need to be public
    private val toolBar: Toolbar by lazy { activityRule.activity.findViewById<Toolbar>(R.id.toolBar) }

    @Before
    fun init() {
        activityRule.activity.start(CodeFragment.instance("123")) // launch CodeFragment
        activityRule.activity.onCreateOptionsMenu(toolBar.menu)
    }

    /**
     * Type code, check "OK" visibility; clear code, check "OK" visibility
     */
    @Test
    fun checkForInputCode() {
        val edtCode = onView(withId(R.id.edtCode))
        val menuActionOk = toolBar.menu.findItem(R.id.action_ok) // get actionOk menu item

        edtCode.perform(typeText("25783")) // put fake code into edtCode
        Assert.assertEquals(menuActionOk.isVisible, true) // check if actionOk is visible

        edtCode.perform(clearText()) // clear code value
        Assert.assertNotEquals(menuActionOk.isVisible, true) // check if actionOk is not visible (the same as assertEquals(..., false))
    }

    /**
     * Start fragment, wait for resend and check menu items visibility when code is empty
     */
    @Test
    fun checkResendItemWhenEmpty() {
        checkResendItem("", true) // clear code, show "resend"
    }

    /**
     * Start fragment, wait for resend and check menu items visibility when code is not empty
     */
    @Test
    fun checkResendItemWhenNotEmpty() {
        checkResendItem("25738", false) // input code, do not show "resend"
    }

    private fun checkResendItem(code: String, resendVisible: Boolean) {
        onView(withId(R.id.edtCode)).perform(typeText(code))

        TimeUnit.SECONDS.sleep(5)
        Assert.assertEquals(toolBar.menu.findItem(R.id.action_resend).isVisible, resendVisible)
        Assert.assertEquals(toolBar.menu.findItem(R.id.action_ok).isVisible, !resendVisible)
    }
}