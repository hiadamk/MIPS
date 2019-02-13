package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;


//TODO Use multi-casting to constantly ping the number of players in the game in a thread.
//TODO Use multi-casting to constantly ping the current players in the game in a thread
public class ServerLobby {
    
    private int playerCount;
    private ArrayList<InetAddress> playerIPs;
    private boolean gameStarted;
    private ServerGameplayHandler s;
    
    public ServerLobby() {
        pinger.start();
        this.playerCount = 0;
        this.playerIPs = new ArrayList<>();
        acceptConnections.start();
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
                    Thread.sleep(1000);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    };
    
    
    /**
     * Accepts connections from clients
     */
    Thread acceptConnections = new Thread() {
        @Override
        public void run() {
            super.run();
            try {

                
                while (!isInterrupted()) {
                    if (playerCount < 5) {
                        Socket soc = new ServerSocket(NetworkUtility.SERVER_DGRAM_PORT).accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                        PrintWriter out = new PrintWriter(soc.getOutputStream());
                        System.out.println("Waiting for new connection...");

                        String r = in.readline();
                        if (r.equals(NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX)) {
                            System.out.println(r);

                            InetAddress ip = soc.getInetAddress();
                            System.out.println("Connecting to: " + ip);
                            playerIPs.add(ip);

                            int playerID = playerCount;
                            out.println(""+playerID);
                            out.flush();
                            System.out.println("Sent client " + playerID + " their ID...");

                            out.println("SUCCESS");
                            out.flush();
                            System.out.println("Sent client " + playerID + " a successful connection message...");
                            playerCount++;
                            in.close();
                            out.close();
                            soc.close();
                        }
                        
                    } else {
                        ds.close();
                        return;
                    }
                    
                }
            } catch (IOException e) {
            
            }
            
        }
    };
    
    
    /**
     * Starts the game for all clients
     *
     * @return
     */
    public ServerGameplayHandler gameStart() {
        pinger.interrupt();
        acceptConnections.interrupt();
        gameStarted = true;
        System.out.printf("Server starting game...");
        for (InetAddress ip : playerIPs) {
            try {
                Socket soc = new Socket(ip, NetworkUtility.CLIENT_DGRAM_PORT);
                PrintWriter out = new PrintWriter(soc.getOutputStream());
                String str = "START GAME";

                out.println(str);
                out.flush();
                out.close();
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
//        s.acceptConnections();
    }
}
