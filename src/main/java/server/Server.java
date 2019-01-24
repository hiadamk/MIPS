package server;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.enums.Direction;
import utils.enums.EntityType;

public class Server {

  private BlockingQueue<Input> inputs;
  private Entity[] agents;
  private final Point2D.Double respawnPoint = new Double(5, 5); //Need to set respawn point somehow

  public Server() {
    inputs = new LinkedBlockingQueue<>();
    int aiCount = 5 - makeConnections();
    if (aiCount > 0) {
      //Generate the AI to control each entity needed
    }
    agents = new Entity[5];
    agents[0] = new Entity(EntityType.PACMAN);
    agents[1] = new Entity(EntityType.GHOST1);
    agents[2] = new Entity(EntityType.GHOST2);
    agents[3] = new Entity(EntityType.GHOST3);
    agents[4] = new Entity(EntityType.GHOST4);
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
    if (x.getType() == EntityType.PACMAN) {
      x.setType(y.getType());
      y.setType(EntityType.PACMAN);
      x.setLocation(respawnPoint);
      x.setDirection(Direction.UP);
    } else if (y.getType() == EntityType.PACMAN) {
      y.setType(x.getType());
      x.setType(y.getType());
      y.setLocation(respawnPoint);
      y.setDirection(Direction.UP);
    }
  }
}
