package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private List<SettingList> settingLists=new ArrayList<>();
    private Button returnbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        initSettingLists();
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recyclerview_setting);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        SettingListAdapter adapter=new SettingListAdapter(settingLists);
        recyclerView.setAdapter(adapter);

        returnbutton=(Button)findViewById(R.id.return_button);
        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,MyActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSettingLists(){
        SettingList list1=new SettingList("功能");
        settingLists.add(list1);
        SettingList list2=new SettingList("字体大小");
        settingLists.add(list2);
        SettingList list3=new SettingList("账号安全");
        settingLists.add(list3);
        SettingList list4=new SettingList("关于TIM与帮助");
        settingLists.add(list4);
        SettingList list5=new SettingList("账号管理");
        settingLists.add(list5);
    }
}
