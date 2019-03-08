package server;

import utils.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

// TODO Use multi-casting to constantly ping the number of players in the game in a thread.
// TODO Use multi-casting to constantly ping the current players in the game in a thread
public class ServerLobby {

  /**
   * Thread which sends messages to multicast group to make server IP known
   */
  Thread pinger =
          new Thread() {
            @Override
            public void run() {
              super.run();
              try {
                MulticastSocket socket = new MulticastSocket();
                InetAddress group = NetworkUtility.GROUP;



                while (!isInterrupted()) {
                  byte[] buf;
                  String message = playerCount.get() + "|" + (hostPresent ? 1 : 0);

                  buf = message.getBytes();
                  DatagramPacket sending =
                          new DatagramPacket(buf, 0, buf.length, group, NetworkUtility.CLIENT_M_PORT);
                  Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
                  a:
                  while (faces.hasMoreElements()) {
                    NetworkInterface iface = faces.nextElement();
                    if (iface.isLoopback() || !iface.isUp()) {
                      continue;
                    }

                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {

                      InetAddress addr = addresses.nextElement();
                      socket.setInterface(addr);
                      socket.send(sending);
                    }
                  }
                  Thread.sleep(1000);
                }

              } catch (InterruptedException e) {
                return;
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          };
  private AtomicInteger playerCount;
  private ArrayList<InetAddress> playerIPs;
  private boolean gameStarted;
  private ServerGameplayHandler s;
  private String[] names = new String[5];
  private Queue<String> outputQueue;
  private int MIPID;
  private boolean[] usedIDs = {false, false, false, false, false};
  private ArrayList<Socket> activeClientSockets = new ArrayList<>();
  private ArrayList<PrintWriter> activeOutstreams = new ArrayList<>();
  private ArrayList<BufferedReader> activeInputStreams = new ArrayList<>();
  private ArrayList<lobbyLeaverListener> lobbyLeavers = new ArrayList<>();
  private ServerSocket server;
  private boolean hostPresent = true;

  /**
   * Accepts connections from clients
   */
  Thread acceptConnections =
          new Thread() {
            @Override
            public void run() {
              super.run();

              try {
                server = new ServerSocket(NetworkUtility.SERVER_DGRAM_PORT);
                while (!isInterrupted()) {
                  if (playerCount.get() < 5) {
                    Socket soc = server.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                    PrintWriter out = new PrintWriter(soc.getOutputStream());
                    activeInputStreams.add(in);
                    activeOutstreams.add(out);
                    activeClientSockets.add(soc);
                    System.out.println("Waiting for new connection...");

                    String r = in.readLine();
                    String name = in.readLine();
                    names[playerCount.get()] = name;

                    if (r.equals(NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX)) {
                      InetAddress ip = soc.getInetAddress();
                      System.out.println("Connecting to: " + ip);
                      playerIPs.add(ip);

                      int playerID = getNextID();
                      out.println("" + playerID);
                      out.flush();
                      System.out.println("Sent client " + playerID + " their ID...");
                      out.println("" + MIPID);
                      out.flush();
                      out.println("SUCCESS");
                      out.flush();
                      System.out.println(
                              "Sent client " + playerID + " a successful connection message...");
                      playerCount.set(playerCount.get() + 1);
                      lobbyLeaverListener l = new lobbyLeaverListener(soc, in, out, ip,playerID);
                      lobbyLeavers.add(l);
                      l.start();
                    }

                  } else {
                    return;
                  }
                }
                server.close();
              } catch (SocketException e) {
                System.out.println("Sockets were closed while waiting to accept");
              }catch (IOException e){
                e.printStackTrace();
              }
            }
          };

  public ServerLobby() {
    pinger.start();
    this.playerCount = new AtomicInteger(0);
    this.playerIPs = new ArrayList<>();
    acceptConnections.start();
    this.MIPID = (new Random()).nextInt(5);
  }

  public void shutDown(){
    acceptConnections.interrupt();
    if (server != null && !server.isClosed()) {
      try {
        server.close();
      } catch (IOException err)
      {
        err.printStackTrace(System.err);
      }
    }
    shutdownTCP();
    pinger.interrupt();
  }

  /**
   * Starts the game for all clients Needs to send player Names
   *
   * @return
   */
  public ServerGameplayHandler gameStart(Queue<Input> inputQueue, Queue<String> outputQueue) {
    this.outputQueue = outputQueue;
    pinger.interrupt();
    acceptConnections.interrupt();
    gameStarted = true;
    for (InetAddress ip : playerIPs) {
      try {
        Socket soc = new Socket(ip, NetworkUtility.CLIENT_DGRAM_PORT);
        PrintWriter out = new PrintWriter(soc.getOutputStream());
        String str = "START GAME";

        out.println(str);
        out.flush();
        for (String name : names) {
          out.println(name);
          out.flush();
        }
        out.flush();
        shutdownTCP();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      this.s = new ServerGameplayHandler(this.playerIPs, playerCount.get(), inputQueue, outputQueue);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return s;
  }

  private int getNextID(){
    for(int i = 0; i < usedIDs.length; i++){
      if(!usedIDs[i]){
        usedIDs[i] = true;
        return i;
      }
    }
    return 0;
  }

  /**
   * Stops the game for clients.
   */
  public void gameStop() {
    s.close();

  }

  public int getPlayerCount() {
    return this.playerCount.get();
  }

  private void shutdownTCP(){
    try{
      for(Socket s: activeClientSockets){
        s.close();
      }

      for(PrintWriter p:activeOutstreams){
        p.close();
      }

      for(BufferedReader b:activeInputStreams){
        b.close();
      }

      for(lobbyLeaverListener l:lobbyLeavers){
        l.interrupt();
      }
    }catch (IOException e){
      e.printStackTrace();
    }

  }

  private class lobbyLeaverListener extends Thread{

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private int id;
    private InetAddress ip;
    public lobbyLeaverListener(Socket client, BufferedReader in, PrintWriter out, InetAddress ip, int id){
      this.client = client;
      this.in = in;
      this.out = out;
      this.ip = ip;
      this.id = id;
    }

    @Override
    public void run(){
      while(!isInterrupted()){
        try {
          String message = in.readLine();
          if(message.equals(NetworkUtility.DISCONNECT_NON_HOST)){
            ServerLobby.this.usedIDs[id] = false;
            ServerLobby.this.playerIPs.remove(ip);
            ServerLobby.this.names[id] = null;
            ServerLobby.this.playerCount.set(ServerLobby.this.playerCount.get() -1);
            in.close();
            out.close();
            client.close();
            System.out.println("Removed Player: " + id + " from game");
          }else if(message.equals(NetworkUtility.DISCONNECT_HOST)){
            hostPresent = false;
            ServerLobby.this.usedIDs[id] = false;
            ServerLobby.this.playerIPs.remove(ip);
            ServerLobby.this.names[id] = null;
            ServerLobby.this.playerCount.set(ServerLobby.this.playerCount.get() -1);
            in.close();
            out.close();
            client.close();
            System.out.println("Removed Host Player: " + id + " from game. And shut down the game");
            ServerLobby.this.shutDown();
          }
        } catch (IOException e) {

        }catch(NullPointerException e1){
          ServerLobby.this.usedIDs[id] = false;
          ServerLobby.this.playerIPs.remove(ip);
          ServerLobby.this.names[id] = null;
          ServerLobby.this.playerCount.set(ServerLobby.this.playerCount.get() -1);
          try {
            in.close();
            out.close();
            client.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
          System.out.println("Removed Player: " + id + " from game");
        }
      }
    }
  }
}
