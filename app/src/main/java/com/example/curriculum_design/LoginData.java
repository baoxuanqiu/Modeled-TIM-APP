package com.example.curriculum_design;

import org.litepal.crud.LitePalSupport;

public class LoginData extends LitePalSupport {
    private String data;//保存账号
//    private byte[] headshot;//保存头像
    public void setData(String data){
        this.data=data;
    }
    public String getData(){
        return data;
    }
}
