package com.tianma.tweaks.miui.utils;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DebugHelper {

    private DebugHelper() {
    }

    public static void printViewTree(View rootView) {
        traverseViewTree(rootView, 0);
    }

    private static void traverseViewTree(View rootView, int depth) {
        print(depth, rootView);
        if (rootView instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) rootView;
            depth++;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View view = vg.getChildAt(i);
                traverseViewTree(view, depth);
            }
        }
    }

    private static void print(int depth, View view) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        sb.append("|---");
        sb.append(view.getClass().getName());
        if (view instanceof TextView) {
            sb.append(" (")
                    .append(((TextView) view).getText())
                    .append(")");
        }
        XLog.d("%s", sb.toString());
    }

    public static void printCursor(Uri uri, Cursor c) {
        XLog.d("%s", uri.toString());
        if (c == null) {
            return;
        }
        boolean hasNext = c.moveToNext();
        if (!hasNext) {
            return;
        }

        int columnCount = c.getColumnCount();
        String[] columnNames = c.getColumnNames();
        int[] columnTypes = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnTypes[i] = c.getType(i);
        }
        c.moveToPrevious();
        while (c.moveToNext()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < columnCount; i++) {
                Object value = null;
                int columnType = columnTypes[i];
                if (columnType == Cursor.FIELD_TYPE_INTEGER) {
                    c.getInt(i);
                }
                switch (columnType) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        value = c.getInt(i);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        value = c.getBlob(i);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        value = c.getFloat(i);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        value = c.getString(i);
                        break;
                    default:
                    case Cursor.FIELD_TYPE_NULL:
                        break;
                }
                sb.append(columnNames[i]).append(" = ").append(value).append(", ");
            }
            sb.append("\n");
            XLog.d("%s", sb.toString());
        }
    }


}
