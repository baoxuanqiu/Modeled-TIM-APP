package com.example.curriculum_design;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MyActivity extends BaseActivity {
    private Button setting;
    private TextView message;
    private TextView my;
    private TextView myname;
    private LinearLayout telephone;
    private ImageView myimage;
    private Dialog dialog;
    private View inflate;
    private TextView camera;
    private TextView pic;
    private TextView cancel;
    private Uri imageUri;
    private static final int TAKE_PHOTO=1;
    private static final int CHOOSE_PHOTO=2;
    private Boolean isHide=false;
    private String accountID;//该用户的ID
    private Bitmap accountImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }

        setting=(Button)findViewById(R.id.setting_button);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
        message=(TextView)findViewById(R.id.text_message_1);
        my=(TextView)findViewById(R.id.text_my_1);
        my.setSelected(true);
        message.setSelected(false);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyActivity.this,MainActivity.class);
                startActivity(intent);
                message.setSelected(false);
            }
        });
        //设置“我的”界面的用户名
        myname=(TextView)findViewById(R.id.myname_text);
        myimage=(ImageView)findViewById(R.id.user_head_image);
        GetAccountId getAccountId=new GetAccountId();
        accountID=getAccountId.account_ID;//获取用户accountid
        String real_name=getAccountId.getAccount_Name();
        myname.setText(real_name);
        accountImage=getAccountId.getAccount_Image();
        myimage.setImageBitmap(accountImage);

        telephone=(LinearLayout)findViewById(R.id.telephone_linear);
        telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(MyActivity.this,PhoneActivity.class);
                startActivity(intent_1);
            }
        });

        myimage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showBottomChoose(v);
        }
    });
    }

    //返回主界面不退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showBottomChoose(View view){
        dialog = new Dialog(this,R.style.DialogTheme);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.bottom_choose_photo, null);
        //初始化控件
        camera = (TextView) inflate.findViewById(R.id.camera);
        pic = (TextView) inflate.findViewById(R.id.picture);
        cancel = (TextView) inflate.findViewById(R.id.cancel);
        camera.setOnClickListener(new View.OnClickListener() {//选择拍照
            @Override
            public void onClick(View v) {
                dialog.hide();
                isHide=true;
                //创建File对象，用于存储拍照后的图片
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){//将File对象转换为Uri对象
                    imageUri= FileProvider.getUriForFile(MyActivity.this,"com.example.curriculum_design.fileprovider",outputImage);
                }else{
                    imageUri=Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent_2=new Intent("android.media.action.IMAGE_CAPTURE");
                intent_2.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent_2,TAKE_PHOTO);
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {//选择从相册选择
            @Override
            public void onClick(View v) {
                dialog.hide();
                isHide=true;
                //申请获取相机的权限
                if(ContextCompat.checkSelfPermission(MyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MyActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {//选择取消
            @Override
            public void onClick(View v) {
//                Toast.makeText(MyActivity.this,"You clicked the cancel",Toast.LENGTH_SHORT).show();
                dialog.hide();
                isHide=true;
            }
        });
        dialog.setContentView(inflate);//将布局设置给Dialog
        Window dialogWindow = dialog.getWindow();//获取当前Activity所在的窗体
        dialogWindow.setGravity( Gravity.BOTTOM);//设置Dialog从窗体底部弹出
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();//获得窗体的属性
//        lp.y = 20;//设置Dialog距离底部的距离
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;//这两句是为了保证textView可以水平满屏
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);//将属性设置给窗体
        dialog.show();//显示对话框
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode==RESULT_OK){
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        myimage.setImageBitmap(bitmap);
                        Account account=new Account();
                        account.setHeadshot(img(bitmap));
                        account.updateAll("accountid=?",accountID);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    //打开相册
    private void openAlbum(){
        Intent intent_3=new Intent("android.intent.action.GET_CONTENT");
        intent_3.setType("image/*");
        startActivityForResult(intent_3,CHOOSE_PHOTO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isHide==true){
            dialog.dismiss();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"你关闭了打开相册的权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //document类型的Uri处理方式
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String seletion=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,seletion);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //content类型的Uri处理方式
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //file类型的Uri，直接获取路径即可
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String path=null;
        //获取真实的图片路径
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            myimage.setImageBitmap(bitmap);
            Account account=new Account();
            account.setHeadshot(img(bitmap));
            account.updateAll("accountid=?",accountID);
        }else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    //将Bitmap图片转化为字节形式
    private byte[]img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
