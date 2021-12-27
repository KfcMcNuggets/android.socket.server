package com.example.myapplication;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;

public class Crypter {
    
    private String keyForServer;
    private String msg;
    private String sender;
    private String senderId;
    private String receiver;
    
    Crypter (Message message){
      
        this.keyForServer = "123456789serverk";
        this.msg = message.msg;
        this.sender = message.sender;
        this.receiver = message.receiver;
        this. senderId = message.senderId;

    }  

    public Message encrypter() throws Exception{
           
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        byte[] key = keyForServer.getBytes("UTF-8");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivparameterspec = new IvParameterSpec(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
        byte[] cipherText = cipher.doFinal(receiver.getBytes("UTF8"));
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(cipherText);
        
        return new Message(sender, senderId, msg, encryptedText);

    }

    public Message decrypter() throws Exception{
           
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        byte[] key = keyForServer.getBytes("UTF-8");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivparameterspec = new IvParameterSpec(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
        Base64.Decoder decoder = Base64.getMimeDecoder();
        byte[] cipherText = decoder.decode(receiver.getBytes("UTF8"));
        String decryptedText = new String(cipher.doFinal(cipherText), "UTF-8");
        
        return new Message(sender, senderId, msg, decryptedText);

    }
}
