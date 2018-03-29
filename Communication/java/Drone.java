package server;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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

class Drone extends Thread{
	private final Socket client;
        private Socket controller;
        private OutputStream output;
        
	public Drone(Socket c){
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
                /*  todo: there's a sort of "build up" of frames at first where
                    nothing gets displayed at first, but then many frames flash
                    at once
                */
                while(true){
                    time = System.nanoTime();
                    frame = converter.convert(grabber.grab());
                    removeThis.draw(frame, 1000000000.0/((((double)(System.nanoTime()-time)))));
                    sendFrame(frame);
                    //System.out.println(1/(((float)(System.nanoTime()-time))/1000000.0));
                }
            }catch(IOException e){
                System.out.println(e);
                System.exit(-1);
            }
	}
        
        public void attachController(Socket controller_){
            controller = controller_;
            try {
                output = controller.getOutputStream();
            } catch (IOException ex) {
                System.out.println(ex);
                System.exit(-1);
            }
            System.out.print("Phone connected: ");
            System.out.println(controller);
        }
        
        private void sendFrame(BufferedImage frame) throws IOException{
            if(output == null) return;
            System.out.println("Attempting to send frame...");
            //https://stackoverflow.com/questions/3211156/how-to-convert-image-to-byte-array-in-java#3211685
            //if it tries to write data, it dies. why?
            //output.write(((DataBufferByte) frame.getRaster().getDataBuffer()).getData());
            //maybe don't use sendFrame() every single frame. just do it whenever
            //the phone client sends a request for it
            output.write(new byte[500000000]);
            //output.write(new byte[50]);
            output.flush();
            System.out.println("Sent frame");
            /*
            try {
                if(output == null) return;
                //seems to freeze here. may be better to send raw bytes? pi has
                //no involvement here after all
                
                //also consider just having libgdx in this project as well. just
                //send the same object type that you expect on the client side
                ImageIO.write(frame, "png", output);
                //flush
            } catch (IOException ex) {
                System.out.println("sendFrame() error");
                System.exit(-1);
            }
            */
        }
}