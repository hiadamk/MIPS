package server.telemeters;

import ai.AILoopControl;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import main.Client;
import objects.Entity;
import server.NetworkUtility;
import utils.GameLoop;
import utils.Input;
import utils.Methods;
import utils.Point;
import utils.enums.Direction;
import utils.enums.PowerUp;

public class HostTelemetry extends Telemetry {

  private final int playerCount;
  private BlockingQueue<Input> inputs;
  private BlockingQueue<String> outputs;
  private boolean singlePlayer;
  private AILoopControl ai;
  private boolean aiRunning;
  // GameLoop inputProcessor;

  /**
   * MultiPlayer Constructor
   */
  public HostTelemetry(
      int playerCount, Queue<Input> inputQueue, Queue<String> outputQueue, Client client) {
    super(client);
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
  public HostTelemetry(Queue<Input> clientQueue, Client client) {
    super(client);
    inputs = (BlockingQueue<Input>) clientQueue;
    outputs = new LinkedBlockingQueue<>();
    this.playerCount = 1;
    singlePlayer = true;
    initialise();
    //    startGame();
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
      for (int i = 0; i < aiCount; i++) {
        aiControlled[i] = highestId;
        highestId--;
      }
      aiRunning = false;
      ai = new AILoopControl(agents, aiControlled, map, inputs, pellets);
    }
  }

  public void addInput(Input in) {
    inputs.add(in);
  }

  public void startGame() {
    updateClients(agents); // set starting positions
    startAI();

    final long DELAY = (long) Math.pow(10, 7);
    final long positionDELAY = (long) Math.pow(10, 9) / 2;
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

    scoreUpdater =
        new GameLoop(scoreDELAY) {
          @Override
          public void handle() {
            updateScores(agents);
          }
        };
    scoreUpdater.start();
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
      if (d.equals(Direction.USE)) {
        usePowerUp(id);
      } else {
        agents[id].setDirectionSetFlag(false);
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
  }

  private void usePowerUp(int id) {
    // TODO : implement
    System.out.println("POWERUP USED PLAYER: " + id);
    PowerUp item;
    if ((item = agents[id].getFirstItem()) != null) {
      item.use(agents[id], activePowerUps);
    }
    // TODO if player has powerup, do this:
    //   informPowerup(id, powerup, location);
  }

  private void informClients(Input input, Point location) {
    outputs.add(NetworkUtility.makeEntitiyMovementPacket(input, location, getMipID()));
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
  }

  private void informPowerup(int id, PowerUp powerup, Point location) {
    outputs.add(NetworkUtility.makePowerUpPacket(id, powerup, location));
  }

  private void updateScores(Entity[] agents) {
    outputs.add(NetworkUtility.makeScorePacket(agents));
  }

  private void updateClients(Entity[] agents) {
    outputs.add(NetworkUtility.makeEntitiesPositionPacket(agents) + getMipID());
  }
}
