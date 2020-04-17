package com.tianma.tweaks.miui.app.widget.tag;

import android.os.Parcel;
import android.os.Parcelable;

public class TagBean implements Parcelable {

    private String key;
    private String value;
    private boolean selected = false;
    private boolean enabled = true;

    public TagBean() {
        this(null, null);
    }

    public TagBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    protected TagBean(Parcel in) {
        key = in.readString();
        value = in.readString();
        selected = in.readByte() != 0;
        enabled = in.readByte() != 0;
    }

    public static final Creator<TagBean> CREATOR = new Creator<TagBean>() {
        @Override
        public TagBean createFromParcel(Parcel in) {
            return new TagBean(in);
        }

        @Override
        public TagBean[] newArray(int size) {
            return new TagBean[size];
        }
    };
}
