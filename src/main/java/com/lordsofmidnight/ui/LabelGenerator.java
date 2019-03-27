package com.lordsofmidnight.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Class to reduce repeated linies of code for generating labels.
 */
public class LabelGenerator {

  /**
   * Generates a label
   *
   * @param visible Whether or not the label is initially visible to the root
   * @param root Where the label will be placed
   * @param text The text shown
   * @param colour The colour of the text
   * @param fontSize The size of the font
   * @return The created label
   */
  public static Label generate(boolean visible, Pane root, String text, UIColours colour,
      int fontSize) {
    Label newLabel = new Label(text);
    newLabel.setVisible(visible);
    root.getChildren().add(newLabel);
    newLabel.setStyle(" -fx-font-size:" + fontSize + "pt ; -fx-text-fill: " + colour + ";");
    return newLabel;
  }

  /**
   * Generates a label without a root.
   * @param visible Whether or not the label is initially visible to the root
   * @param text The text shown
   * @param colour The colour of the text
   * @param fontSize The size of the font
   * @return The created label
   */
  public static Label generate(boolean visible, String text, UIColours colour,
      int fontSize) {
    Label newLabel = new Label(text);
    newLabel.setVisible(visible);
    newLabel.setStyle(" -fx-font-size:" + fontSize + "pt ; -fx-text-fill: " + colour + ";");
    return newLabel;
  }
}
