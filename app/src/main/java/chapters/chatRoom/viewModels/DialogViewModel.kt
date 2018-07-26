package chapters.chatRoom.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.12.05
 */
class DialogViewModel : ViewModel() {

    private val messageData = MutableLiveData<List<QBChatMessage>>()
    private val occupantsData = MutableLiveData<List<QBUser>>()

    val messages: List<QBChatMessage>
        get() = messageData.value ?: listOf()
    val users: List<QBUser>
        get() = occupantsData.value ?: listOf()

    fun setOccupants(list: List<QBUser>) {
        occupantsData.value = list
    }

    fun setMessages(list: List<QBChatMessage>) {
        messageData.value = list
    }

    override fun onCleared() {
        messageData.value = listOf()
        occupantsData.value = listOf()
    }
}