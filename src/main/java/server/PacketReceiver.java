package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Queue;

public class PacketReceiver extends Thread {

  private InetAddress group;
  private MulticastSocket socket;
  private boolean running = false;
  private Queue<String> feedQueue;
  private int port;
  private DatagramSocket ds;

  /**
   * Constructor only needs the multicasting group to communicate
   *
   * @param port The port we want to bind to
   * @param feedQueue The queue we want to send packets from
   * @param group The multi-casting group that we will connect to
   * @throws IOException caused by the use of sockets
   */
  public PacketReceiver(InetAddress group, int port, Queue<String> feedQueue) throws IOException {
    this.group = group;
    this.socket = new MulticastSocket(port);
    setUpNetworkInterfaces();
    this.feedQueue = feedQueue;
  }

  public PacketReceiver(int port, Queue<String> feedQueue) throws IOException {
    this.port = port;
    this.ds = new DatagramSocket(port);
    this.feedQueue = feedQueue;
  }

  private void setUpNetworkInterfaces() throws IOException {
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
  }

  /**
   * Continuously listens to the port agreed and adds the messages to the relevant queue in the
   * client/server
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
        System.out.println(received);
        received = received.replaceAll("\u0000.*", "");
        if (received.startsWith(NetworkUtility.PREFIX)
            && received.endsWith(NetworkUtility.SUFFIX)) {
          received =
              received.substring(
                  NetworkUtility.PREFIX.length(),
                  received.length() - NetworkUtility.SUFFIX.length()); // rids PREFIX and SUFFIX
          System.out.println(feedQueue);
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
    socket.close();
  }
}
