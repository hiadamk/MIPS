package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Class which will holds shared utility data for classes.
 */
public class Utility {
    
    public static int SERVER_PORT = 4446;
    public static int CLIENT_PORT = 4445;
    public static InetAddress GROUP;
    
    static {
        try {
            GROUP = InetAddress.getByName("239.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @return
     * @throws IOException
     */
    public static String getInterface() throws IOException {
        
        Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
        while (faces.hasMoreElements()) {
            NetworkInterface iface = faces.nextElement();
            if (iface.isLoopback() || !iface.isUp())
                continue;
            
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                System.out.println();
                return (addr.toString());
            }
        }
        return "";
    }
}
