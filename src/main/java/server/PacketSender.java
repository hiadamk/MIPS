package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Queue;

public class PacketSender extends Thread {
    private InetAddress group;
    private MulticastSocket socket;
    private String networkInterface;
    private int port;
    private boolean running = true;
    private Queue<String> feedQueue;
//    private byte[] buf;
    
    /**
     * Constructs a Packet Sender object
     * @param group The group we want to bind to
     * @param port The port we want to listen to
     * @param  feedQueue The queue we want to send items from.
     * @throws IOException Thrown by the multicast socket
     */
    public PacketSender(InetAddress group, int port, Queue<String> feedQueue) throws IOException {
        this.group = group;
        this.socket = new MulticastSocket();
        this.networkInterface = NetworkUtility.getInterface();
        this.port = port;
        this.feedQueue = feedQueue;
    }
    
    
    /**
     * Looks at the queue of messages and sends them to the needed recipient.
     */
    @Override
    public void run() {
        super.run();
        running = true;
        try {
            while (running) {
                if (feedQueue.isEmpty()) {
                    continue;
                }
                String s = feedQueue.poll();
                System.out.println("About to send " + s);
                send(s);
            }
        
        } catch (IOException e) {
            running = false;
            System.out.println("Server closed");
            socket.close();
        }
        
    }
    
    /**
     * Takes a String and copies it into the outgoing packet byte buffer,
     * as well as adding PREFIX and SUFFIX strings to the buffer edges.
     *
     * @param s
     */
    private byte[] prepareBuf(String s) { // will truncate
        
        byte[] buf;
        String toSend = NetworkUtility.PREFIX + s + NetworkUtility.SUFFIX;
        buf = toSend.getBytes(NetworkUtility.CHARSET);
        
        return buf;
    }
    
    
    /**
     * Sends packets to designated port in the group
     *
     * @param message the message which we want to send to the server
     * @throws IOException caused by the packets and and interfaces.
     */
    public void send(String message) throws IOException {
        byte[] buf = prepareBuf(message);
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
                    System.out.println("Packet sent");
                    return;
                }
                
            }
        }
        
    }
    
    
    /**
     * Stops thread execution.
     */
    
    public void shutdown() {
        this.running = false;
        socket.close();
    }
}
