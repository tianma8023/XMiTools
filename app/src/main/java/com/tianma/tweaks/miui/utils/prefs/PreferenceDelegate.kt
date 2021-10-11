package com.tianma.tweaks.miui.utils.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.tianma.tweaks.miui.app.App
import com.tianma.tweaks.miui.utils.ContextUtils
import com.tianma.tweaks.miui.utils.safeAs
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * desc: Preference 的属性代理
 * date: 6/7/21
 */
class PreferenceDelegate<T>(
    private val key: String,
    private val defaultValue: T,
    private val prefName: String = "default"
) : ReadWriteProperty<Any?, T> {

    private val sharedPrefs: SharedPreferences by lazy {
        val context = ContextUtils.getProtectedContextIfNecessary(App.appContext)
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(findPreferenceKey(property))
    }

    private fun findPreferenceKey(property: KProperty<*>): String {
        return if (key.isEmpty()) {
            property.name
        } else {
            key
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findPreference(key: String): T {
        return when (defaultValue) {
            is Int -> sharedPrefs.getInt(key, defaultValue)
            is String -> sharedPrefs.getString(key, defaultValue)
            is Boolean -> sharedPrefs.getBoolean(key, defaultValue)
            is Long -> sharedPrefs.getLong(key, defaultValue)
            is Float -> sharedPrefs.getFloat(key, defaultValue)
            is Set<*> -> sharedPrefs.getStringSet(key, defaultValue.safeAs()).safeAs()
            else -> throw IllegalArgumentException("Unsupported type. $key $defaultValue")
        } as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(findPreferenceKey(property), value)
    }

    private fun putPreference(key: String, value: T) {
        sharedPrefs.edit {
            when (value) {
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Float -> putFloat(key, value)
                is Set<*> -> putStringSet(key, value.safeAs())
                else -> throw IllegalArgumentException("Unsupported type. $key $defaultValue")
            }
        }
    }
}