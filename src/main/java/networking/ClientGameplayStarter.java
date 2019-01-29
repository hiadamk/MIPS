package networking;

import java.net.DatagramSocket;
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
		UDPListenerThread clientListener = new UDPListenerThread(socket, incomingQueue);
		UDPSenderThread clientSender = new UDPSenderThread(socket, outgoingQueue);
		
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
							clientListener.shutdown();
							clientSender.shutdown();
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
