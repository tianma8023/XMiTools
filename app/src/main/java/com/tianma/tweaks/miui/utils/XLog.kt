package com.tianma.tweaks.miui.utils

import android.util.Log
import com.tianma.tweaks.miui.BuildConfig

/**
 * desc: Log util
 * date: 2021/8/6
 */

private const val LOG_TAG: String = BuildConfig.LOG_TAG
private const val LOG_LEVEL = BuildConfig.LOG_LEVEL
private const val LOG_TO_XPOSED = BuildConfig.LOG_TO_XPOSED
private const val LOG_TO_EDXPOSED = BuildConfig.LOG_TO_EDXPOSED

private fun log(priority: Int, message: String, vararg args: Any) {

    var targetPriority = priority
    var msg = message
    if (targetPriority < LOG_LEVEL) return

    if (args.isNotEmpty()) {
        msg = String.format(msg, *args)
    }

    if (args.isNotEmpty() && args[args.size - 1] is Throwable) {
        val throwable = args[args.size - 1] as Throwable
        val stacktraceStr = Log.getStackTraceString(throwable)
        msg += """
            
            $stacktraceStr
            """.trimIndent()
    }

    // Write to the default log tag
    Log.println(targetPriority, LOG_TAG, msg)

    // Duplicate to the Xposed log if enabled
    if (LOG_TO_XPOSED) {
        if (targetPriority <= Log.DEBUG) { // DEBUG level 不会在 Xposed 日志中生成,所以调整等级
            targetPriority = Log.INFO
        }
        Log.println(targetPriority, "Xposed", "$LOG_TAG: $msg")
    }
    if (LOG_TO_EDXPOSED) {
        if (targetPriority <= Log.DEBUG) { // DEBUG level 不会在 EdXposed 日志中生成,所以调整等级
            targetPriority = Log.INFO
        }
        // EdXposed
        Log.println(targetPriority, "EdXposed-Bridge", "$LOG_TAG: $msg")
        // LSPosed
        Log.println(targetPriority, "LSPosed-Bridge", "$LOG_TAG: $msg")
    }
}

fun logV(message: String, vararg args: Any) {
    log(Log.VERBOSE, message, *args)
}

fun logD(message: String, vararg args: Any) {
    log(Log.DEBUG, message, *args)
}

fun logI(message: String, vararg args: Any) {
    log(Log.INFO, message, *args)
}

fun logW(message: String, vararg args: Any) {
    log(Log.WARN, message, *args)
}

fun logE(message: String, vararg args: Any) {
    log(Log.ERROR, message, *args)
}