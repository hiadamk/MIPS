package utils;

import javafx.scene.input.KeyCode;
import utils.enums.InputKey;

public class Settings {

  private static KeyCode up = KeyCode.UP;
  private static KeyCode down = KeyCode.DOWN;
  private static KeyCode left = KeyCode.LEFT;
  private static KeyCode right = KeyCode.RIGHT;
  private static KeyCode useItem = KeyCode.SPACE;

  public static KeyCode getKey(InputKey key) {
    switch (key) {
      case UP:
        return up;
      case DOWN:
        return down;
      case LEFT:
        return left;
      case RIGHT:
        return right;
      default:
        return useItem;
    }
  }

  public static void setKey(InputKey key, KeyCode keyCode) {
    switch (key) {
      case UP:
        up = keyCode;
        break;
      case DOWN:
        down = keyCode;
        break;
      case LEFT:
        left = keyCode;
        break;
      case RIGHT:
        right = keyCode;
        break;
      default:
        useItem = keyCode;
    }
    System.err.println("Invalid key to be bound");
  }
}
