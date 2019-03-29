package com.lordsofmidnight.utils;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.ArrayList;
import javafx.scene.image.Image;

/**
 * Interface for all renderable objects
 */
public interface Renderable {

  /**
   * @return The location of the object
   */
  Point getLocation();

  /** @return The images of the object */
  ArrayList<Image> getImage();

  /** @return The direction the object is facing */
  Direction getDirection();
}
