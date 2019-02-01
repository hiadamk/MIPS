package main;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import utils.Settings;
import utils.enums.Direction;

public class KeyController implements EventHandler<KeyEvent> {

  private Direction mapping;
  private Direction activeKey;

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
    if (e.getCode() == Settings.getKey(Direction.UP)) {
      activeKey = Direction.UP;
    } else if (e.getCode() == Settings.getKey(Direction.DOWN)) {
      activeKey = Direction.DOWN;
    } else if (e.getCode() == Settings.getKey(Direction.LEFT)) {
      activeKey = Direction.LEFT;
    } else if (e.getCode() == Settings.getKey(Direction.RIGHT)) {
      activeKey = Direction.RIGHT;
    }
  }
}
