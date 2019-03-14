package com.lordsofmidnight.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.lordsofmidnight.utils.Input;

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
    // puts inputs from queues into the outgoing queue - not sure this one closes
    this.incomingPacketManager =
        new Thread() {
          public void run() {
            while (!isInterrupted() && running) {
              if (incomingQueue.isEmpty()) {
                continue;
              }
              System.out.println("SERVER RECEIVED -> " + incomingQueue.peek());
              inputQueue.add(Input.fromString(incomingQueue.poll()));

              try {
                Thread.sleep(50);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        };
  }

  public void close() {
    receiver.shutdown();
    sender.shutdown();
    running = false; //shuts down incoming packet manager
  }
}


