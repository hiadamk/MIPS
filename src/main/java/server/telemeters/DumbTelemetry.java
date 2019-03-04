package server.telemeters;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import main.Client;
import server.NetworkUtility;
import utils.GameLoop;
import utils.Input;
import utils.Point;
import utils.enums.Direction;

public class DumbTelemetry extends Telemetry {

  private BlockingQueue<String> inputs;
  private Queue<Input> clientQueue;
  private GameLoop inputProcessor;

  // dumb telemetry is like telemetry but it relies on information from the server to set it's
  // entites
  // rather than using any AI.
  // it is the client's telemetry.
  public DumbTelemetry(Queue<String> inputQueue, Client client) {
    super(client);
    inputs = (BlockingQueue<String>) inputQueue;
    initialise();
    //    startGame();
  }

  private void initialise() {
    initialiseEntities();
    initialisePellets();
  }

  // Not needed as the only input received is from server and not from client.
  public void addInput(Input in) {
    System.err.println("DumbTelemetry receiving inputs");
  }

  public void startGame() {
    System.out.println("Started dumb telemetry");
    final long DELAY = (long) Math.pow(10, 7);
    inputProcessor =
        new GameLoop(DELAY) {
          @Override
          public void handle() {
            processInputs();
            processPhysics(agents, map, resourceLoader, pellets, activePowerUps);
          }
        };
    inputProcessor.start();
  }

  void processInputs() {
    while (!inputs.isEmpty()) {
      System.out.println("Dumb HostTelemetry received: " + inputs.peek());
      System.out.println(inputs.peek().substring(0, 4));
      String input = inputs.poll();

      switch (input.substring(0, 4)) { // looks at first 4 characters
        case "POS1":
          setEntityMovement(input.substring(4));
          break;
        case "POS3":
          setEntityPositions(input.substring(4));
          break;
        case NetworkUtility.STOP_CODE:
          stopGame();
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
  }

  @Override
  public void stopGame() {
    inputProcessor.close();
    // TODO render stop screen. I imagine somehow the message the game has stopped must be recieved
    // by the client
    // but currently, telemetry is what gets the signal, so DumbTelemetry must somehow communicate
    // to the client
    // that the game is over.
  }

  // takes a packet string as defined in
  // NetworkUtility.makeEntitiesPositionPacket(Entity[])
  // without the starting POSx code
  private void setEntityPositions(String s) {
    String[] positions = s.split("\\|");
    int mipID = Integer.parseInt(positions[positions.length - 1]);
    agents[mipID].setMipsman(true);
    //    agents[positions[Integer.papositions.length-1]]
    for (int i = 0; i < positions.length - 1; i++) {
      String[] ls = positions[i].split(":");
      int id = Integer.parseInt(ls[0]);
      int direction = Integer.parseInt(ls[1]);
      Double x = Double.valueOf(ls[2]);
      Double y = Double.valueOf(ls[3]);
      agents[id].setLocation(new Point(x, y, map));
      agents[id].setDirection(Direction.fromInt(direction));
    }
  }

  // takes a packet string as defined in
  // NetworkUtility.makeEntityMovementPacket(Input, Point)
  // without the starting POSx code
  private void setEntityMovement(String s) {
    System.out.println("String to handle: " + s);
    String[] ls = s.split("\\|");
    Input input = Input.fromString(ls[0]);
    int id = input.getClientID();
    double x = Double.valueOf(ls[1]);
    double y = Double.valueOf(ls[2]);
    System.out.println("X: " + x);
    System.out.println("Y: " + y);
    System.out.println("ID: " + id);
    agents[id].setLocation(new Point(x, y));
    agents[id].setDirection(input.getMove());
    agents[Integer.parseInt(ls[3])].setMipsman(true);
  }

  public void startAI() {
    // haha trick this does nothing.
    // shouldn't actually be called from client if this object exists
    System.err.println("DumbTelemetry startAI");
  }
}
