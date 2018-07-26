package view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.Gravity
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import com.rollncode.basement.utility.extension.getColorCompat
import com.rollncode.basement.utility.extension.hide
import com.rollncode.basement.utility.extension.show
import com.rollncode.chatVision.R
import instruments.isIncoming
import instruments.userName
import kotlinx.android.synthetic.main.view_dialog_item.view.*
import storage.Settings
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.17
 */
class DialogItemView(context: Context, attributeSet: AttributeSet? = null) : ConstraintLayout(context, attributeSet) {

    init {
        inflate(context, R.layout.view_dialog_item, this)
    }

    override fun setLayoutParams(params: android.view.ViewGroup.LayoutParams?) {
        params?.width = LayoutParams.MATCH_PARENT
        params?.height = LayoutParams.WRAP_CONTENT
        super.setLayoutParams(params)
    }

    @SuppressLint("SetTextI18n")
    fun setMessage(message: QBChatMessage, sender: QBUser, isSameSender: Boolean = false) {
        val (img, tvMessage, hideViews) =
                if (message.isIncoming) Triple(imgCompanionAvatar, tvInMessage, listOf(imgMyAvatar, tvOutMessage))
                else {
                    Triple(imgMyAvatar, tvOutMessage, listOf(imgCompanionAvatar, tvInMessage))
                }
        if (message.readIds?.any { it != Settings.system.currentUserId } == true || message.isIncoming) {
            imgRead.hide()
        } else {
            imgRead.show()
        }

        hideViews.forEach { it.hide() }
        if (isSameSender) {
            rootLayout.setPaddingRelative(0, 0, 0, 0)
            img.hide(false)
            tvSender.hide()
        } else {
            rootLayout.setPaddingRelative(0, context.resources.getDimensionPixelOffset(R.dimen.general_mid), 0, 0)
            img.show()
            val iconColor = if (sender.login == null) R.color.gray_light else R.color.colorAccent
            img.setColorFilter(context.getColorCompat(iconColor), PorterDuff.Mode.SRC_IN)
            tvSender.show()
            tvSender.gravity = if (message.isIncoming) Gravity.START else Gravity.END
            val date = SimpleDateFormat("H:mm d.MM.yy", Locale.getDefault()).format(Date(message.dateSent * 1000))
            tvSender.text = "${sender.userName} $date"
        }
        tvMessage.run {
            show()
            text = message.body
        }
    }
}