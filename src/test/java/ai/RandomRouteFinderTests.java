package ai;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ai.mapping.Mapping;
import ai.routefinding.RandomRouteFinder;
import objects.Entity;

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
}
