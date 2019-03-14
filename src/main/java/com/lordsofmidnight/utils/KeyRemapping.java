package com.lordsofmidnight.utils;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import com.lordsofmidnight.utils.enums.InputKey;

public class KeyRemapping implements EventHandler<KeyEvent> {

  private KeyCode activeKey;

  public KeyRemapping() {
    activeKey = null;
  }

  public KeyCode getActiveKey() {
    return activeKey;
  }

  @Override
  public void handle(KeyEvent e) {
    this.activeKey = e.getCode();
  }

  public void reset() {
    this.activeKey = null;
  }

  public boolean checkForDublicates(KeyCode k) {
    boolean use = Settings.getKey(InputKey.USE).equals(k);
    boolean up = Settings.getKey(InputKey.UP).equals(k);
    boolean left = Settings.getKey(InputKey.LEFT).equals(k);
    boolean right = Settings.getKey(InputKey.RIGHT).equals(k);
    boolean down = Settings.getKey(InputKey.DOWN).equals(k);

    return use | up | left | right | down;
  }

}
