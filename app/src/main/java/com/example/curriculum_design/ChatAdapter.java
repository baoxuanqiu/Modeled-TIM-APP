package com.example.curriculum_design;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chat> mChatList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftChat;
        TextView rightChat;
        CardView chat_cardView_1;
        CardView chat_cardView_2;
        ImageView chat_image_1;
        ImageView chat_image_2;
         public ViewHolder(View view){
             super(view);
             leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
             rightLayout=(LinearLayout)view.findViewById(R.id.right_layout);
             leftChat=(TextView)view.findViewById(R.id.left_chat);
             rightChat=(TextView)view.findViewById(R.id.right_chat);
             chat_cardView_1=(CardView)view.findViewById(R.id.chat_cardView_1);
             chat_cardView_2=(CardView)view.findViewById(R.id.chat_cardView_2);
             chat_image_1=(ImageView)view.findViewById(R.id.chat_image_1);
             chat_image_2=(ImageView)view.findViewById(R.id.chat_image_2);
         }
    }

    public ChatAdapter(List<Chat> chatList){
        mChatList=chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat=mChatList.get(position);
        if(chat.getType()==Chat.TYPE_RECEIVED){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.chat_cardView_1.setVisibility(View.VISIBLE);
            holder.chat_cardView_2.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftChat.setText(chat.getContent());
            holder.chat_image_1.setImageBitmap(chat.getImage());
        }else if (chat.getType()==Chat.TYPE_SENT){
            holder.leftLayout.setVisibility(View.GONE);
            holder.chat_cardView_1.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.chat_cardView_2.setVisibility(View.VISIBLE);
            holder.rightChat.setText(chat.getContent());
            holder.chat_image_2.setImageBitmap(chat.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }
}
