package chapters.chatRoom

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatDelegate
import base.BActivity
import chapters.chatRoom.layers.ChatRoomRouter
import chapters.user.ProfileFragment
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.users.model.QBUser
import com.rollncode.basement.utility.extension.getDrawableCompat
import com.rollncode.basement.utility.extension.show
import com.rollncode.basement.utility.extension.showAlert
import com.rollncode.chatVision.R
import kotlinx.android.synthetic.main.activity_sign_in.*
import storage.Settings
import utils.OfflineException
import kotlin.collections.Map.Entry

/**
 *
 * @author Sviatoslav Koliesnik kolesniksy@gmail.com
 * @since 2017.11.07
 */
class ChatRoomActivity : BActivity(), ChatRoomRouter {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        fun start(activity: Activity) {
            val intent = Intent(activity, ChatRoomActivity::class.java)
            intent.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(toolBar)
        initToolbar(false)
        if (!Settings.system.isOnline) tvOffline.show()
        start(LoginFragment(), false)
    }

    private fun initToolbar(isProfile: Boolean) {
        toolBar.navigationIcon = getDrawableCompat(if (isProfile) R.drawable.svg_arrow_back else R.drawable.svg_profile)
        toolBar.setNavigationOnClickListener {
            if (isProfile) {
                onBackPressed()
            } else {
                if (!Settings.system.isOnline) onRequestError(OfflineException())
                else start(ProfileFragment.instance())
            }
        }
    }

    override fun getContainerId()
            = R.id.flContainer

    override fun onUserLogIn(isProfileFilled: Boolean) {
        if (isProfileFilled) {
            start(ChatRoomsFragment.instance(), false)
        } else {
            start(ProfileFragment.instance(true), false)
        }
    }

    override fun onDialogSelected(dialog: QBChatDialog) {
        start(ChatDialogFragment.instance(dialog))
    }

    override fun onPhoneContactSelected(contact: Entry<String, List<String>>) {
        showAlert {
            setTitle(R.string.send_sms_title)
            setItems(contact.value.toTypedArray()) { _, pos ->
                sendSms(contact.key, contact.value[pos])
            }
        }
    }

    override fun showUserInfo(user: QBUser?) {
        start(ProfileFragment.instance(user))
    }

    override fun createNewDialog(users: List<QBUser>) {
        start(CreateChatFragment.instance(users as ArrayList<QBUser>))
    }

    private fun sendSms(number: String, name: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
                .putExtra("sms_body", getString(R.string.sms_invitation_format, name)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            showAlert {
                setMessage(ex.message)
            }
        }
    }

    override fun start(fragment: Fragment, addToBackStack: Boolean, block: (FragmentTransaction.() -> Unit)?) {
        super.start(fragment, addToBackStack, block)
        initToolbar(fragment is ProfileFragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        initToolbar(getFragmentFromContainer() is ProfileFragment)
    }
}