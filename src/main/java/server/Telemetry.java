package server;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.enums.Direction;

public class Telemetry {

  private BlockingQueue<Input> inputs;
  private Entity[] agents;
  private final Point2D.Double respawnPoint = new Double(5, 5); //Need to set respawn point somehow

  public Telemetry() {
    inputs = new LinkedBlockingQueue<>();
    int aiCount = 5 - makeConnections();
    if (aiCount > 0) {
      //Generate the AI to control each entity needed
    }
    agents = new Entity[5];

    agents[0] = new Entity(true, 0);
    agents[1] = new Entity(false, 1);
    agents[2] = new Entity(false, 2);
    agents[3] = new Entity(false, 3);
    agents[4] = new Entity(false, 4);
    startGame();
  }

  /**
   * Makes the connections to the other clients
   * @return the number of human players in the game
   */
  private int makeConnections() {
    int count = 1;
    // TODO implement
    return  count;
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
        processPhysics();
        updateClients();
      }
    }.start();
  }

  private void processInputs() {
    while (!inputs.isEmpty()) {
      Input input = inputs.poll();
      //Validate the input
      agents[input.getClientID()].setDirection(input.getMove());
    }
  }

  private void informClients() {
    // TODO implement
  }

  private void processPhysics() {
    // TODO implement
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
