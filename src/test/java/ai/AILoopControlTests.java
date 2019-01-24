package ai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import objects.Entity;

class AILoopControlTests {
	private static final Entity[] ALL_AGENTS= {
			new Entity(true, 0),
			new Entity(false, 1),
			new Entity(false, 2),
			new Entity(false, 3),
			new Entity(false, 4)};

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
