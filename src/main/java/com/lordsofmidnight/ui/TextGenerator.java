package com.lordsofmidnight.ui;

import javafx.scene.text.Text;

/**
 * Class for reducing repeated lines when generating Text
 */
public class TextGenerator {

  /**
   * Generates text
   *
   * @param text The text to display
   * @param colour The colour of the text
   * @param size The size of the text
   * @return The text object.
   */
  public static Text generate(String text, UIColours colour, int size){
    Text result = new Text(text);
    result.setStyle(" -fx-fill: " + colour.getHex() + ";"
        + "-fx-font-size: " + size + ";");
    return result;
  }
}
