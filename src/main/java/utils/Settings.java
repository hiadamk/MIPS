package utils;

import javafx.scene.input.KeyCode;
import utils.enums.Direction;

public class Settings {

  private static KeyCode up = KeyCode.UP;
  private static KeyCode down = KeyCode.DOWN;
  private static KeyCode left = KeyCode.LEFT;
  private static KeyCode right = KeyCode.RIGHT;

  public static KeyCode getKey(Direction c) {
    switch (c) {
      case UP:
        return up;
      case DOWN:
        return down;
      case LEFT:
        return left;
      case RIGHT:
        return right;
      default:
        System.err.println("None bound key requested");
        return null;
    }
  }

  public static void setKey(Direction c, KeyCode key) {
    switch (c) {
      case UP:
        up = key;
        break;
      case DOWN:
        down = key;
        break;
      case LEFT:
        left = key;
        break;
      case RIGHT:
        right = key;
        break;
    }
    System.err.println("Invalid key to be bound");
  }
}
