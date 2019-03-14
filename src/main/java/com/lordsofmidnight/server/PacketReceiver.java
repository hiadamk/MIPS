package com.lordsofmidnight.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;

public class PacketReceiver extends Thread {

  private boolean running = false;
  private Queue<String> feedQueue;
  private DatagramSocket ds;

  public PacketReceiver(int port, Queue<String> feedQueue) throws IOException {
    this.ds = new DatagramSocket(port);
    this.feedQueue = feedQueue;
  }

  /**
   * Continuously listens to the port agreed and adds the messages to the relevant queue in the
   * client/com.lordsofmidnight.server
   */
  @Override
  public void run() {
    super.run();
    running = true;
    while (running) {
      try {
        byte[] buf = new byte[NetworkUtility.STRING_LIMIT];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        ds.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        received = received.replaceAll("\u0000.*", "");

        if (received.startsWith(NetworkUtility.PREFIX)
            && received.endsWith(NetworkUtility.SUFFIX)) {
          received =
              received.substring(
                  NetworkUtility.PREFIX.length(),
                  received.length() - NetworkUtility.SUFFIX.length()); // rids PREFIX and SUFFIX
          feedQueue.add(received.trim());
        }
        Thread.sleep(50);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void shutdown() {
    this.running = false;
    if (ds != null && !ds.isClosed()) {
      ds.close();
    }
  }
}