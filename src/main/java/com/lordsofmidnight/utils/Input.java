package com.lordsofmidnight.utils;

import com.lordsofmidnight.utils.enums.Direction;

/**
 * A class to hold the inputs being sent from the client to the telemetry
 */
public class Input {

  private int clientID;
  private Direction move;
  private Boolean useItem;

  /**
   * Creates a new input
   *
   * @param id The client id of the player sending the input
   * @param move The Direction of the input
   */
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

  /**
   * Creates an input from a string
   *
   * @param s The string to create it from
   * @return the new input object
   */
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

  /**
   * @return the id of the client that sent the input
   */
  public int getClientID() {
    return clientID;
  }

  /** @return the Direction of the inputs move */
  public Direction getMove() {
    return move;
  }

  /**
   * Converts the Input into a string
   *
   * @return The string form of the input
   */
  @Override
  public String toString() {
    if (useItem) {
      return clientID + ":use";
    }
    return "" + clientID + ":" + move.toInt();
  }
}
