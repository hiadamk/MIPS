package server;

import static java.lang.Math.abs;
import static utils.Methods.mod;

import ai.AILoopControl;
import java.awt.geom.Point2D.Double;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.Map;
import utils.Methods;
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
   * TODO: increment points functionality
   *
   * @param agents array of entities in current state
   * @author Alex Banks, Matthew Jones
   * @see this#detectEntityCollision(Entity, Entity, ResourceLoader)
   */
  public static void processPhysics(Entity[] agents, Map m, ResourceLoader resourceLoader) {

    final int MAXX = m.getMaxX();
    final int MAXY = m.getMaxY();

    for (int i = 0; i < AGENT_COUNT; i++) {
      Double prevLocation = agents[i].getLocation();
      double offset = agents[i].getVelocity();

      double nextX = prevLocation.getX();
      double nextY = prevLocation.getY();

      if (agents[i].getDirection() != null) {
        switch (agents[i].getDirection()) {
          case RIGHT:
            nextX = mod(nextX + offset, MAXX);
            nextY = mod((int) nextY + 0.5, MAXY);
            break;
          case LEFT:
            nextX = mod(nextX - offset, MAXX);
            nextY = mod((int) nextY + 0.5, MAXY);
            break;
          case DOWN:
            nextY = mod(nextY + offset, MAXY);
            nextX = mod((int) nextX + 0.5, MAXX);
            break;
          case UP:
            nextY = mod(nextY - offset, MAXY);
            nextX = mod((int) nextX + 0.5, MAXX);
            break;
        }

        agents[i].setLocation(new Double(nextX, nextY));
        Double faceLocation = agents[i].getFaceLocation(MAXX, MAXY);

        if (m.isWall(faceLocation)) {
          agents[i].setDirection(null);
          agents[i].setLocation(prevLocation);
          System.err.println(i + "prev: " + prevLocation);
          System.err.println(i + "face: " + faceLocation);
        } else {
          System.out.println(i + "face: " + faceLocation);
        }
      }
      // TODO add points for pellet collision
    }

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
    Double pacmanCenter = pacman.getLocation();
    Double ghoulFace =
        ghoul.getFaceLocation(resourceLoader.getMap().getMaxX(), resourceLoader.getMap().getMaxY());

    if (mod(abs(pacmanCenter.getX() - ghoulFace.getX()), resourceLoader.getMap().getMaxX()) <= 0.5
        && mod(abs(pacmanCenter.getY() - ghoulFace.getY()), resourceLoader.getMap().getMaxY())
        <= 0.5) {

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
    agents[0] = new Entity(true, 0, new Double(1.5, 2.5));
    agents[1] = new Entity(false, 1, new Double(1.5, 18.5));
    agents[2] = new Entity(false, 2, new Double(1.5, 16.5));
    agents[3] = new Entity(false, 3, new Double(1.5, 2.5));
    agents[4] = new Entity(false, 4, new Double(1.5, 2.5));
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
