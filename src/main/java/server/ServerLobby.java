package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServerLobby {
    
    private int playerCount;
    private ArrayList<InetAddress> playerIPs;
    private boolean gameStarted;
    private ServerGameplayHandler s;
    
    public ServerLobby() {
        pinger.start();
        this.playerCount = 0;
        this.playerIPs = new ArrayList<>();
    }
    
    /**
     * Thread which sends messages to multicast group to make server IP known
     */
    Thread pinger = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                MulticastSocket socket = new MulticastSocket();
                InetAddress group = NetworkUtility.GROUP;
                
                
                byte[] buf;
                String message = "PING";
                buf = message.getBytes();
                DatagramPacket sending = new DatagramPacket(buf, 0, buf.length, group, NetworkUtility.CLIENT_M_PORT);
                
                while (!isInterrupted()) {
                    Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
                    a:
                    while (faces.hasMoreElements()) {
                        NetworkInterface iface = faces.nextElement();
                        if (iface.isLoopback() || !iface.isUp())
                            continue;
                        
                        Enumeration<InetAddress> addresses = iface.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            InetAddress addr = addresses.nextElement();
                            socket.setInterface(addr);
                            socket.send(sending);
                            
                        }
                    }
                    Thread.sleep(100);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    };
    
    /**
     * Accepts connections from clients.
     *
     * @throws IOException
     */
    public void acceptConnections() throws IOException {
        DatagramSocket ds = new DatagramSocket(NetworkUtility.SERVER_DGRAM_PORT);
        
        while (!gameStarted) {
            if (playerCount < 5) {
                byte[] buf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buf, 1024);
                ds.receive(dp);
                String r = new String(dp.getData(), 0, dp.getLength());
                if (r.equals(NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX)) {
                    System.out.println(r);
                    InetAddress ip = dp.getAddress();
                    System.out.println("Connecting to: " + ip);
                    playerIPs.add(ip);
                    dp = new DatagramPacket("SUCCESS".getBytes(), "SUCCESS".length(), ip, NetworkUtility.CLIENT_DGRAM_PORT);
                    ds.send(dp);
                    playerCount++;
                }
                
            } else {
                ds.close();
                return;
            }
            
        }
        ds.close();
    }
    
    /**
     * Starts the game for all clients
     *
     * @return
     */
    public ServerGameplayHandler gameStart() {
        pinger.interrupt();
        gameStarted = true;
        System.out.printf("Server starting game...");
        for (InetAddress ip : playerIPs) {
            try {
                DatagramSocket ds = new DatagramSocket();
                String str = "START GAME";
                DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, NetworkUtility.CLIENT_DGRAM_PORT);
                ds.send(dp);
                ds.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    
        }
        try {
            this.s = new ServerGameplayHandler(this.playerIPs, playerCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return s;
        
    }
    
    /**
     * Stops the game for clients.
     */
    public void gameStop() {
        //TODO Implement
    }
    
    
    public static void main(String[] args) throws IOException {
        ServerLobby s = new ServerLobby();
        s.acceptConnections();
    }
}
