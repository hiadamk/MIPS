package ai;

import static org.junit.jupiter.api.Assertions.*;
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
				new Entity(true, 0),
				new Entity(false, 1),
				new Entity(false, 2),
				new Entity(false, 3),
				new Entity(false, 4)};
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
				new Entity(true, 0),
				new Entity(false, 1),
				new Entity(false, 2),
				new Entity(false, 3),
				new Entity(false, 0)};
		Executable e = () -> rrf.setAgents(gas, 0);
		assertThrows(IllegalArgumentException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalState() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0),
				new Entity(false, 1),
				new Entity(false, 2),
				new Entity(false, 3),
				new Entity(false, 4)};
		rrf.setAgents(gas, 0);
		Executable e = () -> rrf.setAgents(gas, 0);
		assertThrows(IllegalStateException.class, e);
	}

	@Test
	void getRouteValid() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(true, 0),
				new Entity(false, 1),
				new Entity(false, 2),
				new Entity(false, 3),
				new Entity(false, 4)};
		rrf.setAgents(gas, 0);
		rrf.getRoute();
	}
	
	@Test
	void getRouteNoAgentsSet() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Executable e = () -> rrf.getRoute();
		assertThrows(IllegalStateException.class, e);
	}
}
