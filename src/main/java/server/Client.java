package server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * Client class which creates the appropriate senders and starts them.
 */
public class Client {
    
    private static String collisionCode = NetworkingData.collisionsCode;
    private static String positionCode = NetworkingData.positionCode;
    private static String stopCode = NetworkingData.stopCode;
    private Queue<String> outgoingQueue;
    private Queue<String> incomingQueue;
    private BlockingQueue<Integer> keypressQueue;
    
    public Client() {
        outgoingQueue = new ConcurrentLinkedQueue<String>();
        incomingQueue = new ConcurrentLinkedQueue<String>();
        keypressQueue = new LinkedBlockingDeque<>();
        
    }
    public static void main(String[] args) throws IOException {
    
        PacketSender sender = new PacketSender(Utility.GROUP, Utility.SERVER_PORT);
        PacketReceiver receiver = new PacketReceiver(Utility.GROUP, Utility.CLIENT_PORT, sender);
        receiver.start();
        sender.start();
        sender.send("1");
        
    }
}
