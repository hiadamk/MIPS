package server;

import main.Client;
import utils.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;

public class ClientLobbySession {
    
    private Queue<String> clientIn;
    private Queue<Input> keypressQueue;
    private InetAddress serverIP;
    private ClientGameplayHandler handler;
    private Client client;
    private String clientName;
    private String[] playerNames = new String[5];
    private volatile boolean gameStarted = false;

    public ClientLobbySession(Queue<String> clientIn,
                              Queue<Input> keypressQueue, Client client, String clientName) throws IOException {

        this.clientIn = clientIn;
        this.keypressQueue = keypressQueue;
        this.serverIP = NetworkUtility.getServerIP();
        this.client = client;
        this.clientName = clientName;
        joiner.start();

    }

    Thread joiner = new Thread() {
        @Override
        public void run() {
            super.run();
            try {
                Socket soc = new Socket(serverIP, NetworkUtility.SERVER_DGRAM_PORT);
                PrintWriter out = new PrintWriter(soc.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                String str = NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX;
                out.println(str);
                out.println(clientName);
                out.flush();


                String r = in.readLine();
                int id = Integer.parseInt(r);
                client.setId(id);


                r = in.readLine();
                if (r.equals("SUCCESS")) {
                    System.out.println("Server connection success");
                }
                out.close();
                in.close();
                soc.close();


                soc = new ServerSocket(NetworkUtility.CLIENT_DGRAM_PORT).accept();
                in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                //get other player names
                r = in.readLine();
                                if (r.equals("STARTGAME")) {
                                    for (int i = 0; i<5; i++){
                                        playerNames[i] = in.readLine();
                                    }
                                    gameStarted = true;
                    handler = new ClientGameplayHandler(serverIP, keypressQueue, clientIn);
                                    client.setPlayerNames(playerNames);
                }

                System.out.println(r);
                in.close();
                soc.close();
            } catch (IOException e) {

            }

        }
    };

public boolean isGameStarted(){
    return gameStarted;
}

    public static void main(String[] args) throws IOException {
//        ClientLobbySession c = new ClientLobbySession(new LinkedBlockingQueue<String>(),
//                new LinkedBlockingQueue<String>(), new LinkedBlockingQueue<Integer>());
//        c.join();
    }
}
