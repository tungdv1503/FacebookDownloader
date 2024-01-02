package com.example.fbvideodownloader.model;

public class MP3model {
    String title;
    String path;
    String parent;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public MP3model(String title, String path, String parent) {
        this.title = title;
        this.path = path;
        this.parent = parent;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
