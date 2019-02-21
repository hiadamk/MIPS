package ui;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ButtonGenerator {

  public Button generate(boolean visible, Pane root, ImageView imageView) {
    Button result = new Button();
    result.setStyle("-fx-background-color: transparent;");
    result.setGraphic(imageView);
    root.getChildren().add(result);
    result.setVisible(visible);
    imageView.setPreserveRatio(true);
    return result;

  }

}
