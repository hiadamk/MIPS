package server;

import java.io.IOException;


/**
 * Client class which creates the appropriate senders and starts them.
 */
public class Client {
    
    
    public static void main(String[] args) throws IOException {
    
        ClientSender sender = new ClientSender(Utility.GROUP);
        ClientReceiver receiver = new ClientReceiver(Utility.GROUP);
        receiver.start();
        sender.start();
        sender.send("1");
        
    }
}
