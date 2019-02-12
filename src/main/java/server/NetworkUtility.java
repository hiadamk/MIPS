package server;

import utils.enums.Direction;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
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
    
    private static DecimalFormat coordFormat = new DecimalFormat("000.000");
    
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
    
    /**
     * Checks for a message in the group and gets the IP of the server from that.
     *
     * @return the IP of the server
     * @throws IOException Thrown by use of multicast sockets.
     */
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
    
    /**
     * Makes the packet to send to the client for the position of entities.
     *
     * @param entityID  The unique ID of the entity
     * @param direction The direction the entity is travelling in.
     * @param position  The last known position of the entity
     * @return The string packet.
     */
    public static String makeEntitiyPositionPacket(int entityID, Direction direction, Point2D.Double position) {
        return "POS0" + entityID + direction.toInt() + coordFormat.format(position.getX()) + coordFormat.format(position.getY());
    }
    
    /**
     * Makes the packet ro send to the client for the position of an item packet
     *
     * @param itemID   The unique identifier of the item
     * @param position The position of the item
     * @return The string packet
     */
    public static String makeItemPositionPacket(int itemID, Point2D.Double position) {
        return "POS1" + itemID + coordFormat.format(position.getX()) + coordFormat.format(position.getY());
    }
    
    /**
     * Makes the packet for when there is a collision between Pac man and a ghoul
     *
     * @param pacmanID Entity ID of pac man
     * @param ghoulID  Entity ID of the ghoul
     * @param position Position that the collision occurred
     * @return The packet representing the collision
     */
    public static String makePacGhoulCollisionPacket(int pacmanID, int ghoulID, Point2D.Double position) {
        return "COL0" + pacmanID + ghoulID + coordFormat.format(position.getX()) + coordFormat.format(position.getX());
    }
    
    /**
     * Makes the packet for a collision between an entity and a packet
     *
     * @param entityID Entity ID
     * @param itemID   ID of the item
     * @param powerup  Enum of the power-up which may have taken place.
     * @param score    Score of the entity after the power up
     * @param position The position the collision took place.
     * @return The String representing this packet.
     */
    public static String makeEntityItemCollisionPacket(int entityID, int itemID, int powerup, int score, Point2D.Double position) {
        return "COL1" + entityID + itemID + powerup + score + coordFormat.format(position.getX()) + coordFormat.format(position.getY());
    }
    
    
}
