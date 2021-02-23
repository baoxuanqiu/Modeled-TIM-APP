package com.example.curriculum_design;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class ChatListItem extends LitePalSupport {
    private long id;
    private String chat_item_id;
    private byte[] chat_item_image;
    private String chat_item_name;
    private String chat_item_content;
    private String chat_item_time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChat_item_id() {
        return chat_item_id;
    }

    public byte[] getChat_item_image() {
        return chat_item_image;
    }

    public String getChat_item_content() {
        return chat_item_content;
    }

    public String getChat_item_name() {
        return chat_item_name;
    }

    public String getChat_item_time() {
        return chat_item_time;
    }

    public void setChat_item_id(String chat_item_id) {
        this.chat_item_id = chat_item_id;
    }

    public void setChat_item_content(String chat_item_content) {
        this.chat_item_content = chat_item_content;
    }

    public void setChat_item_image(byte[] chat_item_image) {
        this.chat_item_image = chat_item_image;
    }

    public void setChat_item_name(String chat_item_name) {
        this.chat_item_name = chat_item_name;
    }

    public void setChat_item_time(String chat_item_time) {
        this.chat_item_time = chat_item_time;
    }
}
