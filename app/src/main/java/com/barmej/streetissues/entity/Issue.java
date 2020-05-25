package com.barmej.streetissues.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class Issue implements Parcelable {

    private String title;
    private String description;
    private String photo;
    private GeoPoint location;
    private long date;

    public Issue() {

    }

    public Issue(String title, String description, String photo, GeoPoint location, long date) {
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.location = location;
        this.date = date;
    }


    protected Issue(Parcel in) {
        title = in.readString();
        description = in.readString();
        photo = in.readString();
        location = new GeoPoint(in.readDouble(), in.readDouble());
        date = in.readLong();

    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(photo);
        parcel.writeDouble(location.getLatitude());
        parcel.writeDouble(location.getLongitude());
        parcel.writeLong(date);

    }
}




