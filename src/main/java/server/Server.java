package server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;


public class Server {
    
    private Queue<String> outgoingQueue;
    private Queue<String> incomingQueue;
    
    private Queue<String> positionQueue;
    private Queue<String> collisionQueue;
    private BlockingQueue<Integer> keypressQueue;
    
    private Thread outgoingPacketManager;
    private Thread incomingPacketManager;
    
    private PacketSender sender;
    private PacketReceiver receiver;
    
    public Server() throws IOException {
        
        outgoingQueue = new ConcurrentLinkedQueue<String>();
        incomingQueue = new ConcurrentLinkedQueue<String>();
        keypressQueue = new LinkedBlockingDeque<>();
        this.positionQueue = new LinkedBlockingDeque<>();
        this.collisionQueue = new LinkedBlockingDeque<>();
        
        initialisePacketManagers();
        
        this.sender = new PacketSender(NetworkUtility.GROUP, NetworkUtility.CLIENT_PORT, this.outgoingQueue);
        this.receiver = new PacketReceiver(NetworkUtility.GROUP, NetworkUtility.SERVER_PORT, this.incomingQueue);
        
        this.incomingPacketManager.start();
        this.outgoingPacketManager.start();
        this.sender.start();
        this.receiver.start();
    }
    
    /**
     * Initialises the packet managers
     */
    private void initialisePacketManagers() {
        //puts inputs from queues into the outgoing queue - not sure this one closes
        this.incomingPacketManager = new Thread() {
            public void run() {
                Integer key;
                while (!isInterrupted()) {
                    if (incomingQueue.isEmpty()) {
                        continue;
                    }
                    System.out.println(incomingQueue.poll());
//                    key = Integer.valueOf(incomingQueue.poll());
//                    keypressQueue.add(key);
                }
            }
        };
        
        //reads inputs from the incoming queue into the relevant queue - position queue or collision queue
        //could be parallelised in adding positions and collisions to the queue - hogging was unlikely as collisions
        //occur a lot less than position updates
        this.outgoingPacketManager = new Thread() {
            public void run() {
                while (!isInterrupted()) {
                    try {
                        if (positionQueue.isEmpty()) {
                            continue;
                        } else {
                            String data = collisionQueue.poll();
                            data = NetworkUtility.COLLISIONS_CODE + data;
                            outgoingQueue.add(data);
                        }
                        if (positionQueue.isEmpty()) {
                            continue;
                        } else {
                            String data = positionQueue.poll();
                            data = NetworkUtility.POSITION_CODE + data;
                            outgoingQueue.add(data);
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        
                    }
                    
                }
            }
        };
    }
    
    public static void main(String[] args) throws IOException {
    
        Server s = new Server();
        System.out.println("Server Running");
        
        
    }
}
