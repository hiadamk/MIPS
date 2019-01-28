package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class ClientSender extends Thread {
    
    private InetAddress group;
    private MulticastSocket socket;
    private String networkInterface;
    
    public ClientSender(InetAddress group) throws IOException {
        this.group = group;
        this.socket = new MulticastSocket();
        this.networkInterface = Utility.getInterface();
        System.out.println(this.networkInterface);
    }
    
    public void send(String message) throws IOException {
        byte[] buf = new byte[256];
        
        buf = message.getBytes();
        DatagramPacket sending = new DatagramPacket(buf, 0, buf.length, group, 4446);
        
        
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
    
    @Override
    public void run() {
        super.run();
//        while(true){
//            try{
//                this.send("1");
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//        }
    }
}
