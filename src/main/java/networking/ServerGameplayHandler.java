package networking;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerGameplayHandler {

  private static final String startCode = networking.NetworkingData.START_CODE;
  private static String collisionCode = networking.NetworkingData.COLLISION_CODE;
  private static String positionCode = networking.NetworkingData.POSITION_CODE;
  private static String stopCode = networking.NetworkingData.STOP_CODE;
  private static int outPort = networking.NetworkingData.PORT;
  private int inPort;
  private UDPListenerThread serverListener;
  private UDPSenderThread serverSender;
  DatagramSocket socket;
  InetAddress address;
  Queue<String> positionQueue;
  Queue<String> collisionQueue;
  BlockingQueue<Integer> keypressQueue;
  private boolean started = false;

  /**
   * Handles the in-game communication session to the client sending data in position queue and
   * collision queue and passing received data to the key press queue. the current model has a
   * listener per client, listening to a distinct port for each client.
   *
   * @param address the address of the client to talk to
   * @param port the port to receive client on, this must be unique between connected clients
   * @param positionQueue
   * @param collisionQueue
   * @param keypressQueue
   */
  public ServerGameplayHandler(String address, int port, Queue<String> positionQueue,
      Queue<String> collisionQueue, BlockingQueue<Integer> keypressQueue)
      throws UnknownHostException {
    this.address = InetAddress.getByName(address);
    this.positionQueue = positionQueue;
    this.collisionQueue = collisionQueue;
    this.keypressQueue = keypressQueue;
    this.inPort = port;
  }

  /**
   * Initiates the session with the client. Client must also be waiting to started
   */
  public void start() {
    //don't do anything if threads are already running
    if (started) {
      return;
    }
    //connect socket to client
    while (connect() != 0) {
    } //may update this to a timeout-style attempt at connecting.
    started = true;
    Queue<String> outgoingQueue = new ConcurrentLinkedQueue<String>();
    Queue<String> incomingQueue = new ConcurrentLinkedQueue<String>();
    serverListener = new UDPListenerThread(socket, incomingQueue);
    serverSender = new UDPSenderThread(socket, outgoingQueue);

    //puts inputs from queues into the outgoing queue - not sure this one closes
    Thread incomingPackager = new Thread() {
      public void run() {
        Integer key;
        while (true) {
          if (incomingQueue.isEmpty()) {
            continue;
          }
          key = Integer.valueOf(incomingQueue.poll());
          keypressQueue.add(key);

        }  //private boolean running = true;

      }
    };

    //reads inputs from the incoming queue into the relevant queue - position queue or collision queue
    //could be parallelised in adding positions and collisions to the queue - hogging was unlikely as collisions
    //occur a lot less than position updates
    Thread outgoingPackager = new Thread() {
      public void run() {
        boolean running = true;
        while (running) {
          try {
            if (!collisionQueue.isEmpty()) {
              String data = collisionQueue.poll();
              data = collisionCode + data;
              outgoingQueue.add(data);
            }
            if (positionQueue.isEmpty()) {
              continue;
            } else {
              String data = positionQueue.poll();
              data = positionCode + data;
              outgoingQueue.add(data);
            }

          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

          }

        }
      }
    };

    incomingPackager.run();
    outgoingPackager.run();
    serverListener.run();
    serverSender.run();

  }

  /**
   * Disconnects from the client, shutting down threads and ceasing communication
   */
  public void stop() {
    if (!started) {
      return;
    }
    collisionQueue.add(
        stopCode); // sends stopcode on outgoingqueue to client. done twice to ensure transmission because UDP
    collisionQueue.add(stopCode);
    serverListener.shutdown();
    serverSender.shutdown();
    socket.disconnect();
    socket.close();
    started = false;
  }

  /**
   * connects to the system.
   *
   * @return 0 for successful connection, -1 for failed connection
   */
  private int connect() {
    try {
      socket = new DatagramSocket(inPort);
      socket.connect(address, outPort);
      collisionQueue.add(startCode);
      return (socket.isConnected() && !socket.isClosed()) ? 0 : -1;

    } catch (SocketException e) {
      return -1;
    }
  }

  /**
   * returns whether the session is running.
   *
   * @return started
   */
  public boolean isStarted() {
    return started;
  }
}
