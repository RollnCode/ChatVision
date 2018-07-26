package chapters.chatRoom.layers

import com.quickblox.chat.model.QBChatDialog
import com.quickblox.users.model.QBUser

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
interface ChatRoomRouter {
    fun onDialogSelected(dialog: QBChatDialog)
    fun onPhoneContactSelected(contact: Map.Entry<String, List<String>>)
    fun onUserLogIn(isProfileFilled: Boolean)
    fun showUserInfo(user: QBUser?)
    fun createNewDialog(users: List<QBUser>)
}