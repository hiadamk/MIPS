package com.lordsofmidnight.ui;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ButtonGenerator {

  public static JFXButton generate(boolean visible, Pane root, String text, UIColours colour,
      int fontsize) {
    JFXButton result = new JFXButton();
    result.setText(text);
    result.setStyle(
        "-jfx-button-type: FLAT;\n"
            + "     -fx-background-color: transparent;\n"
            + "     -fx-text-fill: " + colour.getHex() + ";"
            + "-fx-font-size: " + fontsize + ";");
    root.getChildren().add(result);
    result.setVisible(visible);
    return result;

  }

  public static Button generate(boolean visible, Pane root, ImageView imageView) {
    Button result = new Button();
    result.setGraphic(imageView);
    root.getChildren().add(result);
    result.setStyle("-fx-background-color: transparent;");
    result.setVisible(visible);
    imageView.setPreserveRatio(true);
    return result;

  }

}
