package com.example.curriculum_design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {
    private EditText accountedit;
    private EditText passwordedit;
    private ImageView user_head_image;
    private Button login;
    private ImageView pull;
    private TextView createaccount;
    private PopupWindow popupWindow;
    private OptionsItemAdapter optionsItemAdapter;
    private ArrayList<String> datas=new ArrayList<>();//下拉框选项ID数据源
    private ArrayList<Bitmap> head_shots=new ArrayList<>();//下拉框选项头像图片数据源
    private ListView listView;//展示所有下拉选项的listview
    private Handler handler;//用来处理选中或者删除下拉项信息
    private boolean flag = false;//初始化成功标志
    private LinearLayout parent;
    private int parent_width;
    private LocalBroadcastManager localBroadcastManager;//本地广播
    private ToggleButton tbPasswordIsVisibility;//显示密码的可见与不可见
    private SharedPreferences pref;//保存账号密码
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }

        pull=(ImageView)findViewById(R.id.pull_image);
        user_head_image=(ImageView)findViewById(R.id.user_head_image_1);
        accountedit=(EditText)findViewById(R.id.accountEdit);
        passwordedit=(EditText)findViewById(R.id.passwordEdit);
        login=(Button)findViewById(R.id.login);
        tbPasswordIsVisibility=(ToggleButton)findViewById(R.id.tb_password_visibility);
        tbPasswordIsVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//显示密码
                    passwordedit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {//隐藏密码
                    passwordedit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                passwordedit.setSelection(passwordedit.length());//每次显示或者关闭时，密码显示编辑的线不统一在最后，下面是为了统一
            }
        });
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember=pref.getBoolean("remember_password_account",false);
        if(isRemember){
            String account_1=pref.getString("account","");
            String password_1=pref.getString("password","");
            accountedit.setText(account_1);
            passwordedit.setText(password_1);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=accountedit.getText().toString();
                String password=passwordedit.getText().toString();
                List<Account> account1= LitePal.select("password")
                        .where("accountid like ?",account)
                        .find(Account.class);
                if(account1.size()!=0){
                    Gson gson = new Gson();
                    String real_password = gson.toJson(account1.get(0));//将list集合转化为json字符串
                    JSONObject jsonObj = JSONObject.parseObject(real_password);
                    real_password=jsonObj.getString("password");//json字符串取password的值
                    Log.d("LoginActivity","password is "+real_password);
                    if(real_password!=null && password.equals(real_password)){
                        //登录成功跳转
                        addData(accountedit.getText().toString());//将登录成功的账号保存到数据库中，用于下拉列表的显示
                        //保存密码和账号到登录框里面
                        editor=pref.edit();
                        editor.putBoolean("remember_password_account",true);
                        editor.putString("account",account);
                        editor.putString("password",password);
                        editor.apply();
                        Intent intent_1=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent_1);
                        Intent intent_2=new Intent("com.example.broadcasttest.GET_ACCOUNTID");
                        intent_2.setPackage(getPackageName());
                        intent_2.putExtra("accountId",account);
                        sendBroadcast(intent_2,null);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,"账号或者密码无效！",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"该账号不存在，请前往注册！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        createaccount=(TextView)findViewById(R.id.create_account_text);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_2=new Intent(LoginActivity.this,CreateAccountActivity.class);
                startActivity(intent_2);
            }
        });
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        while (!flag){
            initWedget();
            flag=true;
        }
    }
    //    初始化界面控件
    public void initWedget(){
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle data=msg.getData();
                switch (msg.what){
                    case 1:
                        //选中下拉项，下拉框消失
                        int selIndex = data.getInt("selIndex");
                        accountedit.setText(datas.get(selIndex));
                        user_head_image.setImageBitmap(head_shots.get(selIndex));
                        popupWindow.dismiss();
                        passwordedit.setVisibility(View.VISIBLE);
                        login.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        //移除下拉项数据
                        int delIndex = data.getInt("delIndex");
                        final int delIndex_1=delIndex;
                        final String deldata=datas.get(delIndex);
                        final AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("删除账号");
                        builder.setMessage("你确定删除账号"+deldata+"？");
                        builder.setCancelable(false);
                        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("LoginActivity:","data= "+deldata);
                                LitePal.deleteAll(LoginData.class,"data=?",deldata);
                                datas.remove(delIndex_1);
                                //刷新下拉列表
                                optionsItemAdapter.notifyDataSetChanged();
                                if(datas.size()==0){
                                    popupWindow.dismiss();
                                    pull.setVisibility(View.GONE);
                                    passwordedit.setVisibility(View.VISIBLE);
                                    login.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.setCancelable(true);
                            }
                        });
                        builder.show();
                        break;
                }
            }
        };
        parent=(LinearLayout)findViewById(R.id.parent);
        parent_width=parent.getWidth();
        pull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag && datas.size()!=0){
                    pull.setImageDrawable(getResources().getDrawable(R.drawable.pull_1));
                    popupWindow.showAsDropDown(parent,0,-3);//点击三角形按钮时显示
                    passwordedit.setVisibility(View.GONE);
                    login.setVisibility(View.GONE);
                }
            }
        });
        initPopupWindow();
    }

    public void initPopupWindow(){
        initDatas();
        View loginwindow=(View)this.getLayoutInflater().inflate(R.layout.options_list,null);
        listView=(ListView)loginwindow.findViewById(R.id.list);
        optionsItemAdapter=new OptionsItemAdapter(LoginActivity.this,handler,datas,head_shots);
        listView.setAdapter(optionsItemAdapter);
        popupWindow=new PopupWindow(loginwindow,parent_width, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setOutsideTouchable(true);//点击popupWindow以外的区域，自动关闭popupWindow
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));//点击返回键或者其他时设置透明颜色背景
        //监听popupwindow是否弹出，如果否(例如按了后退键)，则密码框和按钮显示出来
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                passwordedit.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                pull.setImageDrawable(getResources().getDrawable(R.drawable.pull));
            }
        });
        if(datas.size()==0){
            popupWindow.dismiss();
            pull.setVisibility(View.GONE);
            passwordedit.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
        }else {
            pull.setVisibility(View.VISIBLE);
        }
    }

    private void initDatas(){
        List<LoginData> loginDataList=LitePal.findAll(LoginData.class);
        for (LoginData loginData:loginDataList){
            datas.add(loginData.getData());
        }
        for (String data:datas){
            List<Account> account1= LitePal.select("headshot")
                    .where("accountid like ?",data)
                    .find(Account.class);
            byte[] images=account1.get(0).getHeadshot();
            Bitmap bitmap=BitmapFactory.decodeByteArray(images, 0, images.length);
            head_shots.add(bitmap);
        }
    }
    public void addData(String data){
        List<LoginData> loginDataList=LitePal.where("data=?",data)
                .find(LoginData.class);
        if(loginDataList.size()==0){
            LoginData loginData=new LoginData();
            loginData.setData(data);
            loginData.save();
            Intent intent_3=new Intent("com.example.curriculum_design.REFURBISH_LIST");
            localBroadcastManager=LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent_3);
        }
    }

    //将Bitmap图片转化为byte
    private byte[]img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
//    //将byte[]图片转化为Bitmap
//    private Bitmap Bytes2Bimap(byte[] b){
//        if(b.length!=0){
//            return BitmapFactory.decodeByteArray(b, 0, b.length);
//        } else {
//            return null;
//        }
//    }

}
