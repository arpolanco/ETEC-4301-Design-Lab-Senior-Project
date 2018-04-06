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
    
    public static void handlePhone(Socket phone) throws IOException{
        System.out.println("Phone connected. Checking for drones...");
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