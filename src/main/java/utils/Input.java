package utils;

import utils.enums.Direction;

public class Input {

  private int clientID;
  private Direction move;

  public Input(int id, Direction move) {
    this.clientID = id;
    this.move = move;
  }

  public int getClientID() {
    return clientID;
  }

  public Direction getMove() {
    return move;
  }
}
