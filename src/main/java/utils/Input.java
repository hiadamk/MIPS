package utils;

import utils.enums.Direction;

public class Input {

  private int clientID;
  private Direction move;

  public Input(int id, Direction move) {
    this.clientID = id;
    this.move = move;
  }

  public static Input fromString(String s) {
    int id = Integer.parseInt(s.split(":")[0]);
    int directionInt = Integer.parseInt(s.split(":")[1]);
    return new Input(id, Direction.fromInt(directionInt));
  }

  public int getClientID() {
    return clientID;
  }

  public Direction getMove() {
    return move;
  }

  @Override
  public String toString() {
    return "" + clientID + ":" + move.toInt();
  }
}
