package com.example.curriculum_design;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity{
    private ForceOfflineReceiver receiver;
//    public String account_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    //注册广播，处在栈顶的活动会接受到这条广播
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.Curriculum_design.force_offline");
        receiver=new ForceOfflineReceiver();
        registerReceiver(receiver,intentFilter);
    }

    //处在非栈顶的活动取消注册广播
    @Override
    protected void onPause() {
        super.onPause();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    class ForceOfflineReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context, Intent intent) {
            final AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("退出当前账号");
            builder.setMessage("退出后将无法继续收到信息通知，确定退出？");
            builder.setCancelable(false);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();
                    Intent i=new Intent(context,StartActivity.class);
                    context.startActivity(i);
                }
            });
            builder.show();
        }
    }

}
