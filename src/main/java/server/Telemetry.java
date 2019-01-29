package server;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.ResourceLoader;
import utils.Map;
import utils.enums.Direction;

public class Telemetry {

  private final Point2D.Double respawnPoint = new Double(5, 5); // Need to set respawn point somehow
  private BlockingQueue<Input> inputs;
  private Entity[] agents;
  private static final int AGENT_COUNT = 5;

  public Telemetry() {
    inputs = new LinkedBlockingQueue<>();
    int aiCount = AGENT_COUNT - makeConnections();
    if (aiCount > 0) {
      // Generate the AI to control each entity needed
    }
    agents = new Entity[AGENT_COUNT];

    agents[0] = new Entity(true, 0);
    agents[1] = new Entity(false, 1);
    agents[2] = new Entity(false, 2);
    agents[3] = new Entity(false, 3);
    agents[4] = new Entity(false, 4);
    startGame();
  }

  /**
   * Makes the connections to the other clients
   *
   * @return the number of human players in the game
   */
  private int makeConnections() {
    int count = 1;
    // TODO implement
    return count;
  }

  public Entity getEntity(int id) {
    return agents[id];
  }

  public void addInput(Input in) {
    inputs.add(in);
  }

  public void startGame() {
    // TODO implement
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        processInputs();
        informClients();
        agents = processPhysics(agents);
        updateClients();
      }
    }.start();
  }

  private void processInputs() {
    while (!inputs.isEmpty()) {
      Input input = inputs.poll();
      // Validate the input
      agents[input.getClientID()].setDirection(input.getMove());
    }
  }

  private void informClients() {
    // TODO implement
  }

  /**
   * Static Method for updating game state
   *
   * @param agents array of entities in current state
   * @return array of entities in new state
   * @author Alex Banks
   * @
   */
  private static Entity[] processPhysics(Entity[] agents) {
    Map m = (new ResourceLoader(System.getProperty("user.dir"))).getMap();


    for (int i = 0; i < AGENT_COUNT; i++) {
      Point2D.Double tempLocation = agents[i].getLocation();
      double offset = agents[i].getVelocity();

      switch (agents[i].getDirection()) {
        case UP:
          tempLocation.setLocation(tempLocation.getX() + offset, tempLocation.getY());
          break;
        case DOWN:
          tempLocation.setLocation(tempLocation.getX() - offset, tempLocation.getY());
          break;
        case RIGHT:
          tempLocation.setLocation(tempLocation.getX(), tempLocation.getY() + offset);
          break;
        case LEFT:
          tempLocation.setLocation(tempLocation.getX(), tempLocation.getY() - offset);
          break;
      }

      //TODO check out of bounds

    }

    return agents;
  }

  private void updateClients() {
    // TODO implement
  }

  private void entityCollision(Entity x, Entity y) {
    if (x.isPacman()) {
      x.setPacMan(false);
      y.setPacMan(true);
      x.setLocation(respawnPoint);
      x.setDirection(Direction.UP);
    } else if (y.isPacman()) {
      y.setPacMan(false);
      x.setPacMan(true);
      y.setLocation(respawnPoint);
      y.setDirection(Direction.UP);
    }
  }
}
