package ai.routefinding.routefinders;

import ai.routefinding.RouteFinder;
import utils.Point;
import utils.enums.Direction;

/**
 * Route finding algorithm that will aim to reach the junction that will next be reached by Mipsman.
 *
 * @author Lewis Ackroyd
 */
public class NextJunctionRouteFinder implements RouteFinder {
    private static final boolean COMPLETE = false;
    /**
     * */
    @Override
    public Direction getRoute(Point myLocation, Point targetLocation) {
        if (!COMPLETE) {
            return new RandomRouteFinder().getRoute(myLocation, targetLocation);
        }
        return null;
    }
}
