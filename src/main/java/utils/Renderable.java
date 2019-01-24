package utils;
import java.awt.geom.Point2D;
import utils.enums.Direction;
import javafx.scene.image.Image;

public interface Renderable {
    public Point2D.Double getLocation();
    public ArrayList<Image> getImage();
    public Direction getDirection();
}
