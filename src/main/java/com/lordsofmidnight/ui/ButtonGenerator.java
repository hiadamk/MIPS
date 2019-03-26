package com.lordsofmidnight.ui;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Class for  reduced repeated lines of code when creating buttons
 */
public class ButtonGenerator {

  /**
   * Creates a JFoenix button
   *
   * @param visible Whether the button is visible in its root
   * @param root Where the button is placed
   * @param text The text shown on the button
   * @param colour The colour of the text
   * @param fontsize The size of the font
   * @return The button created
   */
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

  /**
   * Creates a button with an image shown
   * @param visible Whether the button is visible in its root initially
   * @param root Where the button is shown
   * @param imageView The image shown on the button
   * @return The button returned.
   */
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
