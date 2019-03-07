package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;
import java.util.Queue;

import javafx.application.Platform;
import main.Client;
import utils.Input;

public class ClientLobbySession {

  private Queue<String> clientIn;
  private Queue<Input> keypressQueue;
  private InetAddress serverIP;
  private ClientGameplayHandler handler;
  private Client client;
  private String clientName;
  private String[] playerNames = new String[5];
  private volatile boolean gameStarted = false;

  private Socket soc;
  private PrintWriter out;
  private BufferedReader in;

  Thread joiner =
          new Thread() {
            @Override
            public void run() {
              super.run();
              try {


                System.out.println("Getting the server address");
                MulticastSocket socket = new MulticastSocket(NetworkUtility.CLIENT_M_PORT);
                InetAddress group = NetworkUtility.GROUP;
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

                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.printf("Server Address: " + packet.getAddress());
                serverIP = packet.getAddress();

                soc = new Socket(serverIP, NetworkUtility.SERVER_DGRAM_PORT);
                out = new PrintWriter(soc.getOutputStream());
                in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                String str = NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX;
                out.println(str);
                System.out.println("SENT CONNECT TO SERVER");
                out.println(clientName);
                System.out.println("SENT CLIENT NAME: " + clientName);
                out.flush();

                String r = in.readLine();
                int id = Integer.parseInt(r);
                client.setId(id);

                r = in.readLine();
                int MIPID = Integer.parseInt(r);
                client.setMIP(MIPID);
                r = in.readLine();
                if (r.equals("SUCCESS")) {
                  System.out.println("Server connection success");
                }
//                out.close();
//                in.close();
//                soc.close();

                Socket ss = new ServerSocket(NetworkUtility.CLIENT_DGRAM_PORT).accept();
                in = new BufferedReader(new InputStreamReader(ss.getInputStream()));

                // get other player names
                r = in.readLine();
                if (r.equals("START GAME")) {
                  for (int i = 0; i < 5; i++) {
                    playerNames[i] = in.readLine();
                    System.out.println("NAME: " + playerNames[i]);
                  }
                  gameStarted = true;
                  handler = new ClientGameplayHandler(serverIP, keypressQueue, clientIn);
                  client.setPlayerNames(playerNames);
                  if(!client.isHost){
                    Platform.runLater(()-> client.startMultiplayerGame());
                    shutdownTCP();
                  }
                }

                in.close();
                ss.close();
              } catch (IOException e) {

              }
            }
          };

  public ClientLobbySession(
          Queue<String> clientIn, Queue<Input> keypressQueue, Client client, String clientName)
          throws IOException {

    this.clientIn = clientIn;
    this.keypressQueue = keypressQueue;
//    this.serverIP = NetworkUtility.getServerIP();
    this.client = client;
    this.clientName = clientName;
    joiner.start();
  }

  public void leaveLobby(){
      out.write(NetworkUtility.DISCONNECT);
      shutdownTCP();
  }

  private void shutdownTCP(){
    try{
      out.close();
      in.close();
      soc.close();
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  public boolean isGameStarted() {
    return gameStarted;
  }
}
