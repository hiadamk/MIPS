package utils;

import javafx.scene.image.Image;
import utils.enums.Direction;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public interface Renderable {
    
    public Point2D.Double getLocation();
    
    public ArrayList<Image> getImage();
    
    public Direction getDirection();
}
