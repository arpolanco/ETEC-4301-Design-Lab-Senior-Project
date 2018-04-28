package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.net.Socket;
import java.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
*/

public class Client extends Thread{
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private Pixmap frame;
    //private byte[] positionBuffer;
    private Image droneFrame;
    Protocol protocol;
    SocketHints hints;
    
    final String HOST = "206.21.94.104";
    final int PORT = 1101;
    
    public Client(){
        //initialize socket
        /*
        hints = new SocketHints();
        hints.connectTimeout = 0;
        hints.socketTimeout = 0;
        hints.keepAlive = true;
        socket = Gdx.net.newClientSocket(protocol, HOST, PORT, hints);
        */
        openSocket();
        sendMessage("PHONE");
        //buffer = new byte[640 * 480 * 5];
        //shout type to server
    }
    
    private void openSocket(){
        
        try {
            socket = new Socket(HOST, PORT);
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException ex) {
            System.exit(-1);
        }
        
    }
    
    public Texture getImage(){
        /*try {
            //maybe this sends a request to the server, which then returns the
            //current frame when it was called
            int bytesRead = -1;
            //while((bytesRead = input.read(buffer)) > 0){
            //}
            //frame = new Pixmap(buffer, 0, bytesRead);
            input.skip(500000000);
        } catch (IOException ex) {
            System.exit(-1);
        }*/
        return new Texture(frame);
    }
    
    public boolean sendData(){
        //byte[] data = {'h', 'i'};
        if (output == null){
            return false;
        }else if (socket.isClosed()){
            openSocket();
            return false;
        }
        return sendMessage(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date()));
        /*
        try {
            output.write('G');
            output.write('\n');
            output.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
        */
    }
    
    private boolean sendMessage(String string){
        try {
            output.write(string.getBytes());
            output.write('\n');
            output.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    public boolean sendByte(byte value){
        try {
            output.write(value);
            output.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
        
    }
}
