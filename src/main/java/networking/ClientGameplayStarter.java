package networking;
import networking.NetworkingData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientGameplayStarter {
	private static String collisionCode = networking.NetworkingData.collisionsCode;
	private static String positionCode = networking.NetworkingData.positionCode;
	private static String stopCode = networking.NetworkingData.stopCode;
	static Thread clientListener;
	static Thread clientSender;
	public static void start(DatagramSocket socket, Queue<String> positionQueue, Queue<String> collisionQueue, BlockingQueue<Integer> keypressQueue) {


		Queue<String> outgoingQueue = new ConcurrentLinkedQueue<String>(); 
		Queue<String> incomingQueue = new ConcurrentLinkedQueue<String>();
		UDPListenerThread r1 = new UDPListenerThread(socket, incomingQueue);
		UDPSenderThread r2 = new UDPSenderThread(socket, outgoingQueue);
		clientListener = new Thread(r1); // clientListener
		clientSender = new Thread(r2);//clientSender
		
		//puts inputs from queues into the outgoing queue - not sure this one closes
		Thread outgoingPackager = new Thread() {
			public void run() {
				Integer key;
				while (true) {
					try {
						key = keypressQueue.take();
						outgoingQueue.add( key.toString());
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
				boolean running = true;
				while (running) {
					try {
						if (incomingQueue.isEmpty()) {continue;}
						String data = incomingQueue.poll();
						
						if (data.startsWith(positionCode)){
							positionQueue.add(data.substring(positionCode.length()));
						}
						else if (data.startsWith(collisionCode)) {
							collisionQueue.add(data.substring(collisionCode.length()));
						}else if (data.startsWith(stopCode)) {
							running = false;
							r1.stop();
							r2.stop();
							socket.close();
							System.out.println("Recieved Stop Instruction");
						}
						else {
							throw new Exception();
						}
					} catch ( Exception e) {
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
	
}
