package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInformationActivity extends AppCompatActivity {
    private SimpleAdapter simpleAdapter;
    private List informationList=new ArrayList<>();
    private String friendID;
    private String friendName;
    private ImageView friendImage;
    private Button return_button;
    private Button chat_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        Intent intent_1=getIntent();
        friendID=intent_1.getStringExtra("friendID");

        return_button=(Button) findViewById(R.id.return_button_4);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(PersonalInformationActivity.this,ContactsActivity.class);
                startActivity(intent_1);
            }
        });

        ListView personal_information=(ListView)findViewById(R.id.personal_information_listView);
        simpleAdapter=new SimpleAdapter(this,informationList,android.R.layout.simple_expandable_list_item_2,
                new String[]{"name","number"},new int[]{android.R.id.text1,android.R.id.text2});
        personal_information.setAdapter(simpleAdapter);
        initInformation();
        chat_button=(Button)findViewById(R.id.chat_button);
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_2=new Intent(PersonalInformationActivity.this,ChatActivity.class);
                intent_2.putExtra("friendID",friendID);
                startActivity(intent_2);
            }
        });
    }

    private void initInformation(){
        List<Contact> contactList= LitePal
                .select("contact_name","contact_address","contact_tele","contact_head")
                .where("contact_ID=?",friendID)
                .find(Contact.class);
        friendName=contactList.get(0).getContact_name();
        String address=contactList.get(0).getContact_address();
        String tele=contactList.get(0).getContact_tele();
        byte[] images=contactList.get(0).getContact_head();
        Bitmap bitmap= BitmapFactory.decodeByteArray(images, 0, images.length);
        Map map=new HashMap();
        map.put("name","ITM账号");
        map.put("number",friendID);
        informationList.add(map);
        Map map_1=new HashMap();
        map_1.put("name","昵称");
        map_1.put("number",friendName);
        informationList.add(map_1);
        Map map_2=new HashMap();
        map_2.put("name","地址");
        map_2.put("number",address);
        informationList.add(map_2);
        Map map_3=new HashMap();
        map_3.put("name","手机号码");
        map_3.put("number",tele);
        informationList.add(map_3);
        friendImage=(ImageView)findViewById(R.id.personal_image);
        friendImage.setImageBitmap(bitmap);
    }
}
