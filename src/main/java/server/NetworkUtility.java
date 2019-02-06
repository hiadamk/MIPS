package server;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * Class which will holds shared utility data for classes.
 */
public class NetworkUtility {
    
    public static final int SERVER_M_PORT = 4446;
    public static final int CLIENT_M_PORT = 4445;
    public static final int SERVER_DGRAM_PORT = 3000;
    public static final int CLIENT_DGRAM_PORT = 3001;
    
    public static final String PREFIX = "SMSG";
    public static final String SUFFIX = "EMSG";
    public static final String POSITION_CODE = "POS";
    public static final String COLLISIONS_CODE = "COS";
    public static final String STOP_CODE = "EXIT";
    public static final int STRING_LIMIT = 24;
    public static final Charset CHARSET = StandardCharsets.US_ASCII;
    
    public static InetAddress GROUP;
    
    static {
        try {
            GROUP = InetAddress.getByName("239.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the correct network interface to send/receive messages on
     *
     * @return the string of the network interface which will be used for multicasting
     * @throws IOException Thrown by Network Interface
     */
    public static String getInterface() throws IOException {
        
        Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
        while (faces.hasMoreElements()) {
            NetworkInterface iface = faces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }
            
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                System.out.println();
                return (addr.toString());
            }
        }
        return "";
    }
    
    public static InetAddress getServerIP() throws IOException {
        System.out.println("Getting the server address");
        MulticastSocket socket = new MulticastSocket(CLIENT_M_PORT);
        InetAddress group = NetworkUtility.GROUP;
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
        
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        System.out.printf("Server Address: " + packet.getAddress());
        return packet.getAddress();
        
    }
}
