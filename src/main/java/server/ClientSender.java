package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;


/**
 * Class whose sole purpose is to send packets from the client to the server
 */
public class ClientSender extends Thread {
    
    private InetAddress group;
    private MulticastSocket socket;
    private String networkInterface;
    
    /**
     * @param group
     * @throws IOException
     */
    public ClientSender(InetAddress group) throws IOException {
        this.group = group;
        this.socket = new MulticastSocket();
        this.networkInterface = Utility.getInterface();
    }
    
    /**
     * Sends packets to the server
     * @param message the message which we want to send to the server
     * @throws IOException caused by the packets and and interfaces.
     */
    public void send(String message) throws IOException {
        byte[] buf = new byte[256];
        
        buf = message.getBytes();
        DatagramPacket sending = new DatagramPacket(buf, 0, buf.length, group, Utility.SERVER_PORT);
        
        
        Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
        while (faces.hasMoreElements()) {
            NetworkInterface iface = faces.nextElement();
            if (iface.isLoopback() || !iface.isUp())
                continue;
            
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr.toString().equals(networkInterface)) {
                    socket.setInterface(addr);
                    socket.send(sending);
                    return;
                }
                
            }
        }
        
    }
    
    /**
     * Default run method, currently empty as threading functionality may not be needed in the class.
     */
    @Override
    public void run() {
        super.run();
    }
}
