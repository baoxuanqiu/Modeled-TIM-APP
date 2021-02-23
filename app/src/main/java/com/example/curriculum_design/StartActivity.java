package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StartActivity extends AppCompatActivity {
    private TextView welcome;
    private Button qq_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }

        welcome=(TextView)findViewById(R.id.welcome);
        TextPaint tp=welcome.getPaint();
        tp.setFakeBoldText(true);
        qq_login=(Button)findViewById(R.id.qq_login);
        qq_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
