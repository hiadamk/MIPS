package utils;

import java.util.ArrayList;

import javafx.scene.image.Image;
import utils.enums.Direction;

public interface Renderable {
  
  Point getLocation();
  
  ArrayList<Image> getImage();
  
  Direction getDirection();
}
