package com.tianma.tweaks.miui.data.http.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 诗
 */
public class Poem implements Parcelable {

    private String content;

    private String origin;

    private String author;

    private String category;

    public Poem() {
    }

    protected Poem(Parcel in) {
        content = in.readString();
        origin = in.readString();
        author = in.readString();
        category = in.readString();
    }

    public static final Creator<Poem> CREATOR = new Creator<Poem>() {
        @Override
        public Poem createFromParcel(Parcel in) {
            return new Poem(in);
        }

        @Override
        public Poem[] newArray(int size) {
            return new Poem[size];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Poem{" +
                "content='" + content + '\'' +
                ", origin='" + origin + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(origin);
        dest.writeString(author);
        dest.writeString(category);
    }
}
