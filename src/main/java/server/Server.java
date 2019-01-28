package server;

import java.io.IOException;
import java.net.InetAddress;

public class Server {
    
    public static void main(String[] args) throws IOException {
        
        InetAddress group = InetAddress.getByName("239.255.255.255");
        
        ServerSender sender = new ServerSender(group);
        ServerReceiver receiver = new ServerReceiver(group, sender);
        sender.start();
        receiver.start();
        System.out.println("Server Running");
        
        
    }
}
