package com.lordsofmidnight.renderer;

import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.enums.Awards;
import com.lordsofmidnight.utils.enums.Direction;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.Settings;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class EndGameScreen {

  private final Image background;
  private final int TARGET_FALL_FRAMES = 120;
  private final ResourceLoader resourceLoader;
  private Font geoVerySmall = null;
  private Font geoLarge = null;
  private Font geoSmall = null;
  private GraphicsContext gc;
  private int xResolution;
  private int yResolution;
  private final long secondInNanoseconds = (long) Math.pow(10, 9);
  private Entity gameWinner;
  private Entity awardWinner1;
  private Entity awardWinner2;
  private Double winnerLocation;
  private Double award1Location;
  private Double award2Location;
  private Double winnerSize;
  private Double awardSize;
  private AnimationTimer fallingAnimation;
  private ArrayList<Image> gameWinnerImages;
  private ArrayList<Image> awardWinner1Images;
  private ArrayList<Image> awardWinner2Images;
  private AnimationTimer awardScreen;
  private int animationFrame;

  public EndGameScreen(GraphicsContext gc, ResourceLoader r) {
    this.gc = gc;
    this.xResolution = Settings.getxResolution();
    this.yResolution = Settings.getyResolution();
    this.background = r.getBackground();
    this.resourceLoader = r;

    final double fontRatio = 0.1;
    try {
      this.geoLarge =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              xResolution * fontRatio);
      this.geoSmall =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              0.4 * xResolution * fontRatio);
      this.geoVerySmall =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              0.25 * xResolution * fontRatio);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void showEndSequence(Entity[] entities) {
    this.animationFrame = 0;
    ArrayList<Entity> entityArr = new ArrayList<>(Arrays.asList(entities));
    Awards[] awards = Awards.getTwoRandomAwards();
    this.gameWinner =
        Collections.max(
            entityArr,
            Comparator.comparingInt(o -> o.getStatsTracker().getStat(Awards.MOST_POINTS)));

    this.awardWinner1 =
        Collections.max(
            entityArr, Comparator.comparingInt(o -> o.getStatsTracker().getStat(awards[0])));

    this.awardWinner2 =
        Collections.max(
            entityArr, Comparator.comparingInt(o -> o.getStatsTracker().getStat(awards[1])));

    this.gameWinnerImages =
        resourceLoader.getEndScreenMip(gameWinner.getClientId(), gameWinner.isMipsman());
    this.awardWinner1Images =
        resourceLoader.getEndScreenMip(awardWinner1.getClientId(), awardWinner1.isMipsman());
    this.awardWinner2Images =
        resourceLoader.getEndScreenMip(awardWinner2.getClientId(), awardWinner2.isMipsman());

    this.winnerSize = getSpriteSize(0.4, gameWinner);
    this.awardSize = getSpriteSize(0.3, awardWinner1);

    this.winnerLocation =
        new Point2D.Double(xResolution / 2 - (winnerSize.getX() / 2), yResolution * 0.3);
    this.award1Location =
        new Point2D.Double((xResolution * 0.15) - (awardSize.getX() / 2), yResolution * 0.45);
    this.award2Location =
        new Point2D.Double((xResolution * 0.85) - (awardSize.getX() / 2), yResolution * 0.45);

    this.fallingAnimation =
        new AnimationTimer() {
          int currentFrame = 0;

          @Override
          public void handle(long now) {
            if (currentFrame >= TARGET_FALL_FRAMES) {
              this.stop();
              showAwards(awards);
            }
            if (currentFrame % 12 == 1) {
              gameWinner.nextFrame();
              awardWinner1.nextFrame();
              awardWinner2.nextFrame();
            }
            renderFallFrame(currentFrame, true);
            currentFrame += 1;
          }
        };

    new AnimationTimer() {
      double opacity = 0;
      double opacityIncrement = 0.001;
      long gameOverTime = (long) (3 * Math.pow(10, 9));
      int currentFrame = 0;

      @Override
      public void handle(long now) {
        if (TARGET_FALL_FRAMES <= currentFrame) {
          gc.clearRect(0, 0, xResolution, yResolution);
          this.stop();
          fallingAnimation.start();
        }
        currentFrame++;
        gc.setFill(new Color(1, 1, 1, 0.5));
        if (opacity + opacityIncrement < 1) {
          opacity += opacityIncrement;
        }
        gc.setFill(new Color(0, 0, 0, opacity));
        gc.fillRect(0, 0, xResolution, yResolution);
        gc.setFont(geoLarge);
        gc.setFill(Color.RED);
        gc.fillText("GAME OVER", xResolution / 2, yResolution / 2);
        gc.setStroke(Color.WHITE);
        gc.strokeText("GAME OVER", xResolution / 2, yResolution / 2);
      }
    }.start();
  }

  private void renderFallFrame(int currentFrame, boolean falling) {
    if (currentFrame % 8 == 0) {
      animationFrame++;
    }
    double y;

    gc.drawImage(background, 0, 0, xResolution, yResolution);

    y =
        (falling)
            ? winnerLocation.getY() * ((double) currentFrame / TARGET_FALL_FRAMES)
            : winnerLocation.getY();
    gc.drawImage(
        gameWinnerImages.get(animationFrame % gameWinnerImages.size()),
        winnerLocation.getX(),
        y,
        winnerSize.getX(),
        winnerSize.getY());

    y =
        (falling)
            ? award1Location.getY() * ((double) currentFrame / TARGET_FALL_FRAMES)
            : award1Location.getY();
    gc.drawImage(
        awardWinner1Images.get(animationFrame % awardWinner1Images.size()),
        award1Location.getX(),
        y,
        awardSize.getX(),
        awardSize.getY());

    y =
        (falling)
            ? award2Location.getY() * ((double) currentFrame / TARGET_FALL_FRAMES)
            : award2Location.getY();
    gc.drawImage(
        awardWinner2Images.get(animationFrame % awardWinner2Images.size()),
        award2Location.getX(),
        y,
        awardSize.getX(),
        awardSize.getY());
  }

  private void showAwards(Awards[] awards) {

    this.awardScreen =
        new AnimationTimer() {
          private int currentFrame = 0;

          @Override
          public void handle(long now) {
            currentFrame++;
            gc.drawImage(background, 0, 0, xResolution, yResolution);
            renderFallFrame(currentFrame, false);
            gc.setFill(Color.WHITE);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(geoLarge);
            gc.fillText(gameWinner.getName() + " WON!", xResolution / 2, 0.2 * yResolution);
            gc.setFont(geoSmall);

            double winnerY = winnerLocation.getY() + winnerSize.getY();
            double awardY = award1Location.getY() + awardSize.getY();

            gc.fillText(gameWinner.getName(), xResolution / 2, winnerY);
            gc.fillText(
                awardWinner1.getName(), award1Location.getX() + awardSize.getX() / 2, awardY);
            gc.fillText(
                awardWinner2.getName(), award2Location.getX() + awardSize.getX() / 2, awardY);

            gc.fillText(
                Awards.MOST_POINTS.getName(),
                winnerLocation.getX() + winnerSize.getX() / 2,
                winnerY + 0.1 * yResolution);
            gc.fillText(
                awards[0].getName(),
                award1Location.getX() + awardSize.getX() / 2,
                awardY + 0.1 * yResolution);
            gc.fillText(
                awards[1].getName(),
                award2Location.getX() + awardSize.getX() / 2,
                awardY + 0.1 * yResolution);

            gc.setFont(geoVerySmall);
            gc.fillText(
                awards[1].getMessage(awardWinner2.getStatsTracker().getStat(awards[1])),
                award2Location.getX() + awardSize.getX() / 2,
                winnerY + 0.2 * yResolution);
            gc.fillText(
                Awards.MOST_POINTS.getMessage(gameWinner.getScore()),
                winnerLocation.getX() + winnerSize.getX() / 2,
                winnerY + 0.2 * yResolution);
            gc.fillText(
                awards[0].getMessage(awardWinner1.getStatsTracker().getStat(awards[0])),
                award1Location.getX() + awardSize.getX() / 2,
                winnerY + 0.2 * yResolution);
          }
        };
    awardScreen.start();
  }

  public void StopEndScreen() {
    if (this.awardScreen != null) {
      this.awardScreen.stop();
    }
  }

  private Point2D.Double getSpriteSize(double ratio, Entity e) {
    int y = (int) (ratio * yResolution);
    int x =
        (int)
            (((ratio * yResolution) / e.getImage().get(0).getHeight())
                * e.getImage().get(0).getWidth());
    return new Point2D.Double(x, y);
  }
}
