package com.example.curriculum_design;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.litepal.LitePal;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private TextView message;
    private TextView my;
    private Button contacts;
    private Button add;
    private Dialog dialog;
    private View inflate;
    private LinearLayout create_group_chat;
    private LinearLayout add_friends;
    private SimpleAdapter simpleAdapter;
    List<Map<String,Object>> listItems=new ArrayList<Map<String, Object>>();
    private String itemFriendID;
    private String itemContent;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        message=(TextView)findViewById(R.id.text_message);
        my=(TextView)findViewById(R.id.text_my);
        contacts=(Button)findViewById(R.id.contacts_button);
        add=(Button)findViewById(R.id.add_button);
        localBroadcastManager=LocalBroadcastManager.getInstance(this);//获取实例
        message.setSelected(true);
        my.setSelected(false);
        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MyActivity.class);
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRightChoose(v);
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(MainActivity.this,ContactsActivity.class);
                startActivity(intent_1);
            }
        });


        ListView chat_listView=(ListView)findViewById(R.id.chat_listView);
        simpleAdapter=new SimpleAdapter(this,listItems,R.layout.chat_list_item,new String[]{"chat_image","chat_name","chat_content","chat_time","chat_id"},new int[]{R.id.chat_contact_image,R.id.chat_contact_name,R.id.chat_content,R.id.chat_time_textView,R.id.chat_contact_id});
        //设置simpleAdapter可以直接显示bitmap图片
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if((view instanceof ImageView) & (data instanceof Bitmap)){
                    ImageView imageView = (ImageView)view;
                    Bitmap mBitmap = (Bitmap)data;
                    imageView.setImageBitmap(mBitmap);
                    return true;
                }
                return false;
            }
        });
        chat_listView.setAdapter(simpleAdapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.curriculum_design.SEND_WHO_CHAT_WITH_ME_NOW");
        localReceiver=new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);//注册本地广播监听器
        showChatItem();
        chat_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> map=(Map<String,Object>)MainActivity.this.listItems.get(position);
                Object object=map.get("chat_id");//指定Map集合中的键名。
                if(object instanceof String){//判断键对应的值是否为String类型
                    String chatId=(String)object;
                    Log.d("MainActivity",chatId);
                    Intent intent_4=new Intent(MainActivity.this,ChatActivity.class);
                    intent_4.putExtra("friendID",chatId);
                    startActivity(intent_4);
                }
            }
        });
    }

    private void showRightChoose(View view){
        dialog = new Dialog(this,R.style.DialogTheme);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.right_choose_add, null);
        dialog.setContentView(inflate);//将布局设置给Dialog
        Window dialogWindow = dialog.getWindow();//获取当前Activity所在的窗体
        dialogWindow.setGravity( Gravity.RIGHT);//设置Dialog从窗体底部弹出
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();//获得窗体的属性
        lp.y = -680;//设置Dialog距离顶部的距离
        lp.x=40;//设置Dialog距离右边的距离
        dialogWindow.setAttributes(lp);//将属性设置给窗体
        dialog.show();//显示对话框
        create_group_chat=(LinearLayout)findViewById(R.id.create_group_chat);
        add_friends=(LinearLayout)findViewById(R.id.add_friends);
//        create_group_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //添加点击后发生的事件
//            }
//        });
//        add_friends.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //添加点击后发生的事件
//            }
//        });

    }
    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            itemContent=intent.getStringExtra("final_data");
            itemFriendID=intent.getStringExtra("friendID");
            addChatItem();//先初始化，之后可以删
        }
    }

    private void addChatItem(){
        if (itemContent.equals("")){
            Log.d("MainActivity","This friend do not chat with me");
        }else {
            char[] chars=itemContent.toCharArray();
            String content=itemContent.substring(2,itemContent.length());
            List<Contact> contactList= LitePal
                    .select("contact_name","contact_head")
                    .where("contact_ID=?",itemFriendID)
                    .find(Contact.class);
            String friendName=contactList.get(0).getContact_name();
            byte[] images_1=contactList.get(0).getContact_head();
            List<ChatListItem> chatListItemList=LitePal
                    .where("chat_item_id=?",itemFriendID)
                    .find(ChatListItem.class);
            if (chatListItemList.size()>0){
                LitePal.deleteAll(ChatListItem.class,"chat_item_id=?",itemFriendID);
            }
            if (Character.isDigit(chars[0])) {
                int num = Integer.parseInt(String.valueOf(chars[0]));
                if (num == Chat.TYPE_RECEIVED) {
                    content=friendName+": "+content;
                }
            }
            Calendar calendar = Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String time=year+"-"+month+"-"+day;
            ChatListItem chatListItem=new ChatListItem();
            chatListItem.setChat_item_id(itemFriendID);//添加聊天项的朋友id
            chatListItem.setChat_item_name(friendName);//添加聊天项的朋友名字
            chatListItem.setChat_item_image(images_1);//添加聊天项的朋友头像
            chatListItem.setChat_item_content(content);//添加聊天项的朋友聊天内容
            chatListItem.setChat_item_time(time);//添加聊天项的朋友聊天时间
            chatListItem.save();
            showChatItem();
        }

    }

    private void showChatItem(){
        listItems.clear();
        List<ChatListItem> chatListItems=LitePal
                .order("id desc")
                .find(ChatListItem.class);
        for (ChatListItem chatListItem1:chatListItems){
            Bitmap friendImage = BitmapFactory.decodeByteArray(chatListItem1.getChat_item_image(), 0, chatListItem1.getChat_item_image().length);//朋友的头像
            Map<String,Object> listItem_1=new HashMap<String,Object>();
            listItem_1.put("chat_image",friendImage);
            listItem_1.put("chat_name",chatListItem1.getChat_item_name());
            listItem_1.put("chat_content",chatListItem1.getChat_item_content());
            listItem_1.put("chat_time",chatListItem1.getChat_item_time());
            listItem_1.put("chat_id",chatListItem1.getChat_item_id());
            listItems.add(listItem_1);
//                listItem_1.clear();
        }
        simpleAdapter.notifyDataSetChanged();//刷新
    }

    //退出使退回手机主界面
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        dialog.dismiss();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
