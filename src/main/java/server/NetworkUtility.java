package server;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Enumeration;
import objects.Entity;
import utils.Input;
import utils.Point;
import utils.enums.Direction;
import utils.enums.PowerUp;

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
  public static final String SCORE_CODE = "SCOR";
  public static final String COLLISIONS_CODE = "COL";
  public static final String POWERUP_CODE = "POW";
  public static final String STOP_CODE = "EXIT";
  public static final int STRING_LIMIT = 128;
  public static final Charset CHARSET = StandardCharsets.US_ASCII;

  public static InetAddress GROUP;
  private static DecimalFormat coordFormat = new DecimalFormat("000.000");

  static {
    try {
      GROUP = InetAddress.getByName("239.255.255.255"); // able to include localhost
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
      if (iface.isLoopback() || !iface.isUp()) {
        continue;
      }

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
   * @param entityID The unique ID of the entity
   * @param direction The direction the entity is travelling in.
   * @param position The last known position of the entity
   * @return The string packet.
   */
  public static String makeEntitiyPositionPacket(
      int entityID, Direction direction, Point2D.Double position) {
    return "POS0"
        + entityID
        + direction.toInt()
        + coordFormat.format(position.getX())
        + coordFormat.format(position.getY());
  }

  /**
   * Makes the packet to send to the client for the position and movements of entities
   *
   * @param input The most recent valid input of the agent (includes ID)
   * @param position The last known position of the entity
 * @param mipID 
   * @return The string packet.
   */
  public static String makeEntitiyMovementPacket(Input input, Point position, int mipID) {
    return "POS1"
        + input.toString()
        + "|"
        + coordFormat.format(position.getX())
        + "|"
        + coordFormat.format(position.getY())
        + "|"
        + mipID;
  }

  /**
   * Makes the packet to send to the client for the position of an item packet
   *
   * @param itemID The unique identifier of the item
   * @param position The position of the item
   * @return The string packet
   */
  public static String makeItemPositionPacket(int itemID, Point2D.Double position) {
    return "POS2"
        + itemID
        + "|"
        + coordFormat.format(position.getX())
        + "|"
        + coordFormat.format(position.getY());
  }

  /**
   * Makes the packet to send to the client for the positions of entities.
   *
   * @param agents the list of agents
   * @return The string packet.
   */
  public static String makeEntitiesPositionPacket(Entity[] agents) {
    String s = "POS3"; // id:X:Y|id:X:Y|id:X:Y...
    for (int i = 0; i < agents.length; i++) {
      s +=
          i
              + ":"
              + agents[i].getDirection().toInt()
              + ":"
              + coordFormat.format(agents[i].getLocation().getX())
              + ":"
              + coordFormat.format(agents[i].getLocation().getY())
              + "|";
    }
    return s;
  }

  /**
   * Makes the packet for when there is a collision between Pac man and a ghoul
   *
   * @param pacmanID Entity ID of pac man
   * @param ghoulID Entity ID of the ghoul
   * @param position Position that the collision occurred
   * @return The packet representing the collision
   */
  public static String makePacGhoulCollisionPacket(
      int pacmanID, int ghoulID, Point2D.Double position) {
    return "COL0"
        + pacmanID
        + ghoulID
        + coordFormat.format(position.getX())
        + coordFormat.format(position.getX());
  }

  /**
   * Makes the packet for a collision between an entity and a packet
   *
   * @param entityID Entity ID
   * @param itemID ID of the item
   * @param powerup Enum of the power-up which may have taken place.
   * @param score Score of the entity after the power up
   * @param position The position the collision took place.
   * @return The String representing this packet.
   */
  public static String makeEntityItemCollisionPacket(
      int entityID, int itemID, int powerup, int score, Point2D.Double position) {
    return "COL1"
        + entityID
        + itemID
        + powerup
        + score
        + coordFormat.format(position.getX())
        + coordFormat.format(position.getY());
  }
  
  /**
   * Makes the packet to send to the client that a powerup has been used
   *
   * @param id of the player that used the powerup
   * @param powerup which powerup enum was used
   * @param position The last known position of the entity
   * @return The string packet.
   */
  public static String makePowerUpPacket(int id, PowerUp powerup, Point position) {
    return "POW1"
        + id
        + "|"
        + powerup.toInt()
        + "|"
        + coordFormat.format(position.getX())
        + "|"
        + coordFormat.format(position.getY());
  }

  /**
   * Makes the packet to send to the clients of the current scoreboard
   *
   * @param agents the array of game entities
   * @return The string packet.
   */
  public static String makeScorePacket(Entity[] agents) {
    String s = "SCOR";
        for (Entity agent: agents){
          s+= "|" + agent.getScore();
        }
        return s;
  }
  
}
