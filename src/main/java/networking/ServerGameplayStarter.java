package networking;
import networking.NetworkingData;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerGameplayStarter {
	private static String collisionCode = networking.NetworkingData.collisionsCode;
	private static String positionCode = networking.NetworkingData.positionCode;
	private static String stopCode = networking.NetworkingData.stopCode;

	
	/** starts communication to client on the connected socket, sending data in position queue and collision queue
	 * and passing received data to the keypress queue. 
	 * As this method is static, there is not yet a clean method to close connections, so in future renaming this class to
	 * ServerGameplayHandler, creating one object per class, and calling it's stop method may be the best option.
	 * this by keeping track of 
	 * @param socket
	 * @param positionQueue
	 * @param collisionQueue
	 * @param keypressQueue
	 */
	public static void start(DatagramSocket socket, Queue<String> positionQueue, Queue<String> collisionQueue, BlockingQueue<Integer> keypressQueue) {


		Queue<String> outgoingQueue = new ConcurrentLinkedQueue<String>(); 
		Queue<String> incomingQueue = new ConcurrentLinkedQueue<String>();
		UDPListenerThread r1 = new UDPListenerThread(socket, incomingQueue);
		UDPSenderThread r2 = new UDPSenderThread(socket, outgoingQueue);
		Thread serverListener = new Thread(r1); 
    	Thread serverSender = new Thread(r2);
		
		//puts inputs from queues into the outgoing queue - not sure this one closes
		Thread incomingPackager = new Thread() {
			public void run() {
				Integer key;
				while (true) {
					if (incomingQueue.isEmpty()){continue;}
					key = Integer.valueOf(incomingQueue.poll());
					keypressQueue.add(key);
					
				}
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
						if (positionQueue.isEmpty()) {continue;}
						else {
							String data = collisionQueue.poll();
							data = collisionCode + data;
							outgoingQueue.add(data);
						}
						if (positionQueue.isEmpty()) {continue;}
						else {
							String data = positionQueue.poll();
							data = positionCode + data;
							outgoingQueue.add(data);
						}
					
					} catch ( Exception e) {
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
	public static void stop(DatagramSocket Socket) {
		//TODO 
	}
	
}
