package com.lordsofmidnight.server;

import static com.lordsofmidnight.server.NetworkUtility.CLIENT_M_PORT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServerBrowser extends Thread {

  private ArrayList<InetAddress> addressList;

  /**
   * Adds servers to a list by repeatedly checking for a message in the group and getting the IP of
   * a com.lordsofmidnight.server from it.
   */
  public ServerBrowser(ArrayList<InetAddress> addressList) {
    this.addressList = addressList;
  }

  public void run() {

    try {
      while (!isInterrupted()) {
        System.out.println("Getting a com.lordsofmidnight.server address");
        MulticastSocket socket = new MulticastSocket(CLIENT_M_PORT);
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
        System.out.println("New Server Address: " + packet.getAddress());

        addressList.add(packet.getAddress());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
