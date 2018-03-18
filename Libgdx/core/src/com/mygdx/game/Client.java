package com.mygdx.game;


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
*/

public class Client {
    private Socket socket;
    private InputStream input;
    private Pixmap frame;
    private byte[] buffer;
    //Protocol protocol;
    //SocketHints hints;
    
    final String HOST = "206.21.94.161";
    final int PORT = 1101;
    
    public Client() throws IOException{
        //initialize socket
        //socket = Gdx.net.newClientSocket(protocol, HOST, PORT, hints);
        //this gives an error, but why?
        socket = new Socket(HOST, PORT);
        input = socket.getInputStream();
        buffer = new byte[640 * 480 * 5];
        //shout type to server
    }
    
    public Texture getImage(){
        int bytesRead;
        try {
            bytesRead = input.read(buffer);
            frame = new Pixmap(buffer, 0, bytesRead);
        } catch (IOException ex) {
            System.exit(-1);
        }
        return new Texture(frame);
    }
}
