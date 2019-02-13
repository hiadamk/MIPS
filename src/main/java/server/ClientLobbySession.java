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
                Socket soc = new Socket(ServerIP, NetworkUtility.SERVER_DGRAM_PORT);
                PrintWriter out = new PrintWriter(soc.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream());

                String str = NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX);
                out.println(str);
                out.flush();


                String r = in.readln();
                int id = Integer.parseInt(r);
                client.setId(id);
                

                r = in.readln();
                if (r.equals("SUCCESS")) {
                    System.out.println("Server connection success");
                }
                out.close();
                in.close();
                soc.close();


                soc = new ServerSocket(CLIENT_DGRAM_PORT).accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                r = in.readln();
                                if (r.equals("STARTGAME")) {
                    handler = new ClientGameplayHandler(serverIP, keypressQueue, clientIn);
                }
                
                System.out.println(r);
                in.close();
                soc.close();
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
