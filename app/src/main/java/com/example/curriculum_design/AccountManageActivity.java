package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountManageActivity extends BaseActivity {
    private Button returnbutton;
    private LinearLayout backaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manage);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        returnbutton=(Button)findViewById(R.id.return_button);
        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AccountManageActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        backaccount=(LinearLayout) findViewById(R.id.back_account);
        backaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("com.example.Curriculum_design.force_offline");
                sendBroadcast(intent);
            }
        });
    }
}
