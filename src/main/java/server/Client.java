package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {
    
    
    public static void main(String[] args) throws IOException {
        
        MulticastSocket socket = new MulticastSocket(4445);
        InetAddress group = InetAddress.getByName("239.255.255.255");
        
        ClientSender sender = new ClientSender(group);
        ClientReceiver receiver = new ClientReceiver(group);
        receiver.start();
        sender.start();
        sender.send("1");
        
    }
}
