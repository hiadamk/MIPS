package ai;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Point2D;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ai.mapping.Mapping;
import ai.routefinding.routefinders.RandomRouteFinder;
import objects.Entity;
import utils.enums.Direction;

/**Unit tests for the {@link RandomRouteFinder} class.
 * @author Lewis Ackroyd*/
class RandomRouteFinderTests {

	@Test
	void initialization() {
		@SuppressWarnings("unused")
		RandomRouteFinder rrf = new RandomRouteFinder();
	}

	@Test
	void getRouteValid() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 4, null)};
		for (int i = 0; i < gas.length; i++) {
			gas[i].setLocation(new Point2D.Double(0, i));
		}
		rrf.getRoute(Mapping.point2DtoPoint(gas[1].getLocation()), Mapping.point2DtoPoint(gas[0].getLocation()));
	}
	
	@Test
	void getRouteNullPointer() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 4, null)};
		Executable e = () -> rrf.getRoute(Mapping.point2DtoPoint(gas[1].getLocation()), Mapping.point2DtoPoint(gas[0].getLocation()));
		assertThrows(NullPointerException.class, e);
		
		gas[0].setLocation(new Point2D.Double(1,1));
		
		e = () -> rrf.getRoute(Mapping.point2DtoPoint(gas[1].getLocation()), Mapping.point2DtoPoint(gas[0].getLocation()));
		assertThrows(NullPointerException.class, e);
		
		e = () -> rrf.getRoute(Mapping.point2DtoPoint(gas[0].getLocation()), Mapping.point2DtoPoint(gas[1].getLocation()));
		assertThrows(NullPointerException.class, e);
	}
	
	@Test
	void directionProbabilities() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 4, null)};

		for (int i = 0; i < gas.length; i++) {
			gas[i].setLocation(new Point2D.Double(i, i));
		}
		
		HashMap<Direction, Integer> counters = new HashMap<Direction, Integer>();
		counters.put(Direction.DOWN, 0);
		counters.put(Direction.UP, 0);
		counters.put(Direction.LEFT, 0);
		counters.put(Direction.RIGHT, 0);
		for (int count = 0; count<100000; count++) {
			Direction dir = rrf.getRoute(Mapping.point2DtoPoint(gas[1].getLocation()), Mapping.point2DtoPoint(gas[0].getLocation()));
			counters.put(dir, counters.get(dir)+1);
		}
		for (Direction d : counters.keySet()) {
			System.out.println(d + " " + counters.get(d));
		}
		
		boolean vertical = (counters.get(Direction.UP)>((counters.get(Direction.DOWN)-1000)*2));
		boolean horizontal = counters.get(Direction.LEFT)>((counters.get(Direction.RIGHT)-1000)*2);
		assertTrue(vertical);
		assertTrue(horizontal);
	}
}
