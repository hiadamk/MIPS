package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;


/**
 * Receives messages sent from clients to the server
 */
public class ServerReceiver extends Thread {
    
    private InetAddress group;
    private MulticastSocket socket;
    private ServerSender sender;
    
    /**
     * Constructs the class from the multicast group and the sender which we will use to send a message to the client
     *
     * @param group  The multicast group we are sending messages to
     * @param sender The server sender class we will send messages to the client with
     * @throws IOException Caused by the sockets.
     */
    public ServerReceiver(InetAddress group, ServerSender sender) throws IOException {
        this.group = group;
        this.socket = new MulticastSocket(4446);
        this.sender = sender;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp())
                continue;
            
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                socket.setInterface(addr);
                socket.joinGroup(group);
            }
        }
    }
    
    /**
     *  Continuously listens to the the port which messages are sent to the server on and sends a response to the client
     */
    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                
                String received = new String(packet.getData());
                System.out.println("Server received -> " + received);
                
                sender.send("Hello " + received);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
