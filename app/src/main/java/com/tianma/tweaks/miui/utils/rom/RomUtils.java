package com.tianma.tweaks.miui.utils.rom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Rom 相关工具类
 */
public class RomUtils {

    private RomUtils() {
    }

    public static String getSystemProperty(String propertyName) {
        String result = null;
        BufferedReader br = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop " + propertyName);
            br = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
            result = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
