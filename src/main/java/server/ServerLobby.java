package server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServerLobby {
    
    private int playerCount;
    private ArrayList<InetAddress> playerIPs;
    private boolean gameStarted;
    
    public ServerLobby() {
        pinger.start();
    }
    
    Thread pinger = new Thread(() -> {
        try {
            MulticastSocket socket = new MulticastSocket(4445);
            InetAddress group = InetAddress.getByName("239.255.255.255");
            
            
            byte[] buf;
            String message = "PING";
            buf = message.getBytes();
            DatagramPacket sending = new DatagramPacket(buf, 0, buf.length, group, 4446);
            
            while (true) {
                Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
                a:
                while (faces.hasMoreElements()) {
                    NetworkInterface iface = faces.nextElement();
                    if (iface.isLoopback() || !iface.isUp())
                        continue;
                    
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        socket.setInterface(addr);
                        socket.send(sending);
                        
                    }
                }
                Thread.sleep(100);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    });
    
    public void acceptConnections() {
        //TODO Implement
    }
    
    public void gameStart() {
        //TODO Implement
    }
    
    public void gameStop() {
        //TODO Implement
    }
}
