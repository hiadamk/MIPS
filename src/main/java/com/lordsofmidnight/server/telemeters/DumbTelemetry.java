package com.lordsofmidnight.server.telemeters;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.objects.EmptyPowerUpBox;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.server.NetworkUtility;
import com.lordsofmidnight.utils.GameLoop;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Behaves similar to Host Telemetry but relies on input from the server exclusively to know what is
 * happening with the entities rather than using AI
 */
public class DumbTelemetry extends Telemetry {

  private BlockingQueue<String> inputs;
  private Queue<Input> clientQueue;

  public DumbTelemetry(Queue<String> inputQueue, Client client, AudioController audioController) {
    super(client, audioController);
    inputs = (BlockingQueue<String>) inputQueue;
    initialise();
  }

  /**
   * Initialises the pellets on the maps and the entities
   */
  private void initialise() {
    initialiseEntities();
    initialisePellets();
  }


  /**
   * Populates the map with pellets
   */
  @Override
  void initialisePellets() {
    Pellet pellet;
    pellets = new PointMap<>(map);
    for (int i = 0; i < map.getMaxX(); i++) {
      for (int j = 0; j < map.getMaxY(); j++) {
        Point point = new Point(i + 0.5, j + 0.5);
        if (!map.isWall(point)) {
          pellet = new Pellet(point);
          pellet.updateImages(resourceLoader);
          pellets.put(new Point(i, j), pellet);
        }
      }
    }
  }

  /**
   * Not needed as the only input received is from server and not from client.
   *
   * @param in Erroneous input
   */
  public void addInput(Input in) {
    System.err.println("DumbTelemetry receiving inputs");
  }

  /**
   * Starts the main game loop for the client and processing of inputs.
   */
  public void startGame() {
    System.out.println("Started dumb telemetry");
    gameTimer = GAME_TIME;
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

  /**
   * Removes each of the inputs in the queue and performs the appropriate action based on the contents.
   */
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
        case "POW0":
          updateInventory(input.substring(5));
          break;
        case "POW1":
          activatePowerup(input.substring(4));
          break;
        case "POW2":
          setPowerupBox(input.substring(4));
          break;
        case "SCOR":
          setScore(input.substring(5));
          break;
        case NetworkUtility.STOP_CODE:
          //set client flag that server has left
          client.setHostGone(true);
          stopGame();
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
  }


  /**
   * Called when the server informs the client that the game needs to end.
   */
  @Override
  public void stopGame() {
    inputProcessor.close();

  }

  /**
   * Updates the positions each of the entities
   * @param s Packet containing the positions of each of the entities as defined by
   * NetworkUtility.makeEntitiesPositionPacket(Entity[])
   */
  private void setEntityPositions(String s) {
    String[] positions = s.split("\\|");
    int mipID = Integer.parseInt(positions[positions.length - 2]);
    int gameTime = Integer.parseInt(positions[positions.length - 1]);
    setTime(gameTime);
    for (Entity ent : agents) {
      if (ent.getClientId() == mipID) {
        ent.setMipsman(true);
      } else {
        ent.setMipsman(false);
      }
    }

    for (int i = 0; i < positions.length - 2; i++) {
      String[] ls = positions[i].split(":");
      int id = Integer.parseInt(ls[0]);
      int direction = Integer.parseInt(ls[1]);
      Double x = Double.valueOf(ls[2]);
      Double y = Double.valueOf(ls[3]);
      agents[id].setLocation(x, y);
      agents[id].setDirection(Direction.fromInt(direction));
    }
  }

  /**
   * Updates the client od current movement status of a given entity
   * @param s String containing packet about where each of the entities are moving.
   */
  private void setEntityMovement(String s) {
    String[] ls = s.split("\\|");
    Input input = Input.fromString(ls[0]);
    int id = input.getClientID();
    double x = Double.valueOf(ls[1]);
    double y = Double.valueOf(ls[2]);
    agents[id].setLocation(x, y);
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

  /**
   * Sets the scores for each client to maintain consistency
   * @param scores Packet containing scores of each client
   */
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

  /**
   * Updates the inventories of all clients
   * @param s Packet containing the inventories of each of the clients
   */
  private void updateInventory(String s) {
    String[] inventories = s.split("\\|");
    for (String inventory : inventories) {
      String[] ls = inventory.split(":");
      int id = Integer.parseInt(ls[0]);
      switch (ls.length) {
        case 1:
          agents[id].setItems();
          break;
        case 2:
          agents[id].setItems(Integer.parseInt(ls[1]));
          break;
        case 3:
          agents[id].setItems(Integer.parseInt(ls[1]), Integer.parseInt(ls[2]));
          break;
        default:
          throw new IndexOutOfBoundsException();
      }
    }
  }

  /**
   * Adds a power up box to the pellets
   * @param s The packet used to relay the power up information
   */
  private void setPowerupBox(String s) {
    String[] ls = s.split("\\|");
    double x = Double.valueOf(ls[0]);
    double y = Double.valueOf(ls[1]);
    Point point = new Point(x,y);
    pellets.remove(point);
    EmptyPowerUpBox pellet = new EmptyPowerUpBox(point);
    pellet.updateImages(resourceLoader);
    pellets.put(point, pellet);
  }

  /**
   * Handles activation of a power up for a client
   * @param s Packet String as defined in NetworkUtility.makePowerUPPacket(Input, Point)
   */
  private void activatePowerup(String s) {
    String[] ls = s.split("\\|");

    int id = Integer.parseInt(ls[0]);
    int powerint = Integer.parseInt(ls[1]);
    double x = Double.valueOf(ls[2]);
    double y = Double.valueOf(ls[3]);

    agents[id].setLocation(x, y);
    PowerUp powerup = PowerUp.fromInt(powerint);
    powerup.use(agents[id], activePowerUps, pellets, agents, audioController);
  }

  /**
   * Redundant method for a dumb telemeter as it doesn't control the AI
   */
  public void startAI() { }

}
