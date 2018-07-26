package view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.ViewGroup
import com.rollncode.chatVision.R

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.05.03
 */
class NoDialogItemView(context: Context, attributeSet: AttributeSet? = null) : ConstraintLayout(context, attributeSet) {

    init {
        inflate(context, R.layout.view_no_dialog, this)
        setBackgroundResource(R.drawable.selector_back_blue)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.width = LayoutParams.MATCH_PARENT
        params?.height = LayoutParams.WRAP_CONTENT
        super.setLayoutParams(params)
    }

}