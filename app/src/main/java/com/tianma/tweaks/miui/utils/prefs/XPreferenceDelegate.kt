package com.tianma.tweaks.miui.utils.prefs

import android.os.Build
import androidx.core.content.edit
import com.tianma.tweaks.miui.utils.logD
import com.tianma.tweaks.miui.utils.logE
import com.tianma.tweaks.miui.utils.safeAs
import de.robv.android.xposed.XSharedPreferences
import java.io.File
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * desc: XSharedPreference 的属性代理
 * date: 6/7/21
 */
class XPreferenceDelegate<T>(
    private val key: String,
    private val defaultValue: T,
    private val packageName: String,
    private val prefFileName: String
) : ReadWriteProperty<Any?, T> {

    private val sharedPrefs: XSharedPreferences by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // Android 7.0+
            val prefFile = File("/data/user_de/0/${packageName}/shared_prefs/${prefFileName}.xml")
            XSharedPreferences(prefFile)
        } else {
            XSharedPreferences(packageName, prefFileName)
        }.also { xsp ->
            try {
                xsp.makeWorldReadable()
            } catch (t: Throwable) {
                logE("", t)
            }
        }
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
        return (when (defaultValue) {
            is Int -> sharedPrefs.getInt(key, defaultValue)
            is String -> sharedPrefs.getString(key, defaultValue)
            is Boolean -> sharedPrefs.getBoolean(key, defaultValue)
            is Long -> sharedPrefs.getLong(key, defaultValue)
            is Float -> sharedPrefs.getFloat(key, defaultValue)
            is Set<*> -> sharedPrefs.getStringSet(key, defaultValue.safeAs()).safeAs()
            else -> throw IllegalArgumentException("Unsupported type. $key $defaultValue")
        } as T).also { value ->
            // logD("XPreferenceDelegate: $key = $value")
        }
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