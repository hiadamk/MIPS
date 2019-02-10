package server;

import main.Client;
import utils.Input;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Queue;

public class ClientLobbySession {
    
    private Queue<String> clientIn;
    private Queue<Input> keypressQueue;
    private InetAddress serverIP;
    private ClientGameplayHandler handler;
    private Client client;
    
    
    public ClientLobbySession(Queue<String> clientIn,
                              Queue<Input> keypressQueue, Client client) throws IOException {
        
        this.clientIn = clientIn;
        this.keypressQueue = keypressQueue;
        this.serverIP = NetworkUtility.getServerIP();
        this.client = client;
        joiner.start();
        
    }
    
    Thread joiner = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                DatagramSocket ds = new DatagramSocket(NetworkUtility.CLIENT_DGRAM_PORT);
                
                String str = NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX;
                DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), serverIP, NetworkUtility.SERVER_DGRAM_PORT);
                ds.send(dp);
                
                byte[] buf = new byte[1024];
                dp = new DatagramPacket(buf, 1024);
                ds.receive(dp);
                String r = new String(dp.getData(), 0, dp.getLength());
                r = r.replaceAll("\u0000.*", "");
                int id = Integer.parseInt(r);
                client.setId(id);
                
                
                buf = new byte[1024];
                dp = new DatagramPacket(buf, 1024);
                ds.receive(dp);
                r = new String(dp.getData(), 0, dp.getLength());
                r = r.replaceAll("\u0000.*", "");
                
                if (r.equals("SUCCESS")) {
                    System.out.println("Server connection success");
                }
                
                buf = new byte[1024];
                dp = new DatagramPacket(buf, 1024);
                ds.receive(dp);
                r = new String(dp.getData(), 0, dp.getLength());
                
                if (r.equals("STARTGAME")) {
                    handler = new ClientGameplayHandler(serverIP, keypressQueue, clientIn);
                }
                
                System.out.println(r);
                ds.close();
            } catch (IOException e) {
            
            }
            
        }
    };
    
    
    public static void main(String[] args) throws IOException {
//        ClientLobbySession c = new ClientLobbySession(new LinkedBlockingQueue<String>(),
//                new LinkedBlockingQueue<String>(), new LinkedBlockingQueue<Integer>());
//        c.join();
    }
}
