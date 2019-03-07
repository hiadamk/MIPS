package server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import utils.Input;

/**
 * ClientGameplayHandler class which creates the appropriate senders and starts them.
 */
public class ClientGameplayHandler {

  public Queue<String> outgoingQueue;
  private Queue<String> incomingQueue;

  private BlockingQueue<Input> keypressQueue;
  private Queue<String> clientIn;

  private Thread outgoingPacketManager;
  private Thread incomingPacketManager;

  private PacketSender sender;
  private PacketReceiver receiver;

  private ArrayList<InetAddress> serverIP;

  private int counter = 0;
  private boolean running = true;

  // clientIn gets recievedStrings
  public ClientGameplayHandler(
      InetAddress serverIP, Queue<Input> keypressQueue, Queue<String> clientIn) throws IOException {
    outgoingQueue = new ConcurrentLinkedQueue<>();
    incomingQueue = new ConcurrentLinkedQueue<>();
    this.keypressQueue = (BlockingQueue<Input>) keypressQueue;
    this.clientIn = clientIn;

    this.serverIP = new ArrayList<>();
    this.serverIP.add(serverIP);

    initialisePacketManagers();

    // outgoingQueue.add("POS");

    this.sender =
        new PacketSender(NetworkUtility.SERVER_DGRAM_PORT, this.outgoingQueue, this.serverIP);
    this.receiver = new PacketReceiver(NetworkUtility.CLIENT_DGRAM_PORT, incomingQueue);
    this.incomingPacketManager.start();
    this.outgoingPacketManager.start();
    this.receiver.start();
    this.sender.start();
  }

  /** Initialises the incoming and outgoing packet managers */
  private void initialisePacketManagers() {
    // puts inputs from queues into the outgoing queue
    this.outgoingPacketManager =
        new Thread() {
          public void run() {
            Input key;
            while (!isInterrupted() && running) {
              try {
                key = keypressQueue.take();
                // sends inputs as strings, which are converted back by ServerGameplay handler
                System.out.println("ADDING KEY TO OUTGOING QUEUE: " + key.toString());
                outgoingQueue.add(key.toString());
                Thread.sleep(50);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        };

    // reads inputs from the incoming queue into the relevant queue - position queue or collision
    // queue
    this.incomingPacketManager =
        new Thread() {
          public void run() {
            while (!isInterrupted() && running) {
              try {
                if (incomingQueue.isEmpty()) {
                  continue;
                }
                counter++;
                System.out.println("CLIENT RECEIVED -> " + incomingQueue.peek());
                System.out.println("THIS WAS PACKET NUMBER: " + counter);
                String data = incomingQueue.poll();

                if (data.startsWith(NetworkUtility.POSITION_CODE)
                    || data.startsWith(NetworkUtility.POWERUP_CODE)
                    || data.startsWith(NetworkUtility.SCORE_CODE)
                    || data.startsWith(NetworkUtility.COLLISIONS_CODE)) {
                  clientIn.add(data);
                  System.out.println("Got instruction from server");
                } else if (data.startsWith(NetworkUtility.STOP_CODE)) {
                  clientIn.add(data);
                  close();
                } else {
                  throw new Exception();
                }
                Thread.sleep(50);
              } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Argument in incoming queue had invalid code");
              }
            }
          }
        };
  }
  private void close(){
    receiver.shutdown();
    sender.shutdown();
    running = false;
  }

}
