package server;

import java.io.IOException;

/**
 * Main server class which starts the sender and receiver classes
 */
public class Server {
    
    public static void main(String[] args) throws IOException {
    
    
        ServerSender sender = new ServerSender(Utility.GROUP);
        ServerReceiver receiver = new ServerReceiver(Utility.GROUP, sender);
        sender.start();
        receiver.start();
        System.out.println("Server Running");
        
        
    }
}
