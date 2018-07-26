package adapters

import adapters.SimpleUsersAdapter.UserModel.UserTextView
import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModelWithView
import com.quickblox.users.model.QBUser
import com.rollncode.basement.utility.extension.getColorCompat
import com.rollncode.basement.utility.extension.getDrawableCompat
import com.rollncode.chatVision.R
import instruments.userName

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.05.04
 */
class SimpleUsersAdapter(users: List<QBUser>) : EpoxyAdapter() {

    init {
        removeAllModels()
        addModels(users.map { UserModel(it) })
    }

    inner class UserModel(private val user: QBUser) : EpoxyModelWithView<UserTextView>() {

        override fun buildView(parent: ViewGroup): UserTextView =
                UserTextView(parent.context)

        override fun bind(view: UserTextView) {
            view.text = user.userName
        }

        inner class UserTextView(context: Context) : TextView(context) {

            init {
                setTextColor(context.getColorCompat(R.color.black))
                val padding = context.resources.getDimensionPixelOffset(R.dimen.general_x_small)
                setPaddingRelative(padding, padding, padding, padding)
                setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawableCompat(R.drawable.svg_profile_gray), null, null, null)
            }

            override fun setLayoutParams(params: LayoutParams?) {
                super.setLayoutParams(params)
                params?.width = LayoutParams.MATCH_PARENT
                params?.height = LayoutParams.WRAP_CONTENT
            }
        }
    }
}