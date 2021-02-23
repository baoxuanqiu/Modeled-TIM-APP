package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

public class CreateSuccessActivity extends AppCompatActivity {
    private TextView accountid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_success);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        Intent intent_1=getIntent();
        String data=intent_1.getStringExtra("account_id");

        accountid=(TextView)findViewById(R.id.account_Id);
        accountid.setText(data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            Intent intent_2=new Intent(CreateSuccessActivity.this,LoginActivity.class);
            startActivity(intent_2);
            return true;
        }
        return false;
    }
}
