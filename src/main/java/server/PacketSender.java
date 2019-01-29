package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class PacketSender extends Thread {
    private InetAddress group;
    private MulticastSocket socket;
    private String networkInterface;
    private int port;
    
    /**
     * @param group
     * @throws IOException
     */
    public PacketSender(InetAddress group, int port) throws IOException {
        this.group = group;
        this.socket = new MulticastSocket();
        this.networkInterface = Utility.getInterface();
        this.port = port;
    }
    
    /**
     * Sends packets to the server
     *
     * @param message the message which we want to send to the server
     * @throws IOException caused by the packets and and interfaces.
     */
    public void send(String message) throws IOException {
        byte[] buf = new byte[256];
        
        buf = message.getBytes();
        DatagramPacket sending = new DatagramPacket(buf, 0, buf.length, group, this.port);
        
        
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
