package com.lordsofmidnight.server.telemeters;

import com.lordsofmidnight.ai.AILoopControl;
import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.PowerUpBox;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.server.NetworkUtility;
import com.lordsofmidnight.utils.GameLoop;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HostTelemetry extends Telemetry {

  private final int playerCount;
  private BlockingQueue<Input> inputs;
  private BlockingQueue<String> outputs;
  private boolean singlePlayer;
  private AILoopControl ai;
  private boolean aiRunning;
  private GameLoop inventoryUpdater;

  /**
   * MultiPlayer Constructor
   */
  public HostTelemetry(
      int playerCount, Queue<Input> inputQueue, Queue<String> outputQueue, Client client,
      AudioController audioController) {
    super(client, audioController);
    inputs = (BlockingQueue<Input>) inputQueue;
    outputs = (BlockingQueue<String>) outputQueue;
    this.playerCount = playerCount;
    this.singlePlayer = false;
    initialise();
    //    startGame();
  }

  /**
   * Single Player Constructor
   */
  public HostTelemetry(Queue<Input> clientQueue, Client client, AudioController audioController) {
    super(client, audioController);
    inputs = (BlockingQueue<Input>) clientQueue;
    outputs = new LinkedBlockingQueue<>();
    this.playerCount = 1;
    singlePlayer = true;
    initialise();
  }

  /**
   * Initialises the game agents/entities and AI to control them
   *
   * @author Matthew Jones
   */
  private void initialise() {

    initialiseEntities();

    if (singlePlayer) {
      agents[(new Random()).nextInt(AGENT_COUNT)].setMipsman(true);
    }

    initialisePellets();

    int aiCount = AGENT_COUNT - playerCount;
    if (aiCount > 0) {
      int[] aiControlled = new int[aiCount];
      int highestId = AGENT_COUNT - 1;
//      String[] names = Methods.getRandomNames(aiCount);
      for (int i = 0; i < aiCount; i++) {
        aiControlled[i] = highestId;
//        agents[highestId].setName(names[i]);
        highestId--;
      }
      aiRunning = false;
      ai = new AILoopControl(agents, aiControlled, map, inputs, pellets);
    }
  }

  /**
   * Adds an input to the input queue
   *
   * @param in The input to add
   */
  public void addInput(Input in) {
    inputs.add(in);
  }

  /**
   * Handles starting the game for the host
   */
  public void startGame() {
    updateClients(agents); // set starting positions

    initialisePellets();
    startAI();
    audioController.gameIntro();
    gameTimer = GAME_TIME;
    final long DELAY = (long) Math.pow(10, 7);
    final long positionDELAY = (long) Math.pow(10, 8);
    final long scoreDELAY = (long) Math.pow(10, 9);
    inputProcessor =
        new GameLoop(DELAY) {
          @Override
          public void handle() {
            processInputs();
            processPhysics(agents, map, resourceLoader, pellets, activePowerUps);
          }
        };
    inputProcessor.start();

    positionUpdater =
        new GameLoop(positionDELAY) {
          @Override
          public void handle() {
            updateClients(agents);
          }
        };
    positionUpdater.start();

    inventoryUpdater =
        new GameLoop(positionDELAY) {
          @Override
          public void handle() {
            updateInventories(agents);
          }
        };
    inventoryUpdater.start();

    scoreUpdater =
        new GameLoop(scoreDELAY) {
          @Override
          public void handle() {
            updateScores(agents);
          }
        };
    scoreUpdater.start();
  }

  /**
   * Starts the AI-controlled agents
   */
  public void startAI() {
    if (!aiRunning && ai != null) {
      ai.start();
    }
  }

  /**
   * Method to deal with the inputs provided in the inputs queue
   *
   * @author Matthew Jones
   */
  void processInputs() {
    while (!inputs.isEmpty()) {
      Input input = inputs.poll();
      int id = input.getClientID();
      Direction d = input.getMove();
      if (d.equals(Direction.USE)) {
        if (agents[id].isDead()) {
          agents[id].setPowerUpUsedFlag(false);
          return;
        }
        usePowerUp(id);
        agents[id].setPowerUpUsedFlag(false);
      }else if(d.equals(Direction.STOP)){
        ai.addClient(id);
      }
      else {
        if (Methods.validateDirection(d, agents[id].getLocation(), map)) {
          agents[id].setDirection(d);
          if (!singlePlayer) {
            // this is currently what's set to update on other clients' systems. they'll get valid
            // inputs
            informClients(input, agents[id].getLocation()); // Inputs sent to the other clients
          }
        }
      }
      agents[id].setDirectionSetFlag(false);
    }
  }


  @Override
  void initialisePellets() {
    Pellet pellet;
    Random r = new Random();
    pellets = new PointMap<>(map);
    for (int i = 0; i < map.getMaxX(); i++) {
      for (int j = 0; j < map.getMaxY(); j++) {
        Point point = new Point(i + 0.5, j + 0.5);
        if (!map.isWall(point)) {
          if (r.nextInt(30) == 1)  {
            pellet =  new PowerUpBox(point);
            informPowerupBox(point);
          }
          else{
            pellet = new Pellet(point);
          }
          pellet.updateImages(resourceLoader);
          pellets.put(new Point(i, j), pellet);
        }
      }
    }
  }


  private void usePowerUp(int id) {
    PowerUp item;
    if ((item = agents[id].getFirstItem()) != null) {
      item.use(agents[id], activePowerUps, pellets, agents);
      informPowerup(id, item, agents[id].getLocation());
    }
  }

  private int getMipID() {
    for (Entity e : agents) {
      if (e.isMipsman()) {
        return e.getClientId();
      }
    }
    return 0;
  }

  @Override
  public void stopGame() {
    outputs.add(NetworkUtility.STOP_CODE);
    inputProcessor.close();
    positionUpdater.close();
    scoreUpdater.close();
    inventoryUpdater.close();
    ai.killAI();
  }

  /**
   * Informs clients that a powerup was used
   * @param id The ID of the client who used the powerup
   * @param powerup The powerup used
   * @param location The location it was used at
   */
  private void informPowerup(int id, PowerUp powerup, Point location) {
    outputs.add(NetworkUtility.makePowerUpPacket(id, powerup, location));
  }

  /**
   * Informs clients of a power up box appearing
   * @param point The point where it is.
   */
  private void informPowerupBox(Point point) {
    outputs.add(NetworkUtility.makePowerUpBoxPacket(point));
  }

  /**
   * Informs clients of the updated inventory of each of the agents.
   * @param agents The game agents
   */
  private void updateInventories(Entity[] agents){
    outputs.add(NetworkUtility.makeInventoryPacket(agents));
  }

  /**
   * Informs clients of the scores of each of the agents
   * @param agents The game agents
   */
  private void updateScores(Entity[] agents) {
    outputs.add(NetworkUtility.makeScorePacket(agents));
  }

  /**
   * Informs clients of where each agent is and their direction
   * @param agents The game agents
   */
  private void updateClients(Entity[] agents) {
    outputs.add(NetworkUtility.makeEntitiesPositionPacket(agents) + getMipID() + "|" + gameTimer);
  }

  /**
   * Inform clients that a client changed direction
   * @param input The input to broadcast
   * @param location The location which the input took place.
   */
  private void informClients(Input input, Point location) {
    outputs.add(NetworkUtility.makeEntitiyMovementPacket(input, location, getMipID()));
  }

}
