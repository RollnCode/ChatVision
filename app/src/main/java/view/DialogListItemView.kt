package view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.support.annotation.DrawableRes
import android.support.constraint.ConstraintLayout
import android.view.ViewGroup
import com.quickblox.chat.model.QBChatDialog
import com.rollncode.basement.utility.extension.getScreenSize
import com.rollncode.basement.utility.extension.hide
import com.rollncode.basement.utility.extension.show
import com.rollncode.chatVision.R
import kotlinx.android.synthetic.main.view_dialog_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2017.12.26
 */
class DialogListItemView(context: Context) : ConstraintLayout(context) {

    init {
        inflate(context, R.layout.view_dialog_list_item, this)
        val padding = context.resources.getDimensionPixelOffset(R.dimen.general_mid)
        setPaddingRelative(padding, padding, padding, 0)
        setBackgroundResource(R.drawable.selector_back_blue)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.width = LayoutParams.MATCH_PARENT
        params?.height = context.getScreenSize().y / 7
        super.setLayoutParams(params)
    }

    fun setData(dialog: QBChatDialog) {
        tvDialogName.text = dialog.name
        tvLastMessage.text = dialog.lastMessage

        @DrawableRes val imageStatus: Int
        val numberOfUnreadMessages = dialog.unreadMessageCount

        when {
            dialog.lastMessage == null -> {
                imageStatus = 0
                tvUnreadMessages.hide()

            }
            numberOfUnreadMessages == 0 -> {
                tvUnreadMessages.hide()
                imageStatus = R.drawable.svg_read

            }
            else -> {
                tvUnreadMessages.text = numberOfUnreadMessages.toString()
                tvUnreadMessages.show()
                imageStatus = R.drawable.svg_delivered
            }
        }
        ivMessageStatus.setImageResource(imageStatus)

        var lastMessageTime = dialog.lastMessageDateSent

        if (lastMessageTime != 0L) {
            lastMessageTime *= 1000

            val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
            var time = formatter.format(Date(lastMessageTime))

            if (formatter.format(Date()) == time) {
                formatter.applyPattern("HH:mm")
                time = formatter.format(Date(lastMessageTime))
            }
            tvTime.text = time
        }
    }

    fun updateStatusImage(statusDrawable: Drawable?) {
        val transitionDrawable = TransitionDrawable(arrayOf(ivMessageStatus.drawable, statusDrawable))
        transitionDrawable.isCrossFadeEnabled = true
        ivMessageStatus.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(500)
    }
}