package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

class Server{
    public static void main(String[] args){
        final int TOTAL_DRONES = 1; //should be 4 in the final thing
        ArrayList<Drone> drones = new ArrayList<>();
        int currentDrones = 0;
        int currentControllers = 0;
	try{
	//arraylist of threads
        ServerSocket socket = new ServerSocket(1101);
            System.out.println(socket.toString());
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
	}catch(IOException e){
            System.out.println("Some sort of socket error");
            System.exit(-1);
	}
    }
}