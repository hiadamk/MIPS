package server;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import objects.Entity;
import objects.Pellet;
import utils.GameLoop;
import utils.Input;
import utils.Map;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.Direction;

public class DumbTelemetry implements Telemeters {

  private static final int AGENT_COUNT = 2;
  private BlockingQueue<String> inputs;
  private Entity[] agents;
  private Map map;
  private HashMap<String, Pellet> pellets;
  private Queue<Input> clientQueue;
  private ResourceLoader resourceLoader;

  // dumb telemetry is like telemetry but it relies on information from the server to set it's
  // entites
  // rather than using any AI.
  // it is the client's telemetry.
  public DumbTelemetry(Map map, Queue<String> inputQueue, ResourceLoader resourceLoader) {
    this.map = map;
    this.resourceLoader = resourceLoader;
    inputs = (BlockingQueue<String>) inputQueue;
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
  public static void processPhysics(
      Entity[] agents, Map m, ResourceLoader resourceLoader, HashMap<String, Pellet> pellets) {

    for (int i = 0; i < AGENT_COUNT; i++) {
      if (agents[i].getDirection() != null) {
        Point prevLocation = agents[i].getLocation();
        agents[i].move();
        Point faceLocation = agents[i].getFaceLocation();

        if (m.isWall(faceLocation)) {
          System.out.println("~Player" + i + " drove into a wall");
          agents[i].setLocation(prevLocation.centralise());
          agents[i].setDirection(null);
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
    pelletCollision(agents, pellets);
  }

  private static void pelletCollision(Entity[] agents, HashMap<String, Pellet> pellets) {
    for (Entity agent : agents) {
      if (!agent.isPacman()) {
        continue;
      }
      Point p = agent.getFaceLocation();
      int x = (int) p.getX();
      int y = (int) p.getY();
      Pellet pellet = pellets.get(Integer.toString(x) + y);
      if (pellet != null && pellet.isActive()) {
        pellet.setActive(false);
        agent.incrementScore();
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

      // System.out.println("ghoul " + ghoul.getClientId() + " collided with mipsman " +
      // pacman.getClientId());
    }
  }

  public HashMap<String, Pellet> getPellets() {
    return pellets;
  }

  private void initialise() {
    agents = new Entity[AGENT_COUNT];
    agents[0] = new Entity(false, 0, new Point(1.5, 2.5, map));
    agents[1] = new Entity(false, 1, new Point(1.5, 18.5, map));
    //        agents[2] = new Entity(false, 2, new Point(1.5, 16.5, map));
    //        agents[3] = new Entity(false, 3, new Point(11.5, 2.5, map));
    //        agents[4] = new Entity(false, 4, new Point(14.5, 11.5, map));
    //        agents[(new Random()).nextInt(AGENT_COUNT)].setPacMan(true);

    pellets = new HashMap<String, Pellet>();
    for (int i = 0; i < map.getMaxX(); i++) {
      for (int j = 0; j < map.getMaxY(); j++) {
        Point point = new Point(i + 0.5, j + 0.5);
        if (!map.isWall(point)) {
          Pellet pellet = new Pellet(point);
          pellet.updateImages(resourceLoader);
          pellets.put(Integer.toString(i) + j, pellet);
        }
      }
    }
  }

  public void setMipID(int ID) {
    this.agents[ID].setPacMan(true);
  }

  // Not needed as the only input received is from server and not from client.
  @Override
  public void addInput(Input in) {
  }

  public Map getMap() {
    return map;
  }

  public Entity getEntity(int id) {
    return agents[id];
  }

  private void startGame() {
    final long DELAY = (long) Math.pow(10, 7);
    new GameLoop(DELAY) {
      @Override
      public void handle() {
        processInputs();
        processPhysics(agents, map, resourceLoader, pellets);
      }
    }.start();
  }

  private void processInputs() {
    while (!inputs.isEmpty()) {
      System.out.println("Dumb Telemetry received: " + inputs.peek());
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
          break;
        // TODO - add code for game end procedures down the game on the server end
        default:
          throw new IllegalArgumentException();
      }
    }
  }

  // takes a packet string as defined in
  // NetworkUtility.makeEntitiesPositionPacket(Entity[])
  // without the starting POSx code
  private void setEntityPositions(String s) {
    String[] positions = s.split("\\|");
    int mipID = Integer.parseInt(positions[positions.length-1]);
    agents[mipID].setPacMan(true);
//    agents[positions[Integer.papositions.length-1]]
    for (int i = 0; i < positions.length-1; i++) {
      String[] ls = positions[i].split(":");
      int id = Integer.parseInt(ls[0]);
      Double x = Double.valueOf(ls[1]);
      Double y = Double.valueOf(ls[2]);
      agents[id].setLocation(new Point(x, y, map));
    }
  }

  // takes a packet string as defined in
  // NetworkUtility.makeEntityMovementPacket(Input, Point)
  // without the starting POSx code
  private void setEntityMovement(String s) {
    String[] ls = s.split("\\|");
    Input input = Input.fromString(ls[0]);
    int id = input.getClientID();
    double x = Double.valueOf(ls[1]);
    double y = Double.valueOf(ls[2]);
    agents[id].setLocation(new Point(x, y));
    agents[id].setDirection(input.getMove());
  }

  public void startAI() {
    // haha trick this does nothing.
    // shouldn't actually be called from client if this object exists
  }

  public Entity[] getAgents() {
    return agents;
  }
}
