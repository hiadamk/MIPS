package com.lordsofmidnight.utils;

import com.lordsofmidnight.utils.enums.InputKey;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handels the remapping of keys
 */
public class KeyRemapping implements EventHandler<KeyEvent> {

  private KeyCode activeKey = null;

  /**
   * @return The key pressed most recently
   */
  public KeyCode getActiveKey() {
    return activeKey;
  }

  /**
   * Processes a KeyEvent
   *
   * @param e the event
   */
  @Override
  public void handle(KeyEvent e) {
    this.activeKey = e.getCode();
  }

  /** Resets the activeKey to null */
  public void reset() {
    this.activeKey = null;
  }

  /**
   * Ensures that the key to be mapped is not already mapped
   *
   * @param k the key to map
   * @return True if the key is already mapped
   */
  public boolean checkForDublicates(KeyCode k) {
    boolean use = Settings.getKey(InputKey.USE).equals(k);
    boolean up = Settings.getKey(InputKey.UP).equals(k);
    boolean left = Settings.getKey(InputKey.LEFT).equals(k);
    boolean right = Settings.getKey(InputKey.RIGHT).equals(k);
    boolean down = Settings.getKey(InputKey.DOWN).equals(k);

    return use | up | left | right | down;
  }
}
