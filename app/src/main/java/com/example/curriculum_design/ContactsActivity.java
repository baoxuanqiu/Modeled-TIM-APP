package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {
    private Button return_button;
    private SimpleAdapter simpleAdapter;
    private ListView contact_listView;
    List<Map<String,Object>> listItems=new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }

        return_button=(Button) findViewById(R.id.return_button_3);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ContactsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        addContact();//初始化一些联系人
        contact_listView=(ListView)findViewById(R.id.contacts_listView);
        //这里的布局跟通讯录页面的布局是一样的，直接借用通讯录的布局就行
        simpleAdapter=new SimpleAdapter(this,listItems,R.layout.address_list_item,new String[]{"contact_head","contact_name","contact_ID"},new int[]{R.id.header_image,R.id.tele_name,R.id.tele_number});
        //设置simpleAdapter可以直接显示bitmap图片
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if((view instanceof ImageView) & (data instanceof Bitmap)){
                    ImageView imageView = (ImageView)view;
                    Bitmap mBitmap = (Bitmap)data;
                    imageView.setImageBitmap(mBitmap);
                    return true;
                }
                return false;
            }
        });
        contact_listView.setAdapter(simpleAdapter);
        initContactList();
        contact_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> map=(Map<String,Object>)ContactsActivity.this.listItems.get(position);
                Object object=map.get("contact_ID");//指定Map集合中的键名
                if(object instanceof Integer){//判断键对应的值是否为int类型
                    int ID=Integer.parseInt(String.valueOf(object));
                    Intent intent_1=new Intent(ContactsActivity.this,PersonalInformationActivity.class);
                    intent_1.putExtra("friendID",ID+"");
                    startActivity(intent_1);
                }else{
                    Log.d("ContactActivity","contactID is not int");
                }
            }
        });
    }


    private void initContactList(){
        List<Contact> contactList=LitePal.findAll(Contact.class);
        for (Contact contact:contactList){
            Map<String,Object> listItem=new HashMap<String,Object>();
            listItem.put("contact_name",contact.getContact_name());
            listItem.put("contact_ID",contact.getContact_ID());
            Bitmap bitmap=Bytes2Bimap(contact.getContact_head());
            listItem.put("contact_head",bitmap);
            listItems.add(listItem);
        }
        simpleAdapter.notifyDataSetChanged();//刷新
    }

    //初始化联系人，当可以自主添加联系人时，可以删
    private void addContact(){
        List<Contact> contactList= LitePal.findAll(Contact.class);
        if (contactList.size()==0){
            Bitmap bmp= BitmapFactory.decodeResource(getResources(), R.drawable.user_head);
            Contact contact=new Contact();
            contact.setContact_ID(95100001);
            contact.setContact_name("安达");
            contact.setContact_address("广东省广州市");
            contact.setContact_tele("13444444444");
            contact.setContact_head(Bitmap2Bytes(bmp));
            contact.save();
            Contact contact_1=new Contact();
            contact_1.setContact_ID(95100002);
            contact_1.setContact_name("可乐");
            contact_1.setContact_address("广东省汕头市");
            contact_1.setContact_tele("13455555555");
            contact_1.setContact_head(Bitmap2Bytes(bmp));
            contact_1.save();
            Contact contact_2=new Contact();
            contact_2.setContact_ID(95100003);
            contact_2.setContact_name("雪碧");
            contact_2.setContact_address("广东省深州市");
            contact_2.setContact_tele("13466666666");
            contact_2.setContact_head(Bitmap2Bytes(bmp));
            contact_2.save();
        }
    }

    //将bitmap转化为byte[]图片
    private byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //将byte[]转化为Bitmap
    private Bitmap Bytes2Bimap(byte[] b){
        if(b.length!=0){
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

}
