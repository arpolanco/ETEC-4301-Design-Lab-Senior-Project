package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Server{
    final static int TOTAL_DRONES = 4; //should be 4 in the final thing
    final static int BUFFER_SIZE = 4096;
    static ArrayList<Drone> drones = new ArrayList<>();
    static int currentDrone = 0;
    static int currentControllers = 0;
    final static boolean testingTelemetry = false;
    
    final static Pattern droneIDGetter = Pattern.compile("drone=(\\d+)");
    final static Pattern kppGetter = Pattern.compile("K\\+P\\+P=(\\d+)");
    
    public static void handlePhone(Socket phone) throws IOException{
        System.out.println("Phone connected. Checking for drones...");
        if(testingTelemetry){
            InputStream input = phone.getInputStream();
            final int THRUST = 0x80;    //0b10 000000;
            final int QUIT = 0x40;   //0b01 000000;
            
            final int FIRE = 0x30;   //0b00 11 0000;
            final int PITCH = 0x0;   //0b00 00 0000;
            final int ROLL = 0x10;   //0b00 01 0000;
            final int YAW = 0x20;    //0b00 10 0000;
            
            
            final float maxThrust = 180.0f;
            final float maxPitch = 40.0f;
            final float maxYaw = 40.0f;
            final float maxRoll = 40.0f;
            
            
            int telemetry;
            while(true){
                
                telemetry = input.read();
                //decoding
                if(telemetry == FIRE){
                    System.out.println("Kapow!");
                }else if(telemetry == QUIT){
                    System.out.println("Shutting down...");
                    phone.close();
                    System.exit(0);
                }else if((telemetry & THRUST) == THRUST){
                    System.out.print("Thrust: ");
                    System.out.println(String.format("%7s", Integer.toBinaryString(telemetry & 0x7f)).replace(' ', '0'));
                    //System.out.println(((telemetry&0x7f) / 63.0f)*maxThrust);
                }else{
                    if((telemetry & YAW) == YAW){
                        System.out.print("Yaw: ");
                        //System.out.println(((telemetry&0xf) / 14.0f)*maxYaw);
                    }else if((telemetry & ROLL) == ROLL){
                        System.out.print("Roll: ");
                        //System.out.println(((telemetry&0xf) / 14.0f)*maxRoll);
                    }else{ //pitch (masking 0 is hard)
                        System.out.print("Pitch: ");
                        //System.out.println(((telemetry&0xf) / 14.0f)*maxPitch);
                    }            
                    System.out.println(String.format("%4s", Integer.toBinaryString(telemetry & 0xf)).replace(' ', '0'));
                }
            }
        }
        if(drones.size() < 1){
            System.out.println("No drones online. Closing this connection");
            phone.close();
        }else if(currentControllers == TOTAL_DRONES){
            System.out.println("All the slots are taken, sorry");
            phone.close();
        }else{
            drones.get(currentDrone-1).attachController(phone);
            currentControllers++;
        }        
    }
    
    public static void handleDrone(Socket drone) throws IOException{
        System.out.println("Drone connected. Checking if max reached...");
        if(currentDrone == TOTAL_DRONES){
            System.out.println("Max drones reached. Closing this connection...");
            drone.close();
        }else{
            System.out.print("Drone ");
            System.out.print(currentDrone);
            System.out.println(" connected");
            Drone droneyBoy = new Drone(drone, currentDrone);
            droneyBoy.start();
            drones.add(droneyBoy);
            currentDrone++;
        }
    }
    
    final static byte QUIT = 'q';
    final static byte PITCH = 0b01000000;
    final static byte ROLL = 0b01010000;
    final static byte YAW = 0b01100000;
    
    final static byte PROPORTIONAL = 0b01000000;
    final static byte INTEGRAL = 0b01000100;
    final static byte DIFFERENTIAL = 0b01001000;
    
    public static void handleGet(Socket request, String requestType) throws FileNotFoundException, IOException{
        requestType = requestType.split(" ")[1];
        if(requestType.length() > 1){
            if(requestType.contains("favicon")){
                return;
            }
            requestType = requestType.split("\\?")[1];
            String[] info = requestType.split("&");
            if(info.length > 1){
                //System.out.println(Arrays.toString(info));
                int droneID = -1;
                byte sendType = 0;
                byte sendValue = 0;
                
                Matcher matcher;
                for(String data : info){
                    String[] value = data.split("=");
                    if(value.length > 1){
                        if(value[0].equals("drone")){
                            System.out.print("Drone ID requested: ");
                            System.out.println(value[1]);
                            droneID = Integer.parseInt(value[1]);
                            if(droneID > drones.size()-1){
                                System.out.print("ERROR: drone ");
                                System.out.print(value[1]);
                                System.out.println(" does not exist!");
                                return;
                            }
                        }else if(droneID < 0){
                            System.out.println("ERROR: Tried to do operation without specifying drone!");
                            return;
                        }else{
                            switch(value[0]){
                                case "KPP":
                                    System.out.print("Sending KPP of ");
                                    sendType = PITCH | PROPORTIONAL;
                                    break;
                                case "KPI":
                                    System.out.print("Sending KPI of ");
                                    sendType = PITCH | INTEGRAL;
                                    break;
                                case "KPD":
                                    System.out.print("Sending KPD of ");
                                    sendType = PITCH | DIFFERENTIAL;
                                    break;
                                case "KRP":
                                    System.out.print("Sending KRP of ");
                                    sendType = ROLL | PROPORTIONAL;
                                    break;
                                case "KRI":
                                    System.out.print("Sending KRI of ");
                                    sendType = ROLL | INTEGRAL;
                                    break;
                                case "KRD":
                                    System.out.print("Sending KRD of ");
                                    sendType = ROLL | DIFFERENTIAL;
                                    break;
                                case "KYP":
                                    System.out.print("Sending KYP of ");
                                    sendType = YAW | PROPORTIONAL;
                                    break;
                                case "KYI":
                                    System.out.print("Sending KYI of ");
                                    sendType = YAW | INTEGRAL;
                                    break;
                                case "KYD":
                                    System.out.print("Sending KYD of ");
                                    sendType = YAW | DIFFERENTIAL;
                                    break;
                                default:
                                    System.out.print("Other request: ");
                                    System.out.println(Arrays.toString(value));
                                    break;
                            }
                            System.out.print(value[1]);
                            System.out.print("to drone ");
                            System.out.println(droneID);
                            sendValue = (byte) Integer.parseInt(value[1]);
                            try {
                                drones.get(droneID).sendKValue(sendType, sendValue);
                            } catch (InterruptedException ex) {
                                System.out.println(ex);
                                return;
                            }
                        }
                        
                    }
                }
            }
        }
        int count;
        byte[] buffer = new byte[2048];
        request.getOutputStream().write("HTTP/1.0 200 OK\r\n\r\n".getBytes());
        FileInputStream webpage = new FileInputStream("settings.html");
        while((count = webpage.read(buffer)) > 0){
          request.getOutputStream().write(buffer, 0, count);
        }
        request.getOutputStream().flush();
        request.close();
        webpage.close();
    }
    
    public static void main(String[] args){
	try{
            //arraylist of threads
            ServerSocket host = new ServerSocket(1101);
            Socket newConnection;
            BufferedReader connectionTypeReader;
            
            String connectionType;
            String requestType;
            System.out.println(host.toString());

            while(true){
                newConnection = host.accept();
                connectionTypeReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(newConnection.getInputStream(), BUFFER_SIZE)));
                connectionType = connectionTypeReader.readLine(); //blocking
                switch (connectionType) {
                    case "DRONE":
                        handleDrone(newConnection);
                        break;
                    case "PHONE":
                        handlePhone(newConnection);
                        break;
                    default:
                        requestType = connectionType.substring(0, 4);
                        if(requestType.equals("GET ")){
                            System.out.println(connectionType);
                            handleGet(newConnection, connectionType);
                        }else if(requestType.equals("POST")){
                            System.out.println("Received POST request. Send data to specified drone and kill this connection");
                            System.out.println(connectionType);
                            newConnection.close();
                        }else{
                            System.out.println("Unknown connection type " + requestType);
                        }
                        break;
                }
            }
            /*
            while(currentDrones < TOTAL_DRONES){
                Socket newDrone = socket.accept();
                System.out.println("Drone connected");
                Drone droneThread = new Drone(newDrone);
                droneThread.start();
                drones.add(droneThread);
                currentDrones++;
            }

            //probably have a mutex to prevent phone from connecting as pi
            System.out.println("Max drones reached, now looking for controllers");
            while(currentControllers < currentDrones){
                //detect whether pi or smartphone instead of just assuming based on order
                //problem: a phone can connect twice somehow
                Socket newPhone = socket.accept();
                drones.get(currentControllers).attachController(newPhone);
                currentControllers++;

            }

            System.out.println("Max controllers reached, joining all drone threads");
            //probably have some check for if a phone disconnects. if disconnect,
            //check for reconnect and attach to the drone missing a controller
            for(int i=0;i<TOTAL_DRONES;i++){
                try{
                    drones.get(i).join();
                }catch (InterruptedException ex){
                    System.out.println(ex);
                    System.exit(-1);
                }
            }
            */
	}catch(IOException e){
            System.out.println(e);
            System.exit(-1);
	}
    }
}