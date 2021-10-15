package com.tianma.tweaks.miui.xp.utils.appinfo;

public class AppInfo {

    private String packageName;

    private int versionCode;

    private String versionName;

    public AppInfo() {
    }

    public AppInfo(String packageName, int versionCode, String versionName) {
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}
