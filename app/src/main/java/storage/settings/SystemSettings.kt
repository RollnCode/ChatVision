package storage.settings

import android.content.Context
import android.net.ConnectivityManager
import com.rollncode.basement.application.BaseSettings

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
class SystemSettings(context: Context, name: String? = null) : BaseSettings(context, name) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isOnline: Boolean
        get() = connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false
    var isIncognito by prefBoolean()
    var phoneNumber by prefString()
    var currentUserId by prefInt()
    var isPolicyConfirmed by prefBoolean()
}