package com.example.curriculum_design;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;
import org.w3c.dom.Comment;

import java.util.List;

public class Account extends LitePalSupport {
    private int id;
    private int accountid;
    private String name;
    private String password;
    private String address;
    private String telephone;
    private byte[] headshot;//头像

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public int getAccountid(){
        return accountid;
    }
    public void setAccountid(int accountid){
        this.accountid=accountid;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public String getTelephone(){
        return telephone;
    }
    public void setTelephone(String telephone){
        this.telephone=telephone;
    }
    public byte[] getHeadshot() {
        return headshot;
    }
    public void setHeadshot(byte[] headshot) {
        this.headshot = headshot;
    }
    public Account(){
        super();
    }
    public Account(int id,int accountid,String name,String password,String address,String telephone,byte[] headshot){
        super();
        this.id=id;
        this.accountid=accountid;
        this.password=password;
        this.address=address;
        this.telephone=telephone;
        this.headshot=headshot;
    }
}
