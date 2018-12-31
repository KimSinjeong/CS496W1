package com.example.q.cs496w1;

public class SingleContact {

    String name;
    String phone;
    int resId = R.drawable.icon_273;
    long photoId;
    long Id;

    public SingleContact(String name, String mobile) {
        this.name = name;
        this.phone = mobile;
    }

    public SingleContact(String name, String mobile, int resId) {
        this.name = name;
        this.phone = mobile;
        this.resId = resId;
    }

    public SingleContact(String name, String mobile, long photoid, long id) {
        this.name = name;
        this.phone = mobile;
        this.photoId = photoid;
        this.Id = id;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getPhone() { return phone; }

    public void setPhone(String mobile) {
        this.phone = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) { this.photoId = photoId; }

    public long getId() { return Id; }

    public void setId(int Id) {
        this.Id = Id;
    }
}

