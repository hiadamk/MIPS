package server;

import utils.Input;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//input switched from string to input form in this stage
public class ServerGameplayHandler {

    private Queue<Input> inputQueue;
    private Queue<String> outgoingQueue;
    private Queue<String> incomingQueue;

//    private BlockingQueue<Integer> keypressQueue;
    
    //    private Thread outgoingPacketManager;
    private Thread incomingPacketManager;
    
    private PacketSender sender;
    private PacketReceiver receiver;
    
    private ArrayList<InetAddress> ipStore;

    
    private int playerCount;
    
    public ServerGameplayHandler(ArrayList<InetAddress> ips, int numPlayers,
        Queue<Input> inputQueue, Queue<String> outputQueue) throws IOException {

        this.inputQueue = inputQueue;
        outgoingQueue = outputQueue;
        incomingQueue = new ConcurrentLinkedQueue<>();
        this.playerCount = numPlayers;
        initialisePacketManagers();
        
       // outgoingQueue.add("POS"); //why
        this.ipStore = ips;
        this.sender = new PacketSender(NetworkUtility.CLIENT_DGRAM_PORT, this.outgoingQueue, ipStore);
        this.receiver = new PacketReceiver(NetworkUtility.SERVER_DGRAM_PORT, this.incomingQueue);
        this.incomingPacketManager.start();
        this.sender.start();
        this.receiver.start();
    }


    
    public static void main(String[] args) throws IOException {

//        ServerGameplayHandler s = new ServerGameplayHandler();
        System.out.println("ServerGameplayHandler Running");
    }

    /**
     * Initialises the packet managers
     */
    private void initialisePacketManagers() {
        // puts inputs from queues into the outgoing queue - not sure this one closes
        this.incomingPacketManager =
                new Thread() {
                    public void run() {
                        Integer key;
                        while (!isInterrupted()) {
                            if (incomingQueue.isEmpty()) {
                                continue;
                            }
                            System.out.println(incomingQueue.peek());
                            inputQueue.add(Input.fromString(incomingQueue.poll()));

                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
        
        // reads inputs from the incoming queue into the relevant queue - position queue or collision
        // queue
        // could be parallelised in adding positions and collisions to the queue - hogging was unlikely
        // as collisions
        // occur a lot less than position updates
    
        //Don't think this is needed anymore as the packet sender is directly watching the outgoing queue which the telemetry adds to.

//        this.outgoingPacketManager =
//                new Thread() {
//                    public void run() {
//                        while (!isInterrupted()) {
//                            try {
//                                if (po.isEmpty()) {
//                                    continue;
//                                } else {
//                                    String data = collisionQueue.poll();
//                                    data = NetworkUtility.COLLISIONS_CODE + data;
//                                    outgoingQueue.add(data);
//                                }
//                                if (positionQueue.isEmpty()) {
//                                    continue;
//                                } else {
//                                    String data = positionQueue.poll();
//                                    data = NetworkUtility.POSITION_CODE + data;
//                                    outgoingQueue.add(data);
//                                }
//                                Thread.sleep(50);
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                };
  }
}
