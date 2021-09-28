package com.tianma.tweaks.miui.utils

import android.database.Cursor
import android.net.Uri
import android.view.View
import com.tianma.tweaks.miui.utils.DebugHelper
import android.view.ViewGroup
import android.widget.TextView
import com.tianma.tweaks.miui.utils.XLog
import java.lang.StringBuilder

object DebugHelper {
    fun printViewTree(rootView: View) {
        traverseViewTree(rootView, 0)
    }

    private fun traverseViewTree(rootView: View, depth: Int) {
        var depth = depth
        print(depth, rootView)
        if (rootView is ViewGroup) {
            val vg = rootView
            depth++
            for (i in 0 until vg.childCount) {
                val view = vg.getChildAt(i)
                traverseViewTree(view, depth)
            }
        }
    }

    private fun print(depth: Int, view: View) {
        val sb = StringBuilder()
        for (i in 0 until depth) {
            sb.append("\t")
        }
        sb.append("|---")
        sb.append(view.javaClass.name)
        if (view is TextView) {
            sb.append(" (")
                .append(view.text)
                .append(")")
        }
        XLog.d("%s", sb.toString())
    }

    fun printCursor(uri: Uri, c: Cursor?) {
        XLog.d("%s", uri.toString())
        if (c == null) {
            return
        }
        val hasNext = c.moveToNext()
        if (!hasNext) {
            return
        }
        val columnCount = c.columnCount
        val columnNames = c.columnNames
        val columnTypes = IntArray(columnCount)
        for (i in 0 until columnCount) {
            columnTypes[i] = c.getType(i)
        }
        c.moveToPrevious()
        while (c.moveToNext()) {
            val sb = StringBuilder()
            for (i in 0 until columnCount) {
                var value: Any? = null
                val columnType = columnTypes[i]
                if (columnType == Cursor.FIELD_TYPE_INTEGER) {
                    c.getInt(i)
                }
                when (columnType) {
                    Cursor.FIELD_TYPE_INTEGER -> value = c.getInt(i)
                    Cursor.FIELD_TYPE_BLOB -> value = c.getBlob(i)
                    Cursor.FIELD_TYPE_FLOAT -> value = c.getFloat(i)
                    Cursor.FIELD_TYPE_STRING -> value = c.getString(i)
                    Cursor.FIELD_TYPE_NULL -> {
                    }
                    else -> {
                    }
                }
                sb.append(columnNames[i]).append(" = ").append(value).append(", ")
            }
            sb.append("\n")
            XLog.d("%s", sb.toString())
        }
    }
}