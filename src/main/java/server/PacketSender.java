package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Queue;

public class PacketSender extends Thread {

  private InetAddress group;
  private MulticastSocket socket;
  private String networkInterface;
  private int port;
  private boolean running = true;
  private Queue<String> feedQueue;
  private ArrayList<InetAddress> ipStore = new ArrayList<>();

  /**
   * Constructs a Packet Sender object
   *
   * @param group The group we want to bind to
   * @param port The port we want to listen to
   * @param feedQueue The queue we want to send items from.
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
   * Constructs a Packet Sender object
   *
   * @param port the port we want to send to
   * @param feedQueue the queue which we are constantly reading from to send messages from.
   * @param ips The list of IP addresses which are listening on the port for a message.
   */
  public PacketSender(int port, Queue<String> feedQueue, ArrayList<InetAddress> ips)
      throws IOException {
    this.port = port;
    this.feedQueue = feedQueue;
    this.ipStore = ips;
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
        //System.out.println("About to send " + s);
        send(s);
        Thread.sleep(50);
      }

    } catch (IOException e) {
      running = false;
      System.out.println("ServerGameplayHandler closed");
      socket.close();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Takes a String and copies it into the outgoing packet byte buffer, as well as adding PREFIX and
   * SUFFIX strings to the buffer edges.
   */
  private byte[] prepareBuf(String s) { // will truncate

    byte[] buf;
    String toSend = NetworkUtility.PREFIX + s + NetworkUtility.SUFFIX;
    buf = toSend.getBytes(NetworkUtility.CHARSET);

    return buf;
  }

  /**
   * Sends packets to each IP in the list of IPs listening to agreed port.
   *
   * @param message the message which we want to send to the server
   * @throws IOException caused by the packets and and interfaces.
   */
  public void send(String message) throws IOException {
    byte[] buf = prepareBuf(message);

    DatagramSocket ds = new DatagramSocket();

    for (InetAddress ip : ipStore) {
      DatagramPacket packet = new DatagramPacket(buf, 0, buf.length, ip, this.port);
      ds.send(packet);
    }
  }

  /** Stops thread execution. */
  public void shutdown() {
    this.running = false;
    socket.close();
  }
}
