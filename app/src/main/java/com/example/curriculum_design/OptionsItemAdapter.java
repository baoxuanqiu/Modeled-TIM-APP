package com.example.curriculum_design;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class OptionsItemAdapter extends BaseAdapter{
    private ArrayList<String> list;
    private ArrayList<Bitmap> head_shot;
    private Context mContext;
    private Handler handler;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    public OptionsItemAdapter(Context context, Handler handler, ArrayList<String> list,ArrayList<Bitmap> head_shot){
        this.mContext = context;
        this.handler = handler;
        this.list = list;
        this.head_shot=head_shot;
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.curriculum_design.REFURBISH_LIST");
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            refurbish();
        }
    }
    public void refurbish(){
        list.clear();
        List<LoginData> loginDataList=LitePal.findAll(LoginData.class);
        for (LoginData loginData:loginDataList){
            list.add(loginData.getData());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            //下拉项布局
            convertView = LayoutInflater.from(mContext).inflate(R.layout.options_item, null);
            holder.headimage=(ImageView)convertView.findViewById(R.id.header_image);
            holder.textView = (TextView) convertView.findViewById(R.id.account_id_text);
            holder.deleteimage = (ImageView) convertView.findViewById(R.id.delete_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(list.get(position));
        holder.headimage.setImageBitmap(head_shot.get(position));//更改图片

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg=new Message();
                Bundle data=new Bundle();
                data.putInt("selIndex", position);//设置选中索引
                msg.setData(data);
                msg.what = 1;
                handler.sendMessage(msg);//发出信息
            }
        });

        //为下拉框选项删除图标部分设置事件，最终效果是点击将该选项删除
        holder.deleteimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("delIndex", position);//设置删除索引
                msg.setData(data);
                msg.what = 2;
                handler.sendMessage(msg);//发出信息
            }
        });
        return convertView;
    }
}

class ViewHolder {
    TextView textView;
    ImageView deleteimage;
    ImageView headimage;
}

