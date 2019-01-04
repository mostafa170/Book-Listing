package com.example.android.booklisting.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mostafa on 06/07/2017.
 */

public class ListBook implements Parcelable {
    String author;
    String title;

    public ListBook(String author, String title) {
        this.author = author;
        this.title = title;
    }
    public ListBook( String title) {
        this.title = title;
    }

    protected ListBook(Parcel in) {
        author = in.readString();
        title = in.readString();
    }

    public static final Creator<ListBook> CREATOR = new Creator<ListBook>() {
        @Override
        public ListBook createFromParcel(Parcel in) {
            return new ListBook(in);
        }

        @Override
        public ListBook[] newArray(int size) {
            return new ListBook[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(title);
    }
}
