package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

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
    
    final String HOST = "206.21.94.238";
    final int PORT = 1101;
    
    public Client() throws IOException{
        //initialize socket
        /*
        hints = new SocketHints();
        hints.connectTimeout = 0;
        hints.socketTimeout = 0;
        hints.keepAlive = true;
        socket = Gdx.net.newClientSocket(protocol, HOST, PORT, hints);
        */
        socket = new Socket(HOST, PORT);
        input = socket.getInputStream();
        output = socket.getOutputStream();
        output.write("phone\n".getBytes());
        output.flush();
        //buffer = new byte[640 * 480 * 5];
        //shout type to server
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
        if (output == null) return false;
        try {
            //output.write(data);
            output.write("I AM A CONTROLLER\n".getBytes());
            output.flush();
            return true;
        } catch (IOException ex) {
            return false;
            //System.exit(-1);
        }
    }
}
