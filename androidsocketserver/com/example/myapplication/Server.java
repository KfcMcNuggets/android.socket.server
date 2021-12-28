package com.example.myapplication;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
   
    public static ArrayList<User> userList = new ArrayList<>();
    public static ReadWriteLock userListMutex = new ReentrantReadWriteLock(true);
    public static HashMap<String, Socket> connectedDevices = new HashMap<>();
    public static ReadWriteLock connectedDevicesLock = new ReentrantReadWriteLock(true);
    private static AtomicInteger idCounter = new AtomicInteger();
     
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(4004);
        System.out.println("Opend");
            try {   
                while(true){
                    Socket socket = server.accept();
                    System.out.println("new connection " + socket);
                    try{
                      new ConnectedDevice(socket);
                    } catch (IOException e){
                        System.out.println("Error in server main");
                        socket.close();
                    }
                }
            
            }finally {
                server.close();
                System.out.println("Closed");
            }
            
    
                

    }


    public static int createId(){
        return idCounter.getAndIncrement();
    }

}



  