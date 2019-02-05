package ai;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Point2D;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ai.routefinding.RandomRouteFinder;
import objects.Entity;

class RandomRouteFinderTests {

	@Test
	void initialization() {
		@SuppressWarnings("unused")
		RandomRouteFinder rrf = new RandomRouteFinder();
	}

	@Test
	void setGameAgentsValid() {
		@SuppressWarnings("unused")
		RandomRouteFinder rrf = new RandomRouteFinder();
		@SuppressWarnings("unused")
		Entity[] gas = {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 4, null)};
	}
	@Test
	void setGameAgentsNullPointer() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Executable e = () -> rrf.setAgents(null, 0);
		assertThrows(NullPointerException.class, e);
	}
	@Test
	void setGameAgentsIllegalArgumentDuplicates() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 0, null)};
		Executable e = () -> rrf.setAgents(gas, 0);
		assertThrows(IllegalArgumentException.class, e);
	}
	@Test
	void setGameAgentsIllegalState() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 4, null)};
		rrf.setAgents(gas, 0);
		Executable e = () -> rrf.setAgents(gas, 0);
		assertThrows(IllegalStateException.class, e);
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
		rrf.setAgents(gas, 1);
		rrf.getRoute(0, null);
	}
	@Test
	void getRouteNoAgentsSet() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Executable e = () -> rrf.getRoute(0, null);
		assertThrows(IllegalStateException.class, e);
	}
	@Test
	void getRouteInvalidPacmanID() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 4, null)};
		rrf.setAgents(gas, 1);
		Executable e = () -> rrf.getRoute(-1, null);
		assertThrows(IllegalArgumentException.class, e);
		e = () -> rrf.getRoute(7, null);
		assertThrows(IllegalArgumentException.class, e);
	}
}