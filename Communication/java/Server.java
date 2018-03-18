package server;

import java.net.*;
import java.io.*;

class Server{
    public static void main(String[] args){
	try{
	//arraylist of threads
        ServerSocket socket = new ServerSocket(1101);
            System.out.println(socket.toString());
            //while(true){
                Socket newClient = socket.accept();
                System.out.println("Client connected");
                ClientStuff clientThread = new ClientStuff(newClient);
                //detect whether pi or smartphone instead of just assuming based on order
                clientThread.start();
                try{
                    Socket newPhone = socket.accept();
                    clientThread.attachController(newPhone);
                    clientThread.join();
                }catch(InterruptedException ex){
                    System.exit(-1);
                }
            //}
	}catch(IOException e){
            System.out.println("Error creating socket");
            System.exit(-1);
	}
    }
}