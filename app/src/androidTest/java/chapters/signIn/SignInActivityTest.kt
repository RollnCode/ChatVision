package chapters.signIn

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView.ViewHolder
import com.rollncode.chatVision.R
import org.junit.Rule
import org.junit.Test



/**
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.01
 */
class SignInActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<SignInActivity>(
            SignInActivity::class.java)

    @Test
    fun test() {
        onView(withId(R.id.edtCountryLayout)).perform(click())
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))
        onView(withId(R.id.edtPhone)).perform(typeText("1"))
        onView(withId(R.id.edtPhone)).perform(typeText("2"))
        onView(withId(R.id.edtPhone)).perform(typeText("3"))
        onView(withId(R.id.edtPhone)).perform(typeText("4"))
        onView(withId(R.id.edtPhone)).perform(typeText("5"))
        onView(withId(R.id.edtPhone)).perform(typeText("6"))
        onView(withId(R.id.edtPhone)).perform(typeText("7"))
        onView(withId(R.id.action_ok)).perform(click())
    }

}