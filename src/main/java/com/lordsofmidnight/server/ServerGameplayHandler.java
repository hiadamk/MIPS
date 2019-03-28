package com.lordsofmidnight.server;

import com.lordsofmidnight.utils.Input;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// input switched from string to input form in this stage
public class ServerGameplayHandler {

  private Queue<Input> inputQueue;
  private Queue<String> outgoingQueue;
  private Queue<String> incomingQueue;

  private Thread incomingPacketManager;

  private PacketSender sender;
  private PacketReceiver receiver;

  private ArrayList<InetAddress> ipStore;

  private int playerCount;
  private boolean running = true;

  /**
   * Creates the appropriate senders and recievers for the running of the game and starts them.
   * the reciever turns strings recieved into {@link Input}s
   * the queues feed into telemetry.
   */

  public ServerGameplayHandler(
      ArrayList<InetAddress> ips,
      int numPlayers,
      Queue<Input> inputQueue,
      Queue<String> outputQueue)
      throws IOException {

    this.inputQueue = inputQueue;
    outgoingQueue = outputQueue;
    incomingQueue = new ConcurrentLinkedQueue<>();
    this.playerCount = numPlayers;
    initialisePacketManagers();

    this.ipStore = ips;
    this.sender = new PacketSender(NetworkUtility.CLIENT_DGRAM_PORT, this.outgoingQueue, ipStore);
    this.receiver = new PacketReceiver(NetworkUtility.SERVER_DGRAM_PORT, this.incomingQueue);
    this.incomingPacketManager.start();
    this.sender.start();
    this.receiver.start();
  }

  /**
   * Initialises the packet managers
   */
  private void initialisePacketManagers() {
    // turns strings from incomingQueue into Inputs and adds them to the inputQueue
    this.incomingPacketManager =
        new Thread() {
          public void run() {
            while (!isInterrupted() && running) {
              if (incomingQueue.isEmpty()) {
                continue;
              }
              inputQueue.add(Input.fromString(incomingQueue.poll()));

              try {
                Thread.sleep(1);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        };
  }
/**
 * Closes the threads.
 * */
  public void close() {
    receiver.shutdown();
    sender.shutdown();
    running = false; //shuts down incoming packet manager
  }
}


