package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Queue;

/**
 * Currently there is no way to safely close the thread, I removed the stopmethod as you can't override the super one
 */
class UDPSenderThread extends Thread {
    private final int STRING_LIMIT = NetworkingData.STRING_LIMIT;
    private final String prefix = NetworkingData.prefix;
    private final String suffix = NetworkingData.suffix;
    private final Charset charset = NetworkingData.charset;
    private boolean running = false;
    private Queue<String> feedQueue;
    private byte[] buf;
    private DatagramSocket socket;
    private InetAddress ipaddr;
    private int port;
    
    /**
     * @param socket
     * @param feedQueue
     */
    public UDPSenderThread(DatagramSocket socket, Queue<String> feedQueue) {
        this.feedQueue = feedQueue;
        this.socket = socket;
        this.ipaddr = socket.getInetAddress();
        this.port = socket.getPort();
        if (ipaddr == null || port == -1) {
            throw new IllegalArgumentException("Null ip, socket is not connected to anything!");
        }
        buf = String.format("%" + STRING_LIMIT + "s", "").getBytes(charset); //creates bye array of whitespace
    }
    
    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                if (feedQueue.isEmpty()) {
                    continue;
                }
                String s = feedQueue.poll();
                prepareBuf(s);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, ipaddr, port);
                socket.send(packet);
            }
            
        } catch (IOException e) {
            running = false;
            System.out.println("Server closed");
            socket.close();
        }
    }
    
    /**
     * Takes a String and copies it into the outgoing packet byte buffer,
     * as well as adding prefix and suffix strings to the buffer edges.
     *
     * @param s
     */
    private void prepareBuf(String s) { // will truncate
        System.arraycopy(prefix.getBytes(charset), 0, buf, 0, prefix.length());    // writes prefix into buffer
        System.arraycopy(s.getBytes(charset), 0, buf, prefix.length(), buf.length); // writes string into buffer
        System.arraycopy(suffix.getBytes(charset), 0, buf, (buf.length - suffix.length() - 1), suffix.length()); //writes suffix into buffer
    }
    
    /**
     * Stops thread execution.
     */
    
    public void shutdown() {
        this.running = false;
    }
    
    
}