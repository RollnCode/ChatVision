package chapters.chatRoom.layers

import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBDialogType.GROUP
import com.quickblox.chat.model.QBDialogType.PUBLIC_GROUP
import instruments.QBHelper
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.05.03
 */
class CreateDialogIneractor : CreateDialogContracts.Interactor {
    override fun createDialog(ids: List<Int>, title: String, isPublic: Boolean, img: String): Single<QBChatDialog> =
            QBHelper.createDialog(ids, title, if (isPublic) PUBLIC_GROUP else GROUP, img)
}