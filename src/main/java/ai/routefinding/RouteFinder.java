package ai.routefinding;

import java.awt.Point;
import utils.enums.Direction;

public interface RouteFinder {
    
    /**
     * Returns the direction to travel in until the next junction is reached.
     *
     * @param myLocation The start location for route finding.
     * @param targetLocation The target of the route finding.
     * @return The direction to travel in.
     */
    public Direction getRoute(Point myLocation, Point targetLocation);
    
}