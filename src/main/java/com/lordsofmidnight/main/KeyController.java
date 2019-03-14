package com.lordsofmidnight.main;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import com.lordsofmidnight.utils.Settings;
import com.lordsofmidnight.utils.enums.Direction;
import com.lordsofmidnight.utils.enums.InputKey;

/**
 * Handles the key input from the keyboard
 *
 * @author Matthew Jones
 */
public class KeyController implements EventHandler<KeyEvent> {

  private Direction activeKey;
  private boolean useItem;

  public KeyController() {
    activeKey = null;
  }

  public Direction getActiveKey() {
    return activeKey;
  }

  @Override
  public void handle(KeyEvent e) {
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
