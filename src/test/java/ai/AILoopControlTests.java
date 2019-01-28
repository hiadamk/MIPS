package ai;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import objects.Entity;

class AILoopControlTests {
	private static final Entity[] ALL_AGENTS= {
			new Entity(true, 0),
			new Entity(false, 1),
			new Entity(false, 2),
			new Entity(false, 3),
			new Entity(false, 4)};

	@Test
	void testRunValid() {
		int[] ids = {4};
		for (int i = 0; i<ALL_AGENTS.length; i++) {
			ALL_AGENTS[i].setLocation(new Point2D.Double(1 + i, 1));
		}
		AILoopControl ailc = new AILoopControl(ALL_AGENTS,  ids, AILoopControl.map2);
		ailc.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void testAILoopControlEmptyControl() {
		@SuppressWarnings("unused")
		AILoopControl ailc = new AILoopControl(ALL_AGENTS, new int[0], AILoopControl.map2);
	}
	@Test
	void testAILoopControlFullControl() {
		int[] controlIDs = {0, 1, 2, 3, 4};
		@SuppressWarnings("unused")
		AILoopControl ailc = new AILoopControl(ALL_AGENTS, controlIDs, AILoopControl.map2);
	}
	@Test
	void testAILoopControlTooManyIDs() {
		int[] controlIDs = {0, 1, 2, 3, 4, 5};
		Executable e = () -> new AILoopControl(ALL_AGENTS, controlIDs, AILoopControl.map2);
		assertThrows(IllegalStateException.class, e);
		
	}
	@Test
	void testAILoopControlInvalidMap() {
		Executable e = () -> new AILoopControl(ALL_AGENTS, new int[0], new int[0][0]);
		assertThrows(IllegalArgumentException.class, e);
		e = () -> new AILoopControl(ALL_AGENTS, new int[0], new int[10][0]);
		assertThrows(IllegalArgumentException.class, e);
		e = () -> new AILoopControl(ALL_AGENTS, new int[0], new int[0][10]);
		assertThrows(IllegalArgumentException.class, e);
	}
	@Test
	void testAILoopControlDuplicateID() {
		Entity[] entities= {
				new Entity(true, 0),
				new Entity(false, 1),
				new Entity(false, 2),
				new Entity(false, 3),
				new Entity(false, 2)};
		Executable e = () -> new AILoopControl(entities, new int[0], AILoopControl.map2);
		assertThrows(IllegalArgumentException.class, e);
	}

	@Test
	void testKillAI() {
		int[] i = {0};
		AILoopControl ailc = new AILoopControl(ALL_AGENTS,  i, AILoopControl.map2);
		assertFalse(ailc.killAI());
		ailc.start();
		assertTrue(ailc.killAI());
	}

}
