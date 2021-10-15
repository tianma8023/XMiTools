package com.tianma.tweaks.miui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.tianma.tweaks.miui.BuildConfig
import java.io.File

/**
 * Utils for storage.
 */
object StorageUtils {

    /**
     * 是否有SD卡
     */
    @JvmStatic
    fun isSDCardMounted(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    /**
     * Get sdcard directory
     *
     * @return SD card directory
     */
    @JvmStatic
    fun getSDCardDir(): File = Environment.getExternalStorageDirectory()

    /**
     * get sdcard public documents directory
     *
     * @return SD card public documents directory
     */
    @JvmStatic
    fun getPublicDocumentsDir(): File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)


    @JvmStatic
    fun getSharedPreferencesFile(context: Context?, preferencesName: String): File {
        val dataDir = ContextCompat.getDataDir(context!!)
        val prefsDir = File(dataDir, "shared_prefs")
        return File(prefsDir, "$preferencesName.xml")
    }

    /**
     * Get internal data dir. /data/data/<package_name>/</package_name>
     * */
    @JvmStatic
    fun getInternalDataDir(): File {
        return File(Environment.getDataDirectory(), "data/" + BuildConfig.APPLICATION_ID)
    }

    /**
     * Get internal files dir. /data/data/<package_name>/files/
     *
     * @return</package_name>
     * */
    @JvmStatic
    fun getInternalFilesDir(): File {
        return File(getInternalDataDir(), "files")
    }

    /**
     * Get external files dir. /sdcard/Android/data/<package_name>/files/</package_name>
     * */
    @JvmStatic
    fun getExternalFilesDir(): File {
        return File(
            Environment.getExternalStorageDirectory(),
            "Android/data/" + BuildConfig.APPLICATION_ID + "/files/"
        )
    }

    /**
     * Get files dir
     *
     * @see StorageUtils.getExternalFilesDir
     * @see StorageUtils.getInternalFilesDir
     */
    @JvmStatic
    fun getFilesDir(): File {
        return if (isSDCardMounted()) {
            val externalFilesDir = getExternalFilesDir()
            if (!externalFilesDir.exists()) {
                externalFilesDir.mkdirs()
            }
            externalFilesDir
        } else {
            getInternalFilesDir()
        }
    }

    /**
     * Set file world writable
     */
    @JvmStatic
    @SuppressLint("SetWorldWritable", "SetWorldReadable")
    fun setFileWorldWritable(file: File?, parentDepth: Int) {
        file ?: return
        if (!file.exists()) {
            return
        }
        var tempFile: File? = file
        val tempDepth = parentDepth + 1
        for (i in 0 until tempDepth) {
            tempFile?.setExecutable(true, false)
            tempFile?.setWritable(true, false)
            tempFile?.setReadable(true, false)
            tempFile = tempFile?.parentFile
            if (tempFile == null) {
                break
            }
        }
    }

}