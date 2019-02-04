package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientGameplayHandler {

  private final String collisionCode = networking.NetworkingData.COLLISION_CODE;
  private final String positionCode = networking.NetworkingData.POSITION_CODE;
  private final String stopCode = networking.NetworkingData.STOP_CODE;
  private final int inPort = networking.NetworkingData.PORT;
  private UDPListenerThread clientListener;
  private UDPSenderThread clientSender;
  private DatagramSocket socket;
  private BlockingQueue<Integer> keypressQueue;
  private Queue<String> positionQueue;
  private Queue<String> collisionQueue;
  private boolean started = false;

  /**
   * Handles the in-game communication session to the server receiving data to a position queue and
   * a collision queue and passing sending data from the key press queue.
   */
  public ClientGameplayHandler(Queue<String> positionQueue, Queue<String> collisionQueue,
      BlockingQueue<Integer> keypressQueue) {
    this.keypressQueue = keypressQueue;
    this.positionQueue = positionQueue;
    this.collisionQueue = collisionQueue;
  }


  /**
   * Listens for and connects to the contacting host, and starts the session.
   */
  public void start() {

    if (started) {
      return;
    }
    //connect to server attempting to reach us
    if (connect() != 0) {
      return;
    } //Timeout attempt at connecting.
    started = true;

    Queue<String> outgoingQueue = new ConcurrentLinkedQueue<String>();
    Queue<String> incomingQueue = new ConcurrentLinkedQueue<String>();
    UDPListenerThread clientListener = new UDPListenerThread(socket, incomingQueue);
    UDPSenderThread clientSender = new UDPSenderThread(socket, outgoingQueue);

    //puts inputs from queues into the outgoing queue - not sure this one closes
    Thread outgoingPackager = new Thread() {

      public void run() {

        Integer key;
        while (started) {
          try {
            key = keypressQueue.take();
            outgoingQueue.add(key.toString());
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

        }
      }
    };

    //reads inputs from the incoming queue into the relevant queue - position queue or collision queue
    Thread incomingPackager = new Thread() {
      public void run() {
        while (started) {
          try {
            if (incomingQueue.isEmpty()) {
              continue;
            }
            String data = incomingQueue.poll();

            if (data.startsWith(positionCode)) {
              positionQueue.add(data.substring(positionCode.length()));
            } else if (data.startsWith(collisionCode)) {
              collisionQueue.add(data.substring(collisionCode.length()));
            } else if (data.startsWith(stopCode)) {
              started = false;
            } else {
              throw new Exception();
            }
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Argument in incoming queue had invalid code");
          }

        }
      }
    };

    incomingPackager.run();
    outgoingPackager.run();
    clientListener.run();
    clientSender.run();
  }

  /**
   * connects to a server trying to reach us on the standard port
   *
   * @return 0 if successful connection established, -1 if not.
   */
  private int connect() {
    byte[] buf = new byte[NetworkingData.STRING_LIMIT];
    DatagramPacket p = new DatagramPacket(buf, buf.length);
    try {
      socket = new DatagramSocket(inPort);
      socket.setSoTimeout(10000); //server must connect within 10 seconds
      socket.receive(p); //should be message similar to PREFIX:STARTCODE     :SUFFIX
      socket.connect(p.getAddress(), p.getPort());
      return 0;
    } catch (IOException e) {
      //also catches timeout exception
      return -1;
    }
  }

  public void stop() {
    if (!started) {
      return;
    }
    clientListener.shutdown();
    clientSender.shutdown();
    socket.close();
    started = false;
  }

}
