package com.example.curriculum_design;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.util.List;

public class GetAccountId extends BroadcastReceiver {
    public static String account_ID;
    public static String account_Name;
    public static Bitmap account_Image;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.account_ID = intent.getStringExtra("accountId");//收到广播传过来的accountId
//        Log.d("GetAccountId:",account_ID);
    }

    public String getAccount_Name() {
        List<Account> account1= LitePal.select("name")
                .where("accountid like ?",account_ID)
                .find(Account.class);
        Gson gson = new Gson();
        String real_name = gson.toJson(account1.get(0));//将list集合转化为json字符串
        JSONObject jsonObj = JSONObject.parseObject(real_name);
        this.account_Name=jsonObj.getString("name");//json字符串取name的值
        return account_Name;
    }

    public Bitmap getAccount_Image(){
        List<Account> account1= LitePal.select("headshot")
                .where("accountid like ?",account_ID)
                .find(Account.class);
        byte[] images=account1.get(0).getHeadshot();
        Bitmap bitmap=BitmapFactory.decodeByteArray(images, 0, images.length);
        this.account_Image=bitmap;
        return account_Image;
    }
}