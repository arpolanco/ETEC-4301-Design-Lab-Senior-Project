package server;

import java.net.*;
import java.io.*;


class Server{
    public static void main(String[] args){
	//arraylist of threads
	try{
	//arraylist of threads
        ServerSocket socket = new ServerSocket(1101);
            System.out.println(socket.toString());
            while(true){
                Socket newClient = socket.accept();
                System.out.println("Client connected");
                ClientStuff clientThread = new ClientStuff(newClient);
                clientThread.start();
                //clientThread.join();
            }
	}catch(IOException e){
            System.out.println("Error creating socket");
	}
    }
}