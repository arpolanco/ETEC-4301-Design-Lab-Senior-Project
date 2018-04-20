package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

class Server{
    final static int TOTAL_DRONES = 1; //should be 4 in the final thing
    final static int BUFFER_SIZE = 4096;
    static ArrayList<Drone> drones = new ArrayList<>();
    static int currentDrone = -1;
    static int currentControllers = 0;
    final static boolean testingTelemetry = false;
    
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
        if(currentDrone == -1){
            System.out.println("No drones online. Closing this connection");
            phone.close();
        }else if(currentControllers == TOTAL_DRONES){
            System.out.println("All the slots are taken, sorry");
            phone.close();
        }else{
            drones.get(currentDrone).attachController(phone);
            currentControllers++;
        }        
    }
    
    public static void handleDrone(Socket drone) throws IOException{
        System.out.println("Drone connected. Checking if max reached...");
        if(currentDrone == TOTAL_DRONES){
            System.out.println("Max drones reached. Closing this connection...");
            drone.close();
        }else{
            Drone droneyBoy = new Drone(drone, currentDrone);
            droneyBoy.start();
            drones.add(droneyBoy);
            currentDrone++;
        }
    }
    
    public static void main(String[] args){
	try{
            //arraylist of threads
            ServerSocket host = new ServerSocket(1101);
            Socket newConnection;
            BufferedReader connectionTypeReader;
            
            String connectionType;
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
                        System.out.println("Unknown connection type");
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