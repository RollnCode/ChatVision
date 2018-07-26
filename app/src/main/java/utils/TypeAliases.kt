package utils

import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.14
 */
typealias ObservableMessage = Triple<String, QBChatMessage, Int> //dialogId, message, senderId

typealias ObservableMessageStatus = Triple<Pair<String, String>, String, Int> //Pair<status, messageId>, dialogId, userId

typealias Contact = Pair<String, String> //name, phone

typealias FullContact = Map.Entry<String, List<String>> //name, phone list

typealias ContactMap = Map<String, List<String>> //name, phone list

typealias DialogsList = List<QBChatDialog>