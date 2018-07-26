package com.rollncode.basement.application

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.lang.IllegalArgumentException
import kotlin.reflect.KProperty

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.09.01
 */
abstract class BaseSettings(context: Context, name: String? = null) {

    private val preferences = if (name == null) {
        PreferenceManager.getDefaultSharedPreferences(context)

    } else {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun prefString(default: String = "") = SettingsDelegate(preferences, default)
    fun prefInt(default: Int = 0) = SettingsDelegate(preferences, default)
    fun prefBoolean(default: Boolean = false) = SettingsDelegate(preferences, default)
    fun prefFloat(default: Float = 0f) = SettingsDelegate(preferences, default)
}

class SettingsDelegate<T>(private val preferences: SharedPreferences, private val default: T) {
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (default) {
            is Long    -> preferences.getLong(property.name, default)
            is String  -> preferences.getString(property.name, default)
            is Int     -> preferences.getInt(property.name, default)
            is Boolean -> preferences.getBoolean(property.name, default)
            is Float   -> preferences.getFloat(property.name, default)

            else       -> throw IllegalArgumentException("This type can't be get from Preferences")
        } as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        preferences.edit().apply {
            when (value) {
                is Long    -> putLong(property.name, value)
                is String  -> putString(property.name, value)
                is Int     -> putInt(property.name, value)
                is Boolean -> putBoolean(property.name, value)
                is Float   -> putFloat(property.name, value)

                else       -> throw IllegalArgumentException("This type can't be saved to Preferences")
            }
        }.apply()
    }
}