package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Button return_button;
    private List<Chat> chatList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView chatRecyclerView;
    private ChatAdapter adapter;
    private String friendID;
    private String friendName;
    private TextView chat_with_name;
    private Bitmap myImage;
    private Bitmap friendImage;
    private List<String> chatContentList=new ArrayList<>();
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        localBroadcastManager=LocalBroadcastManager.getInstance(this);

        Intent intent_2=getIntent();
        friendID=intent_2.getStringExtra("friendID");
        List<Contact> contactList= LitePal
                .select("contact_name","contact_head")
                .where("contact_ID=?",friendID)
                .find(Contact.class);
        friendName=contactList.get(0).getContact_name();
        byte[] images_1=contactList.get(0).getContact_head();
        friendImage = BitmapFactory.decodeByteArray(images_1, 0, images_1.length);//朋友的头像
        GetAccountId getAccountId=new GetAccountId();
        myImage=getAccountId.getAccount_Image();//本人的头像
        chat_with_name=(TextView)findViewById(R.id.chat_with_name);
        chat_with_name.setText(friendName);
        showChatContent();

        return_button=(Button)findViewById(R.id.return_button_5);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_4=new Intent("com.example.curriculum_design.SEND_WHO_CHAT_WITH_ME_NOW");
                String finalChatContent=findFinalContent();
                intent_4.putExtra("final_data",finalChatContent);
                intent_4.putExtra("friendID",friendID);
                localBroadcastManager.sendBroadcast(intent_4);//发送本地广播
                Intent intent_1=new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent_1);
            }
        });

        inputText=(EditText)findViewById(R.id.input_text);
        send=(Button)findViewById(R.id.send_button);
        chatRecyclerView=(RecyclerView)findViewById(R.id.chat_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        adapter=new ChatAdapter(chatList);
        chatRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ChatActivity","already send");
                String content=inputText.getText().toString();
                if(!"".equals(content)){
                    Chat chat=new Chat(content,Chat.TYPE_SENT,myImage);
                    content=writeLineToFile(content);//将换行符转化为“\n”
                    saveToFile(String.valueOf(Chat.TYPE_SENT)+" "+content+"\n");
                    chatList.add(chat);
                    adapter.notifyItemInserted(chatList.size()-1);
                    chatRecyclerView.scrollToPosition(chatList.size()-1);
                    inputText.setText("");
                }
            }
        });

    }

    private void showChatContent(){
        load(friendName);//读取聊天记录
        if (chatContentList.size()>0){
            for (int i=0;i<chatContentList.size();i++){
//            Log.d("ChatActivity",chatContentList.get(i));
                char[] chars=chatContentList.get(i).toCharArray();
                String content=chatContentList.get(i).substring(2,chatContentList.get(i).length());
                content=readFileToLine(content);//将换行符显示出来
                if (Character.isDigit(chars[0])){
                    int num = Integer.parseInt(String.valueOf(chars[0]));
                    if (num==Chat.TYPE_RECEIVED){
                        Chat chat=new Chat(content,Chat.TYPE_RECEIVED,friendImage);
                        chatList.add(chat);
                    }else if (num==Chat.TYPE_SENT){
                        Chat chat=new Chat(content,Chat.TYPE_SENT,myImage);
                        chatList.add(chat);
                    }
                }
            }
        }else{
            initChat();
        }
    }

    private void initChat(){
        String content;
        content="你好，请问哪位？";
        Chat chat2=new Chat(content,Chat.TYPE_SENT,myImage);
        saveToFile(String.valueOf(Chat.TYPE_SENT)+" "+content+"\n");
        chatList.add(chat2);
        content="你好呀";
        Chat chat1=new Chat(content,Chat.TYPE_RECEIVED,friendImage);
        saveToFile(String.valueOf(Chat.TYPE_RECEIVED)+" "+content+"\n");
        chatList.add(chat1);
        content="我叫"+friendName+"，认识你很高兴";
        Chat chat3=new Chat(content,Chat.TYPE_RECEIVED,friendImage);
        saveToFile(String.valueOf(Chat.TYPE_RECEIVED)+" "+content+"\n");
        chatList.add(chat3);
        content="你好呀";
        Chat chat4=new Chat(content,Chat.TYPE_RECEIVED,friendImage);
        saveToFile(String.valueOf(Chat.TYPE_RECEIVED)+" "+content+"\n");
        chatList.add(chat4);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            Intent intent_5=new Intent("com.example.curriculum_design.SEND_WHO_CHAT_WITH_ME_NOW");
            String finalChatContent=findFinalContent();
            intent_5.putExtra("final_data",finalChatContent);
            intent_5.putExtra("friendID",friendID);
            localBroadcastManager.sendBroadcast(intent_5);//发送本地广播

            Intent intent_3=new Intent(ChatActivity.this,MainActivity.class);
            startActivity(intent_3);
            return true;
        }
        return false;
    }

    //写文件
    private void saveToFile(String inputText){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=openFileOutput(friendName, Context.MODE_APPEND);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (writer!=null) {
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //读文件
    private void load(String friendname){
        FileInputStream in=null;
        BufferedReader reader=null;
        chatContentList.clear();
        try {
            in=openFileInput(friendname);
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null){
                chatContentList.add(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader!=null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String writeLineToFile(String data){
        String result = data.replaceAll("\r|\n", "\\\\n");
        return result;
    }
    private String readFileToLine(String data){
        String result=data.replaceAll("\\\\n","\n");
        return result;
    }

    private String findFinalContent(){
        load(friendName);//读取聊天记录
        String finalChatContent;
        if (chatContentList.size()>0){
            String finaldata=chatContentList.get(chatContentList.size()-1);
            finalChatContent=finaldata.replaceAll("\r|\n", " ");
        }else{
            finalChatContent="";
        }
        return finalChatContent;
    }

}
