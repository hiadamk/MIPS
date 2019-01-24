package utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javafx.scene.image.Image;
import utils.enums.Direction;

public interface Renderable {
    public Point2D.Double getLocation();
    public ArrayList<Image> getImage();
    public Direction getDirection();
}
