package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.enums.EntityType;

public class Server {

  private BlockingQueue<Input> inputs;
  private Entity[] agents;

  public Server() {
    inputs = new LinkedBlockingQueue<>();
    int aiCount = 5 - makeConnections();
    if (aiCount > 0) {
      //Generate the AI to control each entity needed
    }
    agents = new Entity[5];
    agents[0] = new Entity(EntityType.PACMAN,0);
    agents[1] = new Entity(EntityType.GHOST1,1);
    agents[2] = new Entity(EntityType.GHOST2,2);
    agents[3] = new Entity(EntityType.GHOST3,3);
    agents[4] = new Entity(EntityType.GHOST4,4);
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
}
