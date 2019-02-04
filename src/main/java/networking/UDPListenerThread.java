package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Queue;

class UDPListenerThread extends Thread {

  private final int STRING_LIMIT = NetworkingData.STRING_LIMIT;
  private final String prefix = NetworkingData.PREFIX;
  private final String suffix = NetworkingData.SUFFIX;
  private final Charset charset = NetworkingData.CHARSET;
  private volatile boolean running = false;
  private Queue<String> feedQueue;
  private byte[] buf;
  private DatagramSocket socket;
  private InetAddress ipaddr;

  /**
   * creates a listener thread that continuously recieves udp packets and places contents into a
   * queue as strings
   *
   * @param socket - connected socket used to recieve packets
   * @param feedQueue - queue to place recieved strings into
   */
  public UDPListenerThread(DatagramSocket socket, Queue<String> feedQueue) {
    this.feedQueue = feedQueue;
    this.socket = socket;
    this.ipaddr = socket.getInetAddress();
    if (ipaddr == null) {
      throw new IllegalArgumentException("Null ip, socket is not connected to anything!");
    }
    buf = new byte[STRING_LIMIT];
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    running = true;
    try {
      while (running) {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String s = new String(packet.getData(), 0, packet.getLength(), charset);
        if (s.startsWith(prefix) && s.endsWith(suffix)) {
          s = s.substring(prefix.length(),
              s.length() - suffix.length() - 1); //rids prefix and suffix
          feedQueue.add(s.trim());
        } else {
          continue;
        }
      }

      //closing code - sender should handle closing to server
    } catch (IOException e) {
      System.out.println("Server closed");
      socket.close();
    }
  }

  public void shutdown() {
    this.running = false;
  }

}