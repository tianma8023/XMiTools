package com.tianma.tweaks.miui.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DebugHelper {

    private DebugHelper() {
    }

    public static void tree(View rootView) {
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
}
