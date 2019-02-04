package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class ClientLobbyJoiner {

  private int lobbyPort = networking.NetworkingData.LOBBY_PORT;
  private Queue<String> positionQueue;
  private Queue<String> collisionQueue;
  private BlockingQueue<Integer> keypressQueue;
  private BufferedReader in;
  public boolean connected = false;
  private ClientGameplayHandler handler;

  public ClientLobbyJoiner(Queue<String> positionQueue, Queue<String> collisionQueue,
      BlockingQueue<Integer> keypressQueue) {
    this.positionQueue = positionQueue;
    this.collisionQueue = collisionQueue;
    this.keypressQueue = keypressQueue;
  }

  /**
   * connects to the server and then starts the game when the
   * server tells it to.
   * The method blocks until the game starts so if it doesnt crash immediately
   * it is safe to assume it is connected and waiting.
   * @param ip this is the ip of the host to join
  */
  public void join(String ip) {
    try {
      //connects socket to lobby
      Socket socket = new Socket(ip, lobbyPort);
      connected = true;
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      in.readLine(); // if a line is received from the server then the server is sending game start
      in.close();
      socket.close();
      handler = new ClientGameplayHandler(positionQueue, collisionQueue, keypressQueue);
      handler.start();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.out.println("invalid ip");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * stops the game session handler.
   *  In other words this ceases communication with the running game
   */
  public void stop() {
    if (connected) {
      return;
    }
    handler.stop();
  }
}
