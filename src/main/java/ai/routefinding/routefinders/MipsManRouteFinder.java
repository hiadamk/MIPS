package ai.routefinding.routefinders;

import java.awt.geom.Point2D;

import ai.routefinding.RouteFinder;
import utils.enums.Direction;

/**Route finding algorithm that controls Mipsman. Will aim to reach the nearest pellet whilst avoiding any ghouls.
 * @author Lewis Ackroyd*/
public class MipsManRouteFinder implements RouteFinder {

	@Override
	public Direction getRoute(Point2D.Double myLocation, Point2D.Double targetLocation) {
		// TODO Auto-generated method stub
		return new RandomRouteFinder().getRoute(myLocation,targetLocation);
	}

}
