package com.example.curriculum_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Address_ListActivity extends AppCompatActivity {
    List<Map<String,Object>> listItems=new ArrayList<Map<String, Object>>();
    private Button return_button;
    private String telenum;
    private String telename;
    private LocalBroadcastManager localBroadcastManager;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address__list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }

        return_button=(Button)findViewById(R.id.return_button_2);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(Address_ListActivity.this,PhoneActivity.class);
                startActivity(intent_1);
            }
        });
        ListView phone_listview=(ListView)findViewById(R.id.phone_listview);
        simpleAdapter=new SimpleAdapter(this,listItems,R.layout.address_list_item,new String[]{"header","telename","telenumber"},new int[]{R.id.header_image,R.id.tele_name,R.id.tele_number});
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
        phone_listview.setAdapter(simpleAdapter);
        phone_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> map=(Map<String,Object>)Address_ListActivity.this.listItems.get(position);
                Object object=map.get("telenumber");//指定Map集合中的键名。
                if(object instanceof String){//判断键对应的值是否为String类型
                    telenum=(String)object;
                }
                Object object_1=map.get("telename");//指定Map集合中的键名。
                if(object_1 instanceof String){//判断键对应的值是否为String类型
                    telename=(String)object_1;
                }
                if(ContextCompat.checkSelfPermission(Address_ListActivity.this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Address_ListActivity.this,new String[]{Manifest.permission.CALL_PHONE},2);
                }else {
                    call(telenum,telename);
                }
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        }else {
            readContacts();
        }
    }

    private void readContacts(){
        Cursor cursor=null;
        try{
            String[] projection=new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            };//返回的列名
            String selection=null;//查询的条件
            String[] selectionArgs=null;//查询条件的参数
            String sortOrder=null;//排序
            cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projection,selection,selectionArgs,sortOrder);
            if(cursor!=null){
                while (cursor.moveToNext()){
                    String phoneName=cursor.getString(0);
                    String number=cursor.getString(1);
                    String id=cursor.getString(2);
                    Bitmap photo=getBitmap(this,id);

                    Map<String,Object> listItem=new HashMap<String,Object>();
                    listItem.put("telename",phoneName);
                    listItem.put("telenumber",number);
                    if(photo==null){
                        listItem.put("header",R.drawable.user_head);
                    }else{
                        listItem.put("header",photo);
                    }
                    listItems.add(listItem);
                }
                simpleAdapter.notifyDataSetChanged();//刷新
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }else{
                    Toast.makeText(this,"你关闭了获取联系人的权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    call(telenum,telename);
                }else {
                    Toast.makeText(this,"你关闭了通话的权限",Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    private void call(String tele_num,String tele_name){
        try{
            Intent intent_1=new Intent(Intent.ACTION_CALL);
            intent_1.setData(Uri.parse("tel:"+tele_num));
            startActivity(intent_1);
        }catch (SecurityException e){
            e.printStackTrace();
        }
        Intent intent_2=new Intent("com.example.curriculum_design.CALL_PHONE_NAME");
        intent_2.putExtra("phone_name",tele_name);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent_2);
    }

    //获取联系人头像的bitmap   content://com.android.contacts
    public static Bitmap getBitmap(Context context, String id){
        ContentResolver cr=context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,id);
        InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        return BitmapFactory.decodeStream(is);
    }
}
