package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    private Button returnbutton;
    private EditText telephone;
    private EditText name;
    private EditText password;
    private Button register;
    private CheckBox provision;
    private ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        returnbutton=(Button)findViewById(R.id.return_button_1);
        telephone=(EditText)findViewById(R.id.telephone_text);
        name=(EditText)findViewById(R.id.accountname_text);
        password=(EditText)findViewById(R.id.password_text);
        register=(Button)findViewById(R.id.register);
        provision=(CheckBox) findViewById(R.id.provision);
        toggleButton=(ToggleButton)findViewById(R.id.tb_create_password_visibility);

        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(CreateAccountActivity.this,LoginActivity.class);
                startActivity(intent_1);
            }
        });

        telephone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String inputStr = telephone.getText().toString();
                if (isTelphoneValid(inputStr)){
                    telephone.setError(null);
                    telephone.setSelection(inputStr.length());//将光标移至文字末尾

                }else {
                    telephone.setError("手机号码输入不正确");
                    telephone.setSelection(inputStr.length());//将光标移至文字末尾

                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String inputStr = password.getText().toString();
                if (isPasswordValid(inputStr)){
                    password.setError(null);
                    password.setSelection(inputStr.length());//将光标移至文字末尾
                }else {
                    password.setError("密码至少六位");
                    password.setSelection(inputStr.length());//将光标移至文字末尾
                }
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//显示密码
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {//隐藏密码
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                password.setSelection(password.length());//每次显示或者关闭时，密码显示编辑的线不统一在最后，下面是为了统一
            }
        });

        CompoundButton.OnCheckedChangeListener myListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    final AlertDialog.Builder builder=new AlertDialog.Builder(CreateAccountActivity.this);
                    builder.setTitle("服务协议和隐私政策");
                    builder.setMessage("请你务必审慎阅读、充分理解“服务协议和隐私政策”各条款，包括不限于：为了向你提供即时通讯" +
                            "、内容分享等服务，我们需要收集你的设备信息、操作日志等个人信息。如你同意，请点击“同意”开始接受我们的服务");
                    builder.setCancelable(false);
                    builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setCancelable(true);
                        }
                    });
                    builder.setNegativeButton("暂不使用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            provision.setChecked(false);
                        }
                    });
                    builder.show();
                }
            }
        };
        provision.setOnCheckedChangeListener(myListener);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aname=name.getText().toString();
                String apassword=password.getText().toString();
                String atelephone=telephone.getText().toString();
                if(!provision.isChecked()){
                    Toast.makeText(CreateAccountActivity.this,"请先接受服务协议与隐私政策",Toast.LENGTH_SHORT).show();
                }else if(!isTelphoneValid(atelephone)){
                  Toast.makeText(CreateAccountActivity.this,"手机号码输入错误",Toast.LENGTH_SHORT).show();
                }else if(!isPasswordValid(apassword)){
                    Toast.makeText(CreateAccountActivity.this,"密码输入至少6位",Toast.LENGTH_SHORT).show();
                } else{
                    LitePal.getDatabase();
                    boolean isCreate=initAccount(aname,apassword,atelephone);
                    if(isCreate){
                        int aaccountid = LitePal.max(Account.class, "accountid", int.class);
                        Intent intent_2=new Intent(CreateAccountActivity.this,CreateSuccessActivity.class);
                        intent_2.putExtra("account_id",String.valueOf(aaccountid));
//                        Log.d("successful",String.valueOf (aaccountid));
                        startActivity(intent_2);
                    }else{
                        Toast.makeText(CreateAccountActivity.this,"该手机号已经创建了账户",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    private boolean initAccount(String name,String password,String telephone){
        Account account=new Account();
        account.setName(name);
        account.setPassword(password);
        account.setTelephone(telephone);
        String accountID=queryAccount(telephone);
        if(accountID.equals("error")){
            return false;
        }else{
            int accountID_int=Integer.parseInt(accountID);
            account.setAccountid(accountID_int);
            Bitmap bmp= BitmapFactory.decodeResource(getResources(), R.drawable.user_head);
            account.setHeadshot(img(bmp));//刚开始注册成功设置默认头像
            account.save();
            return true;
        }
    }
    private String queryAccount(String phonenumber){
        String accountId;
        List<Account> accounts=LitePal.findAll(Account.class);
        if(accounts.isEmpty()){
            accountId="95000001";
            return accountId;
        }else{
            List<Account> accounts_1=LitePal.select("telephone")
                    .where("telephone=?",phonenumber)
                    .find(Account.class);
            if(accounts_1.size()==0) {
                int idnum = LitePal.max(Account.class, "accountid", int.class)+1;
                accountId=String.valueOf (idnum);
                Log.d("CreateAccountActivity",accountId);
                return accountId;
            }else{
                accountId="error";
                return accountId;
            }
        }
    }

    //将Bitmap图片转化为字节
    private byte[]img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //校验手机号码输入是否正确
    private boolean isTelphoneValid(String account) {
        if (account == null) {
            return false;
        }
        // 首位为1, 第二位为3-9, 剩下九位为 0-9, 共11位数字
        String pattern = "^[1]([3-9])[0-9]{9}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(account);
        return m.matches();
    }
    //校验密码是否输入正确
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
