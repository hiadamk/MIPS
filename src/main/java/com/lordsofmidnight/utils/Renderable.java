package com.lordsofmidnight.utils;

import java.util.ArrayList;
import javafx.scene.image.Image;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;

public interface Renderable {

  Point getLocation();

  ArrayList<Image> getImage();

  Direction getDirection();
}
