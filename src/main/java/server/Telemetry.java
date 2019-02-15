package server;

import ai.AILoopControl;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.Direction;

public class Telemetry {

  private static final int AGENT_COUNT = 5;
  private BlockingQueue<Input> inputs;
  private BlockingQueue<Input> outputs;
  private Entity[] agents;
  private boolean singlePlayer;
  private Map map;
  private Queue<Input> clientQueue;
  private ServerGameplayHandler server;
  private AILoopControl ai;
  private boolean aiRunning;
  private ResourceLoader resourceLoader;

  public Telemetry(Map map, ServerGameplayHandler server) {
    this.map = map;
    inputs = new LinkedBlockingQueue<>();
    outputs = new LinkedBlockingQueue<>();

    this.server = server;
    this.singlePlayer = false;

    initialise();
    startGame();
  }

  public Telemetry(Map map, Queue<Input> clientQueue, ResourceLoader resourceLoader) {
    this.map = map;
    inputs = new LinkedBlockingQueue<>();
    outputs = new LinkedBlockingQueue<>();
    singlePlayer = true;
    this.clientQueue = clientQueue;
    this.resourceLoader = resourceLoader;
    initialise();
    startGame();
  }

  /**
   * Static method for updating game state increments positions if valid, increments points, and
   * detects and treats entity collisions
   *
   * <p>TODO: increment points functionality
   *
   * @param agents array of entities in current state
   * @author Alex Banks, Matthew Jones
   * @see this#detectEntityCollision(Entity, Entity, ResourceLoader)
   */
  public static void processPhysics(Entity[] agents, Map m, ResourceLoader resourceLoader) {

    for (int i = 0; i < AGENT_COUNT; i++) {
      if (agents[i].getDirection() != null) {
        Point prevLocation = agents[i].getLocation();
        agents[i].move();
        Point faceLocation = agents[i].getFaceLocation();

        if (m.isWall(faceLocation)) {
          System.err.println(i + "prev: " + prevLocation);
          System.err.println(i + "face: " + faceLocation);
          agents[i].setLocation(prevLocation);
          agents[i].setDirection(null);
//        } else {
//          System.out.println(i + "face: " + faceLocation);
        }
      }
    }
    // TODO add points for pellet collision

    // separate loop for checking collision after iteration

    for (int i = 0; i < AGENT_COUNT; i++) {
      for (int j = (i + 1); j < AGENT_COUNT; j++) {

        if (agents[i].isPacman() && !agents[j].isPacman()) {
          detectEntityCollision(agents[i], agents[j], resourceLoader);
        }

        if (agents[j].isPacman() && !agents[i].isPacman()) {
          detectEntityCollision(agents[j], agents[i], resourceLoader);
        }
      }
    }
  }

  /**
   * Static method for 'swapping' a pacman and ghoul if they occupy the same area.
   *
   * @param pacman Entity currently acting as pacman
   * @param ghoul Entity currently running as ghoul
   * @author Alex Banks, Matthew Jones
   */
  private static void detectEntityCollision(
      Entity pacman, Entity ghoul, ResourceLoader resourceLoader) {
    Point pacmanCenter = pacman.getLocation();
    Point ghoulFace = ghoul.getFaceLocation();

    if (pacmanCenter.inRange(ghoulFace)) {

      pacman.setPacMan(false);
      ghoul.setPacMan(true);
      pacman.setLocation(resourceLoader.getMap().getRandomSpawnPoint());
      pacman.setDirection(Direction.UP);
      pacman.updateImages(resourceLoader);
      ghoul.updateImages(resourceLoader);

      System.out.println(
          "ghoul " + ghoul.getClientId() + " collided with mipsman " + pacman.getClientId());
    }
  }

  private void initialise() {
    agents = new Entity[AGENT_COUNT];
    agents[0] = new Entity(false, 0, new Point(1.5, 2.5, map));
    agents[1] = new Entity(false, 1, new Point(1.5, 18.5, map));
    agents[2] = new Entity(false, 2, new Point(1.5, 16.5, map));
    agents[3] = new Entity(false, 3, new Point(11.5, 2.5, map));
    agents[4] = new Entity(false, 4, new Point(14.5, 11.5, map));
    agents[(new Random()).nextInt(AGENT_COUNT)].setPacMan(true);

    System.out.println(Arrays.toString(agents));
    int aiCount = AGENT_COUNT - (server == null ? 1 : server.getPlayerCount());
    if (aiCount < 0) {
      aiCount = 0;
    }
    int[] aiControlled = new int[aiCount];
    int highestId = AGENT_COUNT - 1;
    for (int i = 0; i < aiCount; i++) {
      aiControlled[i] = highestId;
      highestId--;
    }
    aiRunning = false;
    ai = new AILoopControl(agents, aiControlled, map, inputs);
  }

  public Map getMap() {
    return map;
  }

  public Entity getEntity(int id) {
    return agents[id];
  }

  public void addInput(Input in) {
    inputs.add(in);
  }

  private void startGame() {
    final long DELAY = 1000000;
    // TODO implement

    new AnimationTimer() {
      long change;
      long oldTime = System.nanoTime();

      @Override
      public void handle(long now) {
        change = now - oldTime;
        if (change >= DELAY) {
          oldTime = now;
          // System.out.println(change);
          processInputs();
          informClients();
          processPhysics(agents, map, resourceLoader);
          updateClients();
          //        } else {
          //           System.out.println("skipped");
        }
      }
    }.start();
  }

  public void startAI() {
    if (!aiRunning && ai != null) {
      ai.start();
    }
  }

  private void processInputs() {
    while (!inputs.isEmpty()) {
      Input input = inputs.poll();
      int id = input.getClientID();
      if (id != 0) {
        System.err.println("none 0 input found");
      }
      Direction d = input.getMove();
      if (Methods.validiateDirection(d, agents[id], map)) {
        agents[id].setDirection(d);
        outputs.add(input); // To send to the other clients
      }
    }
  }

  private void informClients() {
    while (!outputs.isEmpty()) {
      Input input = outputs.poll();
      if (singlePlayer) {
        clientQueue.add(input);
      } else {
        server.sendPacket(input);
      }
    }
  }

  private void updateClients() {
    // TODO implement
  }

  public Entity[] getAgents() {
    return agents;
  }
}
