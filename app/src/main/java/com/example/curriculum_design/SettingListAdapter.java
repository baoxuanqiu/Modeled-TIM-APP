package com.example.curriculum_design;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SettingListAdapter extends RecyclerView.Adapter<SettingListAdapter.ViewHolder> {
    private List<SettingList> mSettingList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView settingname;
        View setttingListView;
        public ViewHolder(View view){
            super(view);
            setttingListView=view;
            settingname=(TextView)view.findViewById(R.id.setting_name);
        }
    }

    public SettingListAdapter(List<SettingList> settingLists){
        mSettingList=settingLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.setttingListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                SettingList list=mSettingList.get(position);
                switch (list.getName()){
                    case "账号管理":
                        Intent intent_1=new Intent("com.example.curriculum_design.ACCOUNT_MANAGE");
                        v.getContext().startActivity(intent_1);
                        break;
                    case "关于TIM与帮助":
                        break;
                    case "账号安全":
                        break;
                    case "字体大小":
                        break;
                    case "功能":
                        break;
                    default:
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingList settingitem=mSettingList.get(position);
        holder.settingname.setText(settingitem.getName());
    }

    @Override
    public int getItemCount() {
        return mSettingList.size();
    }
}
