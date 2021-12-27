package com.example.myapplication;
import java.io.Serializable;

public class Message implements Serializable {
    String senderId;
    String sender;
    String msg;
    String receiver;
    Message (String sender, String senderId, String msg, String receiver){
        this.senderId = senderId;
        this.sender = sender;
        this.msg = msg;
        this.receiver = receiver;
    }
}
