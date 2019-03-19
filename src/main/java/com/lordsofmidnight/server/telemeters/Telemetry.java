package com.lordsofmidnight.server.telemeters;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.PowerUpBox;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.GameLoop;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Parent class for DumbTelemetry and HostTelemetry
 */
public abstract class Telemetry {


  static final int AGENT_COUNT = 5;
  static final int GAME_TIME = 150 * 100; // Number of seconds *100

  public int getGameTimer() {
    return gameTimer;
  }

  protected int gameTimer = GAME_TIME;
  Map map;
  Entity[] agents;
  PointMap<Pellet> pellets;
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

  ConcurrentHashMap<UUID, PowerUp> activePowerUps = new ConcurrentHashMap<>();
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
      default: {
        for (int i = AGENT_COUNT - 1; i >= 5; i--) {
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

  /**
   * Static method for updating game state increments positions if valid, increments points, and
   * detects and treats entity collisions
   *
   * @param agents array of entities in current state
   * @author Alex Banks, Matthew Jones
   * @see this#detectEntityCollision(Entity, Entity, ResourceLoader)
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
        }
      }
      agents[i].countRespawn();
    }

    // separate loop for checking collision after iteration

    for (int i = 0; i < AGENT_COUNT; i++) {
      for (int j = (i + 1); j < AGENT_COUNT; j++) {

        if (agents[i].isMipsman() && !agents[j].isMipsman() && !agents[i].isInvincible()) {
          detectEntityCollision(agents[i], agents[j], resourceLoader);
        } else if (agents[i].isInvincible() && !agents[j].isInvincible()) {
          invincibleCollision(agents[i], agents[j], resourceLoader);
        }
        if (agents[j].isMipsman() && !agents[i].isMipsman() && !agents[j].isInvincible()) {
          detectEntityCollision(agents[j], agents[i], resourceLoader);
        } else if (agents[j].isInvincible() && !agents[i].isInvincible()) {
          invincibleCollision(agents[j], agents[i], resourceLoader);
        }
      }
    }

    pelletCollision(agents, pellets, activePowerUps);
    Random r = new Random();
    for (Point p : pellets.keySet()) {
      Pellet currentPellet = pellets.get(p);
      if(currentPellet.incrementRespawn()){
        Point point = new Point(p.getX()+0.5,p.getY()+0.5);
        Pellet pellet = r.nextInt(30) == 1 ? new PowerUpBox(point) : new Pellet(point);
        pellets.put(p,pellet);
      }
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
      client.finishGame();
    }
  }

  // physics engine

  void initialisePellets() {
    Random r = new Random();
    pellets = new PointMap<>(map);
    for (int i = 0; i < map.getMaxX(); i++) {
      for (int j = 0; j < map.getMaxY(); j++) {
        Point point = new Point(i + 0.5, j + 0.5);
        if (!map.isWall(point)) {
          Pellet pellet = r.nextInt(30) == 1 ? new PowerUpBox(point) : new Pellet(point);
          pellet.updateImages(resourceLoader);
          pellets.put(new Point(i, j), pellet);
        }
      }
    }
  }

  static void invincibleCollision(Entity killer, Entity victim, ResourceLoader r) {
    if (killer.isDead() || victim.isDead()) {
      return;
    }
    Point killerLocation = killer.getFaceLocation();
    Point victimLocation = victim.getLocation();
    if (victimLocation.inRange(killerLocation)) {
      Methods.kill(killer, victim);
      victim.setLocation(r.getMap().getRandomSpawnPoint());
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
    if (mipsman.isDead() || ghoul.isDead()) {
      return;
    }
    Point mipsmanCenter = mipsman.getLocation();
    Point ghoulFace = ghoul.getFaceLocation();

    if (mipsmanCenter.inRange(ghoulFace)) { // check temporary invincibility here
      client.collisionDetected(ghoul);
      mipsman.setMipsman(false);
      ghoul.setMipsman(true);
      Methods.kill(ghoul, mipsman);
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
  private static void pelletCollision(
      Entity[] agents, PointMap<Pellet> pellets, ConcurrentHashMap<UUID, PowerUp> activePowerUps) {
    for (Entity agent : agents) {
      Point p = agent.getLocation();
      Pellet pellet = pellets.get(p);
      if (pellet != null) {
        pellet.interact(agent, agents, activePowerUps);
      }
    }
  }

  public GameLoop getInputProcessor() {
    return inputProcessor;
  }

  public ConcurrentHashMap<UUID, PowerUp> getActivePowerUps() {
    return activePowerUps;
  }
}
