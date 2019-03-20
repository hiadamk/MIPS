package com.lordsofmidnight.server;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.PowerUpBox;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.utils.enums.Direction;
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
import java.util.LinkedList;

/**
 * Class which will holds shared utility data for classes.
 */
public class NetworkUtility {

  /**
   * Networking constants
   */
  public static final int CLIENT_M_PORT = 4445;
  static final int SERVER_DGRAM_PORT = 3000;
  static final int CLIENT_DGRAM_PORT = 3001;

  static final String PREFIX = "SMSG";
  static final String SUFFIX = "EMSG";
  static final String POSITION_CODE = "POS";
  static final String SCORE_CODE = "SCOR";
  static final String COLLISIONS_CODE = "COL";
  static final String POWERUP_CODE = "POW";
  public static final String STOP_CODE = "EXIT";
  static final String GAME_START = "START GAME";
  static final String DISCONNECT_HOST = "DISCONNECT_HOST";
  static final String DISCONNECT_NON_HOST = "DISCONNECT_NON_HOST";
  static final int STRING_LIMIT = 128;
  static final Charset CHARSET = StandardCharsets.US_ASCII;
  public static final int LOBBY_TIMEOUT = 3500;

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
   * Checks for a message in the group and gets the IP of the com.lordsofmidnight.server from that.
   *
   * @return the IP of the com.lordsofmidnight.server
   * @throws IOException Thrown by use of multicast sockets.
   */
  public static InetAddress getServerIP() throws IOException {
    System.out.println("Getting the com.lordsofmidnight.server address");
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
   * @return The string packet.
   */
  public static String makeEntitiyMovementPacket(Input input, Point position, int mipID) {
    return POSITION_CODE+1
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
    String s = POSITION_CODE+3; // id:X:Y|id:X:Y|id:X:Y...
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
   * Makes the packet of all the clients's inventory contents
   *
   * @param agents the list of players
   * * @return The string packet.
   */
  public static String makeInventoryPacket(Entity[] agents) {
    String s = POWERUP_CODE + 0; // |0:2|.
    for (int i = 0; i < agents.length; i++) {
      LinkedList<PowerUp> items = agents[i].getItems();

      s += "|"
          + i;
      for (PowerUp item : items) {
        s += ":" + item.toInt();
      }
    }
    return s;
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
    return POWERUP_CODE + 1
        + id
        + "|"
        + powerup.toInt()
        + "|"
        + coordFormat.format(position.getX())
        + "|"
        + coordFormat.format(position.getY());
  }


  /**
   * Makes the packet to send to the client that a powerup box exists somewhere
   *
   * @param position the powerup box location
   * @return The string packet.
   */
  public static String makePowerUpBoxPacket(Point position) {
    return POWERUP_CODE + 2
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
    String s = SCORE_CODE;
    for (Entity agent : agents) {
      s += "|" + agent.getScore();
    }
    return s;
  }

}
