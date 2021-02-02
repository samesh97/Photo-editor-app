package com.sba.sinhalaphotoeditor.model;

import java.io.Serializable;

public class FirebaseSticker implements Serializable
{
    public Integer id;
    public String path;

    public FirebaseSticker(){}
    public FirebaseSticker(Integer id, String path)
    {
        this.id= id;
        this.path = path;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
