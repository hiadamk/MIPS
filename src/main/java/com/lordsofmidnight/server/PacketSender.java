package com.lordsofmidnight.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Queue;

public class PacketSender extends Thread {

  private int port;
  private boolean running = true;
  private Queue<String> feedQueue;
  private ArrayList<InetAddress> ipStore = new ArrayList<>();
  private DatagramSocket ds;

  /**
   * Constructs a Packet Sender object
   *
   * @param port the port we want to send to
   * @param feedQueue the queue which we are constantly reading from to send messages from.
   * @param ips The list of IP addresses which are listening on the port for a message.
   */
  public PacketSender(int port, Queue<String> feedQueue, ArrayList<InetAddress> ips) {
    this.port = port;
    this.feedQueue = feedQueue;
    this.ipStore = ips;
    try {
      ds = new DatagramSocket();
    } catch (IOException e) {
      e.printStackTrace();
    }

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
   * @param message the message which we want to send to the com.lordsofmidnight.server
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

  /**
   * Stops thread execution.
   */
  public void shutdown() {
    this.running = false;
    if (ds != null && !ds.isClosed()) {
      ds.close();
    }
  }
}
