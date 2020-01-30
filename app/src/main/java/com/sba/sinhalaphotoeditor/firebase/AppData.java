package com.sba.sinhalaphotoeditor.firebase;

import java.io.Serializable;

public class AppData implements Serializable
{
    public String versionName;
    public int versionCode;

    public AppData()
    {

    }
    public AppData(AppData data)
    {
        this.versionCode = data.versionCode;
        this.versionName = data.versionName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
