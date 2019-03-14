package com.lordsofmidnight.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class LabelGenerator {

  public static Label generate(boolean visible, Pane root, String text, UIColours colour,
      int fontSize) {
    Label newLabel = new Label(text);
    newLabel.setVisible(visible);
    root.getChildren().add(newLabel);
    newLabel.setStyle(" -fx-font-size:" + fontSize + "pt ; -fx-text-fill: " + colour + ";");
    return newLabel;
  }
}
