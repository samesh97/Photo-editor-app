package com.sba.sinhalaphotoeditor.model;

import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class FirebaseStickerCategory implements Serializable
{
    public Integer id;
    public String name;

    public FirebaseStickerCategory(){}
    public FirebaseStickerCategory(Integer id,String name)
    {
        this.id= id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
