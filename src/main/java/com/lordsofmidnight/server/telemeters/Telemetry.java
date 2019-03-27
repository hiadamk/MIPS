package com.lordsofmidnight.server.telemeters;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.renderer.ResourceLoader;
import com.lordsofmidnight.utils.GameLoop;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Parent class for DumbTelemetry and HostTelemetry */
public abstract class Telemetry {

  static final int AGENT_COUNT = 5;
  static final int GAME_TIME = 150 * 100; // Number of seconds *100

  public int getGameTimer() {
    return gameTimer;
  }

  protected int gameTimer = GAME_TIME;
  protected int clientID;
  protected Map map;
  Entity[] agents;
  PointMap<Pellet> pellets;
  ResourceLoader resourceLoader;
  static Client client;
  protected GameLoop inputProcessor;
  protected GameLoop positionUpdater;
  protected GameLoop scoreUpdater;
  protected AudioController audioController;

  Telemetry(Client client, AudioController audioController) {
    this.map = client.getMap();
    Telemetry.client = client;
    this.resourceLoader = client.getResourceLoader();
    this.agents = client.getAgents();
    this.audioController = audioController;
  }

  ConcurrentHashMap<UUID, PowerUp> activePowerUps = new ConcurrentHashMap<>();
  // abstract methods

  abstract void startAI();

  public abstract void addInput(Input in);

  public abstract void startGame();

  abstract void processInputs();

  abstract void initialisePellets();

  public abstract void stopGame();

  // basic get/set methods

  public Entity[] getAgents() {
    return agents;
  }

  public Map getMap() {
    return map;
  }

  public PointMap<Pellet> getPellets() {
    return pellets;
  }

  public void setMipID(int ID) {
    this.agents[ID].setMipsman(true);
  }

  // constructor methods

  void initialiseEntities() {

    agents = new Entity[AGENT_COUNT];
    switch (AGENT_COUNT) {
      default:
        {
          for (int i = AGENT_COUNT - 1; i >= 5; i--) {
            agents[i] = new Entity(false, i, new Point(1.5, 1.5));
          }
        }
      case 5:
        agents[4] = new Entity(false, 4, map.getRandomSpawnPoint(agents));
      case 4:
        agents[3] = new Entity(false, 3, map.getRandomSpawnPoint(agents));
      case 3:
        agents[2] = new Entity(false, 2, map.getRandomSpawnPoint(agents));
      case 2:
        agents[1] = new Entity(false, 1, map.getRandomSpawnPoint(agents));
      case 1:
        agents[0] = new Entity(false, 0, map.getRandomSpawnPoint(agents));
    }

    //Methods.updateImages(agents, resourceLoader);
  }

  // physics engine

  /**
   * Static method to detect if the mipsman entity will eat a pellet
   *
   * @param agents The entities
   * @param pellets The pellets
   * @author Matthew Jones
   */
  private static void pelletCollision(
      Entity[] agents, PointMap<Pellet> pellets, ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      AudioController audioController) {
    for (Entity agent : agents) {
      Point p = agent.getLocation();
      Pellet pellet = pellets.get(p);
      if (pellet != null) {
        pellet.interact(agent, agents, activePowerUps, audioController);
      }
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
      Entity mipsman, Entity ghoul, AudioController audioController) {
    if (mipsman.isDead() || ghoul.isDead()) {
      return;
    }
    Point mipsmanCenter = mipsman.getLocation();
    Point ghoulFace = ghoul.getFaceLocation();
    if (mipsmanCenter.inRange(ghoulFace)) { // check temporary invincibility here
      client.collisionDetected(ghoul);
      /*mipsman.setMipsman(false);
      ghoul.setMipsman(true);
      mipsman.setDirection(Direction.UP);
      mipsman.updateImages(resourceLoader);
      ghoul.updateImages(resourceLoader);
      System.out.println("~Ghoul" + ghoul.getClientId() + " captured Mipsman" +
      mipsman.getClientId()); */
      Methods.kill(ghoul, mipsman, audioController);
    }
  }

  /**
   * Static method for updating game state increments positions if valid, increments points, and
   * detects and treats entity collisions
   *
   * @param agents array of entities in current state
   * @author Alex Banks, Matthew Jones
   * @see this#detectEntityCollision(Entity, Entity, AudioController)
   */
  void processPhysics(
      Entity[] agents,
      Map m,
      ResourceLoader resourceLoader,
      PointMap<Pellet> pellets,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps) {

    for (int i = 0; i < AGENT_COUNT; i++) {
      if (agents[i].getDirection() != Direction.STOP) {
        Point prevLocation = agents[i].getLocation();
        agents[i].move();
        Point faceLocation = agents[i].getFaceLocation();

        if (m.isWall(faceLocation)) {
          // System.out.println("~Player" + i + " drove into a wall");
          agents[i].setLocation(prevLocation.centralise());
          agents[i].setDirection(Direction.STOP);
          agents[i].setDirectionSetFlag(false);
        }
      }
      if (agents[i].isDead()) {
        agents[i].countRespawn();
        int deathCounter = agents[i].getDeathCounter();
        if (deathCounter == 20) {
          agents[i].setLocation(map.getRandomSpawnPoint(agents));
        }
      }
    }

    // separate loop for checking collision after iteration

    for (int i = 0; i < AGENT_COUNT; i++) {
      for (int j = (i + 1); j < AGENT_COUNT; j++) {

        if (agents[i].isMipsman() && !agents[j].isMipsman() && !agents[i].isInvincible()) {
          detectEntityCollision(agents[i], agents[j], audioController);
        } else if (!agents[i].isInvincible() && agents[j].isInvincible()) {
          detectEntityCollision(agents[i], agents[j], audioController);
        }
        if (agents[j].isMipsman() && !agents[i].isMipsman() && !agents[j].isInvincible()) {
          detectEntityCollision(agents[j], agents[i], audioController);
        } else if (!agents[j].isInvincible() && agents[i].isInvincible()) {
          detectEntityCollision(agents[j], agents[i], audioController);
        }
      }
    }

    pelletCollision(agents, pellets, activePowerUps, audioController);
    ArrayList<Point> replace = new ArrayList<>();
    for (Pellet p : pellets.values()) {
      p.incrementRespawn();
      if (p.replace()) {
        replace.add(p.getLocation());
      }
    }
    for (Point p : replace) {
      pellets.put(p, new Pellet(p));
      System.out.println("replaced 1");
    }
    ArrayList<UUID> toRemove = new ArrayList<>();
    for (PowerUp p : activePowerUps.values()) {
      if (p.incrementTime()) {
        toRemove.add(p.id);
      }
    }
    for (UUID id : toRemove) {
      activePowerUps.remove(id);
    }
    gameTimer--;
    if (gameTimer == 0) {
      client.finishGame();
    }
  }

  public void setTime(int t) {
    this.gameTimer = t;
  }

  public GameLoop getInputProcessor() {
    return inputProcessor;
  }

  public ConcurrentHashMap<UUID, PowerUp> getActivePowerUps() {
    return activePowerUps;
  }

  public void setClientID(int clientID) {
    this.clientID = clientID;
  }
}
