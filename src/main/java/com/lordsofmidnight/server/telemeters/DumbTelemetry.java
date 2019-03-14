package com.lordsofmidnight.server.telemeters;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.server.NetworkUtility;
import com.lordsofmidnight.utils.GameLoop;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;

public class DumbTelemetry extends Telemetry {

  private BlockingQueue<String> inputs;
  private Queue<Input> clientQueue;
  // private GameLoop inputProcessor;

  // dumb telemetry is like telemetry but it relies on information from the com.lordsofmidnight.server to set it's
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

  // Not needed as the only input received is from com.lordsofmidnight.server and not from client.
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
        case "POW1":
          activatePowerup(input.substring(4));
          break;
        case "SCOR":
          setScore(input.substring(5));
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
    for (Entity ent : agents) {
      if (ent.getClientId() == mipID) {
        ent.setMipsman(true);
      } else {
        ent.setMipsman(false);
      }
    }

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
    System.out.println("Movement to handle: " + s);
    String[] ls = s.split("\\|");
    Input input = Input.fromString(ls[0]);
    int id = input.getClientID();
    double x = Double.valueOf(ls[1]);
    double y = Double.valueOf(ls[2]);
    System.out.println("X: " + x);
    System.out.println("Y: " + y);
    System.out.println("ID: " + id);
    agents[id].setLocation(new Point(x, y, map));
    agents[id].setDirection(input.getMove());
    int MIPID = Integer.parseInt(ls[3]);
    for (Entity ent : agents) {
      if (ent.getClientId() == MIPID) {
        ent.setMipsman(true);
      } else {
        ent.setMipsman(false);
      }
    }
  }

  // takes a packet string as defined in
  // NetworkUtility.makeScorePacket(Entity[])
  // without the starting SCOR| string
  private void setScore(String scores) {
    try {
      String[] ls = scores.split("\\|");
      for (int i = 0; i < ls.length; i++) {
        int score = Integer.parseInt(ls[i]);
        agents[i].setScore(score);
      }
    } catch (NumberFormatException e) {
      System.out.println("ERROR: INVALID SCORE");
    }
  }

  // takes a packet string as defined in
  // NetworkUtility.makePowerUPPacket(Input, Point)
  // without the starting POW1 code
  private void activatePowerup(String s) {
    System.out.println("Powerup String to handle: " + s);
    String[] ls = s.split("\\|");

    int id = Integer.parseInt(ls[0]);
    int powerint = Integer.parseInt(ls[1]);
    double x = Double.valueOf(ls[2]);
    double y = Double.valueOf(ls[3]);

    agents[id].setLocation(new Point(x, y, map));
    PowerUp powerup = PowerUp.fromInt(powerint);
    // TODO nullpointer when powerup tries to calculate com.lordsofmidnight.gamestate, one for @alex & @matty

    //   powerup.use(agents[id], activePowerUps);
  }

  public void startAI() {
    // haha trick this does nothing.
    // shouldn't actually be called from client if this object exists
    System.err.println("DumbTelemetry startAI");
  }
}
