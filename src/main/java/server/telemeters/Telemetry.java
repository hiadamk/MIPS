package server.telemeters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import main.Client;
import objects.Entity;
import objects.Pellet;
import objects.PowerUpBox;
import utils.GameLoop;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.Direction;
import utils.enums.PowerUp;

/**
 * Parent class for DumbTelemetry and HostTelemetry
 */
public abstract class Telemetry {

  static final int AGENT_COUNT = 5;
  static final int GAME_TIME = 30 * 100; // Number of seconds *100
  static int gameTimer = 0;
  Map map;
  Entity[] agents;
  HashMap<String, Pellet> pellets;
  ResourceLoader resourceLoader;
  static Client client;
  protected GameLoop inputProcessor;
  protected GameLoop positionUpdater;
  protected GameLoop scoreUpdater;

  Telemetry(Client client) {
    this.map = client.getMap();
    Telemetry.client = client;
    this.resourceLoader = client.getResourceLoader();
    this.agents = client.getAgents();
  }

  ArrayList<PowerUp> activePowerUps = new ArrayList<>();
  // abstract methods

  abstract void startAI();

  public abstract void addInput(Input in);

  public abstract void startGame();

  abstract void processInputs();

  public abstract void stopGame();

  // basic get/set methods

  public Entity[] getAgents() {
    return agents;
  }

  public Map getMap() {
    return map;
  }

  public HashMap<String, Pellet> getPellets() {
    return pellets;
  }

  public void setMipID(int ID) {
    this.agents[ID].setMipsman(true);
  }

  // constructor methods

  void initialiseEntities() {

    agents = new Entity[AGENT_COUNT];
    switch (AGENT_COUNT) {
      default: {
        for (int i = AGENT_COUNT-1; i>=5; i--) {
          agents[i] = new Entity(false, i, new Point(1.5, 1.5));
        }
      }
      case 5:
        agents[4] = new Entity(false, 4, map.getRandomSpawnPoint());
      case 4:
        agents[3] = new Entity(false, 3, map.getRandomSpawnPoint());
      case 3:
        agents[2] = new Entity(false, 2, map.getRandomSpawnPoint());
      case 2:
        agents[1] = new Entity(false, 1, map.getRandomSpawnPoint());
      case 1:
        agents[0] = new Entity(false, 0, map.getRandomSpawnPoint());
    }

    Methods.updateImages(agents, resourceLoader);
  }

  void initialisePellets() {
    Random r = new Random();
    pellets = new HashMap<>();
    for (int i = 0; i < map.getMaxX(); i++) {
      for (int j = 0; j < map.getMaxY(); j++) {
        Point point = new Point(i + 0.5, j + 0.5);
        if (!map.isWall(point)) {
          Pellet pellet = r.nextInt(50) == 1 ? new PowerUpBox(point) : new Pellet(point);
          pellet.updateImages(resourceLoader);
          pellets.put(i + "," + j, pellet);
        }
      }
    }
  }

  // physics engine

  /**
   * Static method for updating game state increments positions if valid, increments points, and
   * detects and treats entity collisions
   *
   * @param agents array of entities in current state
   * @author Alex Banks, Matthew Jones
   * @see this#detectEntityCollision(Entity, Entity, ResourceLoader)
   */
  static void processPhysics(
      Entity[] agents,
      Map m,
      ResourceLoader resourceLoader,
      HashMap<String, Pellet> pellets,
      ArrayList<PowerUp> activePowerUps) {

    for (int i = 0; i < AGENT_COUNT; i++) {
      if (agents[i].getDirection() != null) {
        Point prevLocation = agents[i].getLocation();
        agents[i].move();
        Point faceLocation = agents[i].getFaceLocation();

        if (m.isWall(faceLocation)) {
	        //System.out.println("~Player" + i + " drove into a wall");
          agents[i].setLocation(prevLocation.centralise());
          agents[i].setDirection(null);
        }
      }
    }

    // separate loop for checking collision after iteration

    for (int i = 0; i < AGENT_COUNT; i++) {
      for (int j = (i + 1); j < AGENT_COUNT; j++) {

        if (agents[i].isMipsman() && !agents[j].isMipsman()) {
          detectEntityCollision(agents[i], agents[j], resourceLoader);
        }

        if (agents[j].isMipsman() && !agents[i].isMipsman()) {
          detectEntityCollision(agents[j], agents[i], resourceLoader);
        }
      }
    }

    pelletCollision(agents, pellets);
    for (Pellet p : pellets.values()) {
      p.incrementRespawn();
    }
    for (PowerUp p : activePowerUps) {
      if (p.incrementTime()) {
        activePowerUps.remove(p);
      }
    }
    gameTimer++;
    if (gameTimer == GAME_TIME) {
      System.out.println("GAME HAS ENDED ITS OVER");
      System.out.println("GAME HAS ENDED ITS OVER");
      System.out.println("GAME HAS ENDED ITS OVER");
      System.out.println("GAME HAS ENDED ITS OVER");
      System.out.println("GAME HAS ENDED ITS OVER");
      System.out.println("GAME HAS ENDED ITS OVER");
      System.out.println("GAME HAS ENDED ITS OVER");
      System.out.println("GAME HAS ENDED ITS OVER");
      int winner = Methods.findWinner(agents);
      System.out.println("Player " + winner + " won the game");
      System.out.println("Player " + winner + " won the game");
      System.out.println("Player " + winner + " won the game");
      System.out.println("Player " + winner + " won the game");
    }
  }

  /**
   * Static method for 'swapping' a mipsman and ghoul if they occupy the same area.
   *
   * @param mipsman Entity currently acting as mipsman
   * @param ghoul Entity currently running as ghoul
   * @author Alex Banks, Matthew Jones
   */
  private static void detectEntityCollision(
      Entity mipsman, Entity ghoul, ResourceLoader resourceLoader) {
    Point mipsmanCenter = mipsman.getLocation();
    Point ghoulFace = ghoul.getFaceLocation();

    if (mipsmanCenter.inRange(ghoulFace)) { //check temporary invincibility here
      client.collisionDetected(ghoul);
      mipsman.setMipsman(false);
      ghoul.setMipsman(true);
      mipsman.setLocation(resourceLoader.getMap().getRandomSpawnPoint());
      mipsman.setDirection(Direction.UP);
      mipsman.updateImages(resourceLoader);
      ghoul.updateImages(resourceLoader);

      // System.out.println("~Ghoul" + ghoul.getClientId() + " captured Mipsman" +
      // mipsman.getClientId());
    }
  }

  /**
   * Static method to detect if the mipsman entity will eat a pellet
   *
   * @param agents The entities
   * @param pellets The pellets
   * @author Matthew Jones
   */
  private static void pelletCollision(Entity[] agents, HashMap<String, Pellet> pellets) {
    for (Entity agent : agents) {
      Point p = agent.getLocation();
      int x = (int) p.getX();
      int y = (int) p.getY();
      Pellet pellet = pellets.get(x + "," + y);
      if (pellet != null) {
        pellet.interact(agent);
      }
    }
  }

  public GameLoop getInputProcessor() {
    return inputProcessor;
  }
}
