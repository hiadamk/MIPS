package com.lordsofmidnight.renderer;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.Settings;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EndGameScreenTest extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Group root = new Group();
    Scene s = new Scene(root, Settings.getxResolution(), Settings.getyResolution(), Color.BLACK);

    Canvas canvas = new Canvas(Settings.getxResolution(), Settings.getyResolution());
    GraphicsContext gc = canvas.getGraphicsContext2D();

    ResourceLoader r = new ResourceLoader("src/test/resources/");
    EndGameScreen end = new EndGameScreen(gc,r.getBackground());

    Entity[] entities = new Entity[3];

    entities[0] = new Entity(true,0,new Point(0,0));
    entities[1] = new Entity(false,1,new Point(0,0));
    entities[2] = new Entity(false,2,new Point(0,0));

    Methods.updateImages(entities,r);

    root.getChildren().add(canvas);

    primaryStage.setScene(s);
    primaryStage.show();

    end.showEndSequence(entities);
  }
}
