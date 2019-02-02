package utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javafx.scene.image.Image;
import utils.enums.Direction;

public interface Renderable {

  Point2D.Double getLocation();

  ArrayList<Image> getImage();

  Direction getDirection();
}
