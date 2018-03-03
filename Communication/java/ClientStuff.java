package server;

import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

//https://github.com/bytedeco/javacv
//https://github.com/bytedeco/javacv/blob/master/LICENSE.txt

/*
You may use this work under the terms of either the Apache License,
Version 2.0, or the GNU General Public License (GPL), either version 2,
or any later version, with "Classpath" exception (details below).

You don't have to do anything special to choose one license or the other
and you don't have to notify anyone which license you are using. You are
free to use this work in any project (even commercial projects) as long
as the copyright header is left intact.
*/

class ClientStuff extends Thread{
	private final Socket client;
	public ClientStuff(Socket c){
            client = c;
            System.out.println(client.toString());
	}
        
        @Override
	public void run(){
            try{
                VideoStream removeThis = new VideoStream(640, 480);
                InputStream stream = client.getInputStream();
                FrameGrabber grabber = new FFmpegFrameGrabber(stream);
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage frame;
                grabber.setFrameRate(30.0);
                grabber.setFormat("H264");
                grabber.start();
                long time;
                while(true){
                    //time = System.nanoTime();
                    frame = converter.convert(grabber.grab());
                    removeThis.draw(frame);
                    //System.out.println(1/(((float)(System.nanoTime()-time))/1000000.0));
                }
            }catch(IOException e){
                System.out.println("Error creating socket");
            }
	}
}