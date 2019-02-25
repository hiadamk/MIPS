package main;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import utils.Settings;
import utils.enums.Direction;
import utils.enums.InputKey;

/**
 * Handles the key input from the keyboard
 *
 * @author Matthew Jones
 */
public class KeyController implements EventHandler<KeyEvent> {

  private InputKey mapping;
  private Direction activeKey;
  private boolean useItem;
  public KeyController() {
    mapping = null;
    activeKey = null;
  }

  public Direction getActiveKey() {
    return activeKey;
  }

  @Override
  public void handle(KeyEvent e) {
    if (mapping != null) {
      Settings.setKey(mapping, e.getCode());
      mapping = null;
      return;
    }
    if (e.getCode() == Settings.getKey(InputKey.UP)) {
      activeKey = Direction.UP;
    } else if (e.getCode() == Settings.getKey(InputKey.DOWN)) {
      activeKey = Direction.DOWN;
    } else if (e.getCode() == Settings.getKey(InputKey.LEFT)) {
      activeKey = Direction.LEFT;
    } else if (e.getCode() == Settings.getKey(InputKey.RIGHT)) {
      activeKey = Direction.RIGHT;
    } else if (e.getCode() == Settings.getKey(InputKey.USE)) {
      useItem = true;
    }
  }

  public boolean UseItem() {
    if (useItem) {
      useItem = false;
      return true;
    }
    return false;
  }


}
