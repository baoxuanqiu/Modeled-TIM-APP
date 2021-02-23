package com.example.curriculum_design;

import android.graphics.Bitmap;

import org.litepal.crud.LitePalSupport;

public class Contact extends LitePalSupport {
    private int id;
    private int contact_ID;//账号
    private String contact_name;//姓名
    private String contact_address;//地址
    private String contact_tele;//电话
    private byte[] contact_head;//头像

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getContact_ID() {
        return contact_ID;
    }

    public byte[] getContact_head() {
        return contact_head;
    }

    public String getContact_address() {
        return contact_address;
    }

    public String getContact_name() {
        return contact_name;
    }

    public String getContact_tele() {
        return contact_tele;
    }

    public void setContact_address(String contact_address) {
        this.contact_address = contact_address;
    }

    public void setContact_head(byte[] contact_head) {
        this.contact_head = contact_head;
    }

    public void setContact_ID(int contact_ID) {
        this.contact_ID = contact_ID;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public void setContact_tele(String contact_tele) {
        this.contact_tele = contact_tele;
    }
}
