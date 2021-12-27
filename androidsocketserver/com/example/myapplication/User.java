package com.example.myapplication;
import java.net.Socket;

import java.util.ArrayList;
import java.io.*;
import static com.example.myapplication.Server.userList;
import static com.example.myapplication.Server.userListMutex;

public class User extends Thread implements Serializable {
    
    static ArrayList<Message> messagesin = new ArrayList<>();
    static ArrayList<Message> messagesout = new ArrayList<>(); 
    private static final long serialVersionUID = 6529685098267757690L;
    private String userId;
    private String username;
    private ArrayList<String> pmMessages = new ArrayList<>();
    public User(String username, String userId) throws IOException{
      
        this.username = username;
        this.userId = userId;
        start();
    }

        @Override
        public void run(){
            while (true) {
                
                Socket socket = Server.connectedDevices.get(this.userId);
              
                try{
                   
                    InputStream inputStream = socket.getInputStream();
                    System.out.println("Before create objinputstream, socket is " + socket + " and inputstream is " + inputStream );
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);  
                    System.out.println("before readobj"); 
                    Message msg = new Crypter((Message) objectInputStream.readObject()).decrypter();
                    messagesin.add(msg);
                    System.out.println("Messages in = " + messagesin.size());
                    System.out.println("Taked message from  " + msg.sender + " to " + msg.receiver);    
                    messageWorker(msg);
                
                } catch (Exception e) {
                    
                    System.out.println("Errror in User");
                    System.out.println(e);
                
                    try {
                       
                        socket.close();
                        System.out.println("user disconnected");
                        
                        userListMutex.writeLock().lock();
                        userList.remove(this);
                        userListMutex.writeLock().unlock();
                        
                        Server.connectedDevicesLock.writeLock().lock();
                        Server.connectedDevices.remove(this.userId);
                        Server.connectedDevicesLock.writeLock().unlock();
                        
                        userListMutex.readLock().lock();
                        for (String id : Server.connectedDevices.keySet() ) {
                            synchronized (Server.connectedDevices){
                                try {
                                
                                    OutputStream outputStream = Server.connectedDevices.get(id).getOutputStream();
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                    objectOutputStream.writeObject(userList);
                                    System.out.println("Sended userlist" + userList.get(0).getUsername());
                                } catch(Exception b) {
                                    System.out.println("Error in sending userlist");
                                    System.out.println(b);
                                }
                            }
                        }
                        userListMutex.readLock().unlock();
                        
                        break;
                    
                    } catch (Exception a) {
                        System.out.println(a);
                    }
                }
            }                 
        }
        
            public void messageWorker(Message msg)
            {
                   
                   userListMutex.readLock().lock();
                   try{
                       
                    for(User user : userList){
                            if(user.getUserId().equals(msg.receiver)){
                                
                                synchronized (user) {
                                Socket socket =  Server.connectedDevices.get(user.userId); 
                                OutputStream outputStream =  socket.getOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                objectOutputStream.writeObject(new Crypter(msg).encrypter());
                                }

                                messagesout.add(msg);
                                System.out.println("Messages out = " + messagesout.size());
                                System.out.println("Sended messege from " + msg.sender + " to " + user.getUserId() + "    " + Server.connectedDevices.get(userId));
                                System.out.println(msg + "      " + msg.msg + "     ");
                            
                            }else{
                                System.out.println(msg.receiver + " is not "  + user.getUserId());
                            }
                    }
                }catch (Exception x ){
                    System.out.println(x);
                }
                userListMutex.readLock().unlock();

            }


        public void setUserId(String userId){
            this.userId = userId;
        }

        public String getUserId(){
            return this.userId;
        }

        public String getUsername(){
            return this.username;
        }
}
