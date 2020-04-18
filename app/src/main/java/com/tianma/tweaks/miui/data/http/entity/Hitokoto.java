package com.tianma.tweaks.miui.data.http.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 一言
 */
// {
//         "id": 4401,
//         "hitokoto": "那么难受，那么痛苦，可是 世界这么美丽...让我如何能够忘记！",
//         "type": "a",
//         "from": "朝花夕誓",
//         "creator": "飞龙project",
//         "created_at": "1553579805"
// }
public class Hitokoto implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("hitokoto")
    private String content;

    @SerializedName("type")
    private String type;

    @SerializedName("from")
    private String from;

    public Hitokoto() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "Hitokoto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", from='" + from + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeString(type);
        dest.writeString(from);
    }

    protected Hitokoto(Parcel in) {
        id = in.readInt();
        content = in.readString();
        type = in.readString();
        from = in.readString();
    }

    public static final Creator<Hitokoto> CREATOR = new Creator<Hitokoto>() {
        @Override
        public Hitokoto createFromParcel(Parcel in) {
            return new Hitokoto(in);
        }

        @Override
        public Hitokoto[] newArray(int size) {
            return new Hitokoto[size];
        }
    };
}
