package ai;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import objects.Entity;
import utils.enums.EntityType;

class AILoopControlTests {
	private static final Entity[] ALL_AGENTS= {
			new Entity(EntityType.PACMAN),
			new Entity(EntityType.GHOST1),
			new Entity(EntityType.GHOST2),
			new Entity(EntityType.GHOST3),
			new Entity(EntityType.GHOST4)};

	@Test
	void testRun() {
		fail("Not yet implemented");
	}

	@Test
	void testValidGameAgentArray() {
		fail("Not yet implemented");
	}

	@Test
	void testAILoopControl() {
		AILoopControl ailc = new AILoopControl(ALL_AGENTS, new Entity[0], AILoopControl.map2);
	}

	@Test
	void testKillAI() {
		fail("Not yet implemented");
	}

}
