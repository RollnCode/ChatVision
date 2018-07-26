package storage

import android.annotation.SuppressLint
import android.content.Context
import storage.settings.SystemSettings

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.25
 */
object Settings {
    private const val settingsName = "chatVisionPref"
    @SuppressLint("StaticFieldLeak")
    lateinit var system: SystemSettings

    fun init(context: Context) {
        system = SystemSettings(context, settingsName)
    }
}