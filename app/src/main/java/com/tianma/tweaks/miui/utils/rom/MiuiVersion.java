package com.tianma.tweaks.miui.utils.rom;

import com.tianma.tweaks.miui.utils.XLogKt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MiuiVersion {

    public final static MiuiVersion V_19_5_7 = new MiuiVersion("19.5.7");

    private static final String DATE_FORMAT = "yy.M.d";

    private long time;

    public MiuiVersion(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            time = sdf.parse(timeStr).getTime();
        } catch (Exception e) {
            XLogKt.logE("time format error %s", timeStr, e);
        }
    }

    public MiuiVersion(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "MiuiVersion{" +
                "time=" + new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(time)) +
                '}';
    }
}
