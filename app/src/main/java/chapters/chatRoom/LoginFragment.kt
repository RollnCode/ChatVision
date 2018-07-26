package chapters.chatRoom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import base.BFragment
import chapters.chatRoom.layers.ChatRoomRouter
import chapters.chatRoom.layers.LoginContracts.View
import chapters.chatRoom.layers.LoginPresenter

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.15
 */
class LoginFragment : BFragment(), View {

    override fun getLayoutRes() = 0
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, b: Bundle?): android.view.View? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LoginPresenter(this)
    }

    override fun onUserLogIn(isProfileFilled: Boolean) {
        (activity as? ChatRoomRouter)?.onUserLogIn(isProfileFilled)
    }
}