package com.example.q.cs496w1;

public class SinglePhoto {
    String name;
    int resId;

    public SinglePhoto(String name, int resId) {
        this.name = name;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
