package server;

import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        private OutputStream droneOutput;
        private InputStream droneInput;
        private BufferedReader inputReader;
        private byte[] droneInputBuffer;
        private final Semaphore controllerLock = new Semaphore(1);
        private final int BUFFER_SIZE = 4096;
        private final int ID;
        
	public Drone(Socket c, int id_){
            client = c;
            ID = id_;
            System.out.print("Drone: ");
            System.out.println(client.toString());
	}
        
        @Override
	public void run(){
            //try{
                VideoStream removeThis = new VideoStream(640, 480);
                /*
                InputStream stream = client.getInputStream();                
                FrameGrabber grabber = new FFmpegFrameGrabber(stream);
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage frame;
                grabber.setFrameRate(30.0);
                grabber.setFormat("H264");
                grabber.start();
                long time;
                */
                /*  todo: there's a sort of "build up" of frames at first where
                    nothing gets displayed at first, but then many frames flash
                    at once
                */
                long time;
                byte bob = 'X';
                while(true){
                    /*
                    time = System.nanoTime();
                    frame = converter.convert(grabber.grab());
                    removeThis.draw(frame, 1000000000.0/((((double)(System.nanoTime()-time)))));
                    */
                    /*
                    time = System.nanoTime();
                    System.out.println("Waiting to receive data from phone...");
                    while(!receiveData()){
                        //
                    }
                    System.out.println("Sending data to drone...");
                    sendData(bob);
                    System.out.println("Waiting to receive data from drone...");
                    receiveDataFromDrone();
                    System.out.print("Time for trip: ");
                    System.out.println((((float)(System.nanoTime()-time))/1000000000.0));
                    */
                    receiveData();
                    //sendFrame(frame);
                    //System.out.println(1/(((float)(System.nanoTime()-time))/1000000.0));
                }
            //}catch(IOException e){
                //System.out.println(e);
                //System.exit(-1);
            //}
	}
        
        private void sendData(byte bob){
            try {
                client.getOutputStream().write(bob);
                client.getOutputStream().flush();
            } catch (IOException ex) {
                System.out.println(ex);
                System.exit(-1);
            }
            
        }
        
        private void receiveDataFromDrone(){
            try {
                System.out.println(new BufferedReader(new InputStreamReader(new BufferedInputStream(client.getInputStream(), BUFFER_SIZE))).readLine());
            } catch (IOException ex) {
                System.out.println(ex);
                System.exit(-1);
            }
        }
        
        public boolean attachController(Socket controller_){
            try {
                controllerLock.acquire();
                controller = controller_;
                /*
                if(Arrays.equals(controller.getInetAddress().getAddress(), client.getInetAddress().getAddress())){
                    System.out.println(Arrays.toString(controller.getInetAddress().getAddress()));
                    //controllerLock.release();
                    //return false;
                    //System.exit(-1);
                }
                if(controller.getLocalAddress().getHostAddress().equals(client.getLocalAddress().getHostAddress())){
                    System.out.println(controller.getLocalAddress().getHostAddress());
                    System.exit(-1);
                }
                */
                try {
                    droneOutput = controller.getOutputStream();
                    droneInput = new BufferedInputStream(controller.getInputStream(), BUFFER_SIZE);
                    inputReader = new BufferedReader(new InputStreamReader(droneInput));
                } catch (IOException ex) {
                    System.out.println(ex);
                    System.exit(-1);
                }
                System.out.print("Phone: ");
                System.out.println(controller);
            } catch (InterruptedException ex) {
                System.out.println(ex);
                System.exit(-1);
            }
            
            controllerLock.release();
            return true;
        }
        
        private boolean receiveData(){
            try {
                controllerLock.acquire();
            } catch (InterruptedException ex) {
                System.out.println(ex);
                System.exit(-1);
            }
            if(controller == null){
                controllerLock.release();
                return false;
            }
            controllerLock.release();
            
            //boolean gotAnything = false;
            //problem: if drone connects as drone and phone connects as phone, only
            //the phone's constructor message is received. if the phone connects as
            //both things, the messages are received as normal
            try {
                System.out.println(inputReader.readLine());
            } catch (IOException ex) {
                System.out.println(ex);
                System.exit(-1);
            }
            return true;
            //possible solutions:
            //open up a second, identical socket for duplex communication
            //see if sending messages the other way still works
            //seems to work if phone connects as pi and controller for some reason
            //if(gotAnything) System.out.println(new String(droneInputBuffer));
            //need to decide order of data sent and received
        }
        
        public InputStream bob(){
            return droneInput;
        }
        
        private void sendFrame(BufferedImage frame) throws IOException{
            /*
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
            */
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