package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Queue;
import java.util.Random;
import utils.Input;

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

            byte[] buf;
            String message = "PING";

            buf = message.getBytes();
            DatagramPacket sending =
                new DatagramPacket(buf, 0, buf.length, group, NetworkUtility.CLIENT_M_PORT);

            while (!isInterrupted()) {
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
            System.out.println("Server pinger was closed successfully");
            return;
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      };
  private int playerCount;
  private ArrayList<InetAddress> playerIPs;
  private boolean gameStarted;
  private ServerGameplayHandler s;
  private String[] names = new String[5];
  private Queue<String> outputQueue;
  private int MIPID;
  /**
   * Accepts connections from clients
   */
  Thread acceptConnections =
      new Thread() {
        @Override
        public void run() {
          super.run();

          try {
            ServerSocket server = new ServerSocket(NetworkUtility.SERVER_DGRAM_PORT);
            while (!isInterrupted()) {
              if (playerCount < 5) {
                Socket soc = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                PrintWriter out = new PrintWriter(soc.getOutputStream());
                System.out.println("Waiting for new connection...");

                String r = in.readLine();
                String name = in.readLine();
                names[playerCount] = name;

                if (r.equals(NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX)) {
                  System.out.println(r);

                  InetAddress ip = soc.getInetAddress();
                  System.out.println("Connecting to: " + ip);
                  playerIPs.add(ip);

                  int playerID = playerCount;
                  out.println("" + playerID);
                  out.flush();
                  System.out.println("Sent client " + playerID + " their ID...");
                  out.println("" + MIPID);
                  out.flush();
                  out.println("SUCCESS");
                  out.flush();
                  System.out.println(
                      "Sent client " + playerID + " a successful connection message...");
                  playerCount++;
                  in.close();
                  out.close();
                  soc.close();
                }

              } else {
                return;
              }
            }
            server.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      };

  public ServerLobby() {
    pinger.start();
    this.playerCount = 0;
    this.playerIPs = new ArrayList<>();
    acceptConnections.start();
    this.MIPID = (new Random()).nextInt(2);
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
    System.out.printf("Server starting game...");
    System.out.println("THE IPS I WILL SEND TO: " + playerIPs.toString());
    for (InetAddress ip : playerIPs) {
      try {
        Socket soc = new Socket(ip, NetworkUtility.CLIENT_DGRAM_PORT);
        PrintWriter out = new PrintWriter(soc.getOutputStream());
        String str = "START GAME";

        out.println(str);
        out.flush();
        System.out.println("SERVER SENT START GAME");
        System.out.println("NAMES IN NAMES ARRAY: " + Arrays.toString(names));
        for (String name : names) {
          out.println(name);
          out.flush();
          System.out.println("SENT THE NAME: " + name);
        }

        out.flush();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      this.s = new ServerGameplayHandler(this.playerIPs, playerCount, inputQueue, outputQueue);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return s;
  }

  /** Stops the game for clients. */
  public void gameStop() {
    if (gameStarted) {
      outputQueue.add(NetworkUtility.STOP_CODE);
      // TODO close server side objects and stuff
    }
  }

  public int getPlayerCount() {
    return this.playerCount;
  }
}
