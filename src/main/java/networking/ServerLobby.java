package networking;

import networking.NetworkingData;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedQueue; these may be better than just allowing any queue interface
import java.util.concurrent.BlockingQueue;

public class ServerLobby {

  private final int lobbyPort = NetworkingData.LOBBY_PORT;
  private final String startCode = NetworkingData.START_CODE;
  private Socket socket;
  private int playerCount;
  private int currentPlayers = 0;
  private PrintWriter out;
  private int[] ports;
  private String[] addresses; //array of addresses to be used in creating gameplay sessions
  private Socket[] sockets; //array of sockets to maintain lobby's connection
  private boolean lobbyOpen;
  private Queue<String>[] positionQueues;
  private Queue<String>[] collisionQueues;
  private BlockingQueue<Integer>[] keypressQueues;
  private ServerGameplayHandler[] handlers;
  private boolean gameStarted = false;

  /**
   * Creates a server lobby object that will handle players joining the lobby, and
   * handles starting of the game. The queues will be related to eachother by index.
   * so player 1 will use positionQueues[0], collisionQueues[0] etc
   * @param ports this must be a list of distinct ports. each player communicates to a listening thread
   * on a different port.
   * @param playerCount This is the number of players excluding the host. It is the number of players to join.
   * @param positionQueues This is a list of positionQueues that will be used to communicate position data.
   * @param collisionQueues This is a list of collisionQueues that will be used to communicate collision data.
   * @param keypressQueues This is a list of keypressQueues that will be filled with keypresses sent to server.
   */
  public ServerLobby(int[] ports, int playerCount, Queue<String>[] positionQueues,
      Queue<String>[] collisionQueues, BlockingQueue<Integer>[] keypressQueues) {

    this.playerCount = playerCount;
    this.sockets = new Socket[playerCount];
    this.handlers = new ServerGameplayHandler[playerCount];
    this.addresses = new String[playerCount];
    this.ports = ports;

    this.positionQueues = positionQueues;
    this.collisionQueues = collisionQueues;

    this.keypressQueues = keypressQueues;
    assert (ports.length == playerCount);
    assert (positionQueues.length == playerCount);
    assert (collisionQueues.length == playerCount);
    assert (keypressQueues.length == playerCount);
    assert (ports.length == playerCount);
  }

  /**
   * Opens lobby for joining. The playercount no. of players must connect for this method to exit.
   */
  public void openLobby() {

    lobbyOpen = true;
    for (int i = 0; i < playerCount; i++) {
      try {
        ServerSocket serverSocket = new ServerSocket(lobbyPort); // creates listening socket
        socket = serverSocket.accept(); //returns socket for communication
        addresses[i] = socket.getInetAddress()
            .getHostAddress(); //add address of connected player to adresses
        sockets[i] = socket;
        serverSocket.close();

        //socket.getOutputStream() // send confirmation they've joined
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to add player" + i);
      }
      currentPlayers++;
    }
  }

  /**
   * Tells the players in the lobby to start their games. After receiving this message both
   * sides will create and run session handlers that transfer queue contents between players and
   * the server.
   *
   * @return status of whether the game started successfully
   */
  public int gameStart() {
    if ((currentPlayers < playerCount) || !lobbyOpen || gameStarted) {
      return -1;
    }
    for (int i = 0; i < playerCount; i++) {
      Socket socket = sockets[i];
      try {
        out = new PrintWriter(socket.getOutputStream());
        //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(startCode);
        out.flush();
        out.close();
        socket.close();
        handlers[i] = new ServerGameplayHandler(addresses[i], ports[i], positionQueues[i],
            collisionQueues[i], keypressQueues[i]);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to add player" + i);
      }
      startHandlers();
    }
    gameStarted = true;
    return 0;
  }

  private void startHandlers() {
    for (int i = 0; i < playerCount; i++) {
      handlers[i].start(); //starts session manager
    }
  }

  /**
   * Broadcasts to stop the game to players.
   * If the game is not running the method returns
   */
  public void gameStop() {
    if (!gameStarted) {
      return;
    }
    for (int i = 0; i < playerCount; i++) {
      handlers[i].stop();
    }
    gameStarted = false;
  }

  public String[] getAdresses() {
    return addresses;
  }

}
