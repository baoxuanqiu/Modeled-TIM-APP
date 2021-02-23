package com.example.curriculum_design;

import android.graphics.Bitmap;

public class Chat {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    private String content;
    private int type;
    private Bitmap image;
    public Chat(String content,int type,Bitmap image){
        this.content=content;
        this.type=type;
        this.image=image;
    }
    public String getContent(){
        return content;
    }
    public int getType(){
        return type;
    }
    public Bitmap getImage() {
        return image;
    }
}
