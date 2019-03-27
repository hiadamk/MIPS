package com.lordsofmidnight.utils;

import com.lordsofmidnight.utils.enums.Direction;

public class Input {

  private int clientID;
  private Direction move;
  private Boolean useItem;

  public Input(int id, Direction move) {
    this.clientID = id;
    this.move = move;
    this.useItem = false;
  }

  /**
   * second type of input for player to use item
   *
   * @param id the clientID of the sender
   */
  public Input(int id) {
    this.clientID = id;
    this.useItem = true;
  }

  public static Input fromString(String s) {
    if (s.length() == 1) {
      return new Input(Integer.parseInt(s));
    }
    int id = Integer.parseInt(s.split(":")[0]);
    int directionInt = Integer.parseInt(s.split(":")[1]);
    return new Input(id, Direction.fromInt(directionInt));
  }

  public boolean isItemUsage() {
    return useItem;
  }

  public int getClientID() {
    return clientID;
  }

  public Direction getMove() {
    return move;
  }

  @Override
  public String toString() {
    if (useItem) {
      return clientID + ":use";
    }
    return "" + clientID + ":" + move.toInt();
  }
}
