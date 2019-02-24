package server.telemeters;

import java.util.HashMap;
import objects.Entity;
import objects.Pellet;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.Direction;

/**
 * Parent class for DumbTelemetry and HostTelemetry
 */
public abstract class Telemetry {

  static final int AGENT_COUNT = 5;
  Map map;
  Entity[] agents;
  HashMap<String, Pellet> pellets;
  ResourceLoader resourceLoader;

  // abstract methods

  abstract void startAI();

  public abstract void addInput(Input in);

  abstract void startGame();

  abstract void processInputs();

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
    agents[0] = new Entity(false, 0, new Point(1.5, 1.5, map));
    agents[1] = new Entity(false, 1, new Point(1.5, 18.5, map));
    agents[2] = new Entity(false, 2, new Point(9.5, 15.5, map));
    agents[3] = new Entity(false, 3, new Point(11.5, 1.5, map));
    agents[4] = new Entity(false, 4, new Point(14.5, 11.5, map));
    Methods.updateImages(agents, resourceLoader);
  }

  void initialisePellets() {
    pellets = new HashMap<>();
    for (int i = 0; i < map.getMaxX(); i++) {
      for (int j = 0; j < map.getMaxY(); j++) {
        Point point = new Point(i + 0.5, j + 0.5);
        if (!map.isWall(point)) {
          Pellet pellet = new Pellet(point);
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

    if (mipsmanCenter.inRange(ghoulFace)) {

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
      if (!agent.isMipsman()) {
        continue;
      }
      Point p = agent.getLocation();
      int x = (int) p.getX();
      int y = (int) p.getY();
      Pellet pellet = pellets.get(x + "," + y);
      if (pellet != null && pellet.isActive()) {
        pellet.setActive(false);
        agent.incrementScore();
      }
    }
  }
}
