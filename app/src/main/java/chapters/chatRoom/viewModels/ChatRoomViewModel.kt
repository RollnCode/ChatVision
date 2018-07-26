package chapters.chatRoom.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import utils.ContactMap
import utils.DialogsList

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.12.05
 */
class ChatRoomViewModel : ViewModel() {

    private val chatData = MutableLiveData<DialogsList>()
    private val usersData = MutableLiveData<List<QBUser>>()
    private val contactData = MutableLiveData<ContactMap>()
    private val dialogData = MutableLiveData<QBChatDialog>()

    val chats: DialogsList
        get() = chatData.value ?: listOf()
    val users: List<QBUser>
        get() = usersData.value ?: listOf()
    val contacts: ContactMap
        get() = contactData.value ?: mapOf()
    val dialog: QBChatDialog
        get() = dialogData.value ?: QBChatDialog()

    fun setData(chats: DialogsList, users: List<QBUser>, contacts: ContactMap) {
        chatData.value = chats
        usersData.value = users
        contactData.value = contacts
    }

    fun setDialog(qbDialog: QBChatDialog) {
        dialogData.value = qbDialog
    }

    fun updateDialog(message: QBChatMessage? = null) {
        if (message != null) {
            dialogData.value?.lastMessageDateSent = message.dateSent
            dialogData.value?.lastMessage = message.body
        }
        dialogData.value?.unreadMessageCount = 0
    }

    override fun onCleared() {
        chatData.value = listOf()
        usersData.value = listOf()
        contactData.value = mapOf()
        dialogData.value = QBChatDialog()
    }
}