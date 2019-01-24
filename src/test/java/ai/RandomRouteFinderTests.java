package test.java.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ai.routefinding.RandomRouteFinder;
import objects.Entity;
import utils.enums.EntityType;

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
				new Entity(EntityType.PACMAN),
				new Entity(EntityType.GHOST1),
				new Entity(EntityType.GHOST2),
				new Entity(EntityType.GHOST3),
				new Entity(EntityType.GHOST4)};
	}
	
	@Test
	void setGameAgentsNullPointer() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Executable e = () -> rrf.setAgents(null, EntityType.PACMAN);
		assertThrows(NullPointerException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalArgumentLengthShort() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(EntityType.PACMAN),
				new Entity(EntityType.GHOST1)};
		Executable e = () -> rrf.setAgents(gas, EntityType.PACMAN);
		assertThrows(IllegalArgumentException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalArgumentLengthLong() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(EntityType.PACMAN),
				new Entity(EntityType.GHOST1),
				new Entity(EntityType.GHOST2),
				new Entity(EntityType.GHOST3),
				new Entity(EntityType.GHOST4),
				new Entity(EntityType.PACMAN)};
		Executable e = () -> rrf.setAgents(gas, EntityType.PACMAN);
		assertThrows(IllegalArgumentException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalArgumentDuplicates() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(EntityType.PACMAN),
				new Entity(EntityType.GHOST1),
				new Entity(EntityType.GHOST2),
				new Entity(EntityType.GHOST3),
				new Entity(EntityType.PACMAN)};
		Executable e = () -> rrf.setAgents(gas, EntityType.PACMAN);
		assertThrows(IllegalArgumentException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalState() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(EntityType.PACMAN),
				new Entity(EntityType.GHOST1),
				new Entity(EntityType.GHOST2),
				new Entity(EntityType.GHOST3),
				new Entity(EntityType.GHOST4)};
		rrf.setAgents(gas, EntityType.PACMAN);
		Executable e = () -> rrf.setAgents(gas, EntityType.PACMAN);
		assertThrows(IllegalStateException.class, e);
	}

	@Test
	void getRouteValid() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Entity[] gas = {
				new Entity(EntityType.PACMAN),
				new Entity(EntityType.GHOST1),
				new Entity(EntityType.GHOST2),
				new Entity(EntityType.GHOST3),
				new Entity(EntityType.GHOST4)};
		rrf.setAgents(gas, EntityType.PACMAN);
		rrf.getRoute();
	}
	
	@Test
	void getRouteNoAgentsSet() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Executable e = () -> rrf.getRoute();
		assertThrows(IllegalStateException.class, e);
	}
}
