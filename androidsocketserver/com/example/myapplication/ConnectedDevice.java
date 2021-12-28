package com.example.myapplication;
import java.io.*;
import java.net.Socket;
import java.lang.String;
import java.lang.Throwable;
import static com.example.myapplication.Server.userList;
import static com.example.myapplication.Server.userListMutex;
import static com.example.myapplication.Server.createId;;


public class ConnectedDevice extends Thread implements Serializable{
   
    protected Socket socket;
   
    public ConnectedDevice (Socket socket) throws IOException {
        this.socket = socket;
        start();
    }

    @Override
    public void run() {
        
        try {
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message msg = (Message) objectInputStream.readObject();
                System.out.println(msg.msg);
                String userId = "Id#" + createId();
                User user = new User(msg.sender, userId);
                
                Server.connectedDevicesLock.writeLock().lock();
                Server.connectedDevices.put(user.getUserId(), socket);
                Server.connectedDevicesLock.writeLock().unlock();
                
                userListMutex.writeLock().lock();
                userList.add(user);
                userListMutex.writeLock().unlock();

                System.out.println("created new username " + user.getUsername() + userId);
                
                try {
                    
                    OutputStream outputStream =  socket.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(user.getUserId());
                 
                } catch (Exception e) {
                    System.out.println("Error in sending userId " + e);
                }

                Server.connectedDevicesLock.readLock().lock();

                for (String id : Server.connectedDevices.keySet() ) {
                   synchronized (Server.connectedDevices){
                        try {
                        
                            OutputStream outputStream = Server.connectedDevices.get(id).getOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                            objectOutputStream.writeObject(userList);
                            System.out.println("Sended userlist" + userList.get(0).getUsername());
                        } catch(Exception e) {
                            System.out.println("Error in sending userlist");
                            System.out.println(e);
                        }
                    }
                }
                Server.connectedDevicesLock.readLock().unlock();

        } catch (Throwable e) {
            System.out.println("Error in connected device");
            try{
                socket.close();
                System.out.println("Disconnect device");
                e.printStackTrace();
            }catch(Exception a){
                System.out.println("Error in disconnect " + a);
            }
        }
    }

    
}
