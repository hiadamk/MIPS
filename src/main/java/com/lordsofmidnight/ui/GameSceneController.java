package com.lordsofmidnight.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.lordsofmidnight.main.Client;

public class GameSceneController {

  private StackPane root;
  private Client client;

  public GameSceneController(Canvas gameCanvas, Client client) {
    this.root = new StackPane();
    this.client = client;
    root.getChildren().add(gameCanvas);
    init();
  }

  private void init() {
    VBox quitContainer = new VBox(15);
    HBox quitBtns = new HBox(15);
    VBox.setMargin(quitBtns, new Insets(0, 0, 0, 60));

    Button quit = ButtonGenerator.generate(true, root, "Quit", UIColours.QUIT_RED, 20);
    StackPane.setAlignment(quit, Pos.TOP_LEFT);
    StackPane.setMargin(quit, new Insets(20, 0, 0, 30));
    quit.setOnAction(e -> {
      quit.setVisible(false);
      quitContainer.setVisible(true);
    });

    quit.setFocusTraversable(false);

    Button yesBtn = ButtonGenerator.generate(true, quitBtns, "Yes", UIColours.GREEN, 15);
    yesBtn.setOnAction(event -> {
      client.closeGame();
      quitContainer.setVisible(false);
      quit.setVisible(true);
    });

    yesBtn.setFocusTraversable(false);
    Button noBtn = ButtonGenerator.generate(true, quitBtns, "No", UIColours.RED, 15);
    noBtn.setOnAction(event -> {
      quit.setVisible(true);
      quitContainer.setVisible(false);
    });

    noBtn.setFocusTraversable(false);
    Label confirmLbl = LabelGenerator
        .generate(true, quitContainer, "Are you sure?", UIColours.WHITE, 15);
    quitContainer.getChildren().add(quitBtns);
    root.getChildren().add(quitContainer);
    StackPane.setAlignment(quitContainer, Pos.TOP_LEFT);
    StackPane.setMargin(quitContainer, new Insets(30, 0, 0, 50));
    quitContainer.setVisible(false);
    root.setLayoutY(20);
    root.requestFocus();
  }

  public StackPane getGameRoot() {
    return root;
  }


}
