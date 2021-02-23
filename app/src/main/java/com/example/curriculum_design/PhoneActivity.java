package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneActivity extends AppCompatActivity {
    private Button return_button;
    private RelativeLayout turnto;
    List<Map<String,Object>> listItems_1=new ArrayList<Map<String, Object>>();
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private String phone_name;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        turnto=(RelativeLayout) findViewById(R.id.turnto_relative);
        turnto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(PhoneActivity.this,Address_ListActivity.class);
                startActivity(intent_1);
            }
        });
        return_button=(Button)findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_2=new Intent(PhoneActivity.this,MyActivity.class);
                startActivity(intent_2);
            }
        });

        ListView call_log_list=(ListView)findViewById(R.id.call_log_listview);
        simpleAdapter=new SimpleAdapter(this,listItems_1,R.layout.call_log_list_item,new String[]{"header","name","time1","time2"},new int[]{R.id.call_log_head,R.id.call_log_name,R.id.call_log_time_1,R.id.call_log_time_2});
        call_log_list.setAdapter(simpleAdapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.curriculum_design.CALL_PHONE_NAME");
        localReceiver=new LocalReceiver();
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        show();//显示通话记录
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            phone_name=intent.getStringExtra("phone_name");
            Log.d("PhoneActivity",phone_name);
            Calendar calendar = Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String time1=hour+":"+minute;
            String time2=year+"."+month+"."+day;
            save(phone_name,time1,time2);
            listItems_1.clear();//清空listItem_1的内容
            show();
        }
    }

    private void show(){
        List<CallLog> callLogs= LitePal.order("id desc").find(CallLog.class);
        if(callLogs.size()==0){
            Log.d("PhoneActivity","error");
        } else {
            for (CallLog callLog:callLogs){
                String calllog_name=callLog.getName();
                char words[]=calllog_name.toCharArray();
                Map<String,Object> listItem_1=new HashMap<String,Object>();
                listItem_1.put("header",words[0]);
                listItem_1.put("name",calllog_name);
                listItem_1.put("time1",callLog.getTime());
                listItem_1.put("time2",callLog.getDate());
                listItems_1.add(listItem_1);
            }
            simpleAdapter.notifyDataSetChanged();//刷新
        }
    }
    public void save(String name,String time,String date){//将通话记录保存到数据库中
        CallLog callLog=new CallLog();
        callLog.setName(name);
        callLog.setTime(time);
        callLog.setDate(date);
        callLog.save();
    }

}
