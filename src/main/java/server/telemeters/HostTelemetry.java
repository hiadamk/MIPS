package server.telemeters;

import ai.AILoopControl;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import objects.Entity;
import server.NetworkUtility;
import utils.GameLoop;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.Direction;

public class HostTelemetry extends Telemetry {

  private final int playerCount;
  private BlockingQueue<Input> inputs;
  private BlockingQueue<String> outputs;
  private boolean singlePlayer;
  private AILoopControl ai;
  private boolean aiRunning;
  private GameLoop inputProcessor;

  public HostTelemetry(
      Map map,
      int playerCount,
      Queue<Input> inputQueue,
      Queue<String> outputQueue,
      ResourceLoader resourceLoader) {
    this.map = map;
    inputs = (BlockingQueue<Input>) inputQueue;
    outputs = (BlockingQueue<String>) outputQueue;
    this.playerCount = playerCount;
    this.resourceLoader = resourceLoader;
    this.singlePlayer = false;
    initialise();
    startGame();
  }

  public HostTelemetry(Map map, Queue<Input> clientQueue, ResourceLoader resourceLoader) {
    this.map = map;
    inputs = (BlockingQueue<Input>) clientQueue;
    outputs = new LinkedBlockingQueue<>();
    this.playerCount = 1;
    singlePlayer = true;
    this.resourceLoader = resourceLoader;
    initialise();
    startGame();
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

    int aiCount = AGENT_COUNT - playerCount;
    if (aiCount > 0) {
      int[] aiControlled = new int[aiCount];
      int highestId = AGENT_COUNT - 1;
      for (int i = 0; i < aiCount; i++) {
        aiControlled[i] = highestId;
        highestId--;
      }
      aiRunning = false;
      ai = new AILoopControl(agents, aiControlled, map, inputs);
    }

    initialisePellets();
  }

  public void addInput(Input in) {
    inputs.add(in);
  }

  void startGame() {
    startAI();

    final long DELAY = (long) Math.pow(10, 7);
    final long positionDELAY = (long) Math.pow(10, 8);

    inputProcessor = new GameLoop(DELAY) {
      @Override
      public void handle() {
        processInputs();

        processPhysics(agents, map, resourceLoader, pellets, activePowerUps);
      }
    };
    inputProcessor.start();

    new GameLoop(positionDELAY) {
      @Override
      public void handle() {
        updateClients(agents);
      }
    }; // .start();
  }

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
      if (Methods.validateDirection(d, agents[id].getLocation(), map)) {
        agents[id].setDirection(d);
        if (!singlePlayer) {
          // this is currently what's set to update on other clients' systems. they'll get valid
          // inputs
          informClients(input, agents[id].getLocation()); // Inputs sent to the other clients
        }
      }
    }
  }

  private void informClients(Input input, Point location) {
    outputs.add(NetworkUtility.makeEntitiyMovementPacket(input, location));
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
  }

  private void updateClients(Entity[] agents) {
    outputs.add(NetworkUtility.makeEntitiesPositionPacket(agents) + Integer.toString(getMipID()));
  }
}
