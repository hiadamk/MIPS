package ai;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.geom.Point2D;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ai.routefinding.RouteFinder;
import ai.routefinding.routefinders.MipsManRouteFinder;
import objects.Entity;
import utils.Map;
import utils.Input;

/**Unit tests for the {@link AILoopControl} class.
 * @author Lewis Ackroyd*/
class AILoopControlTests {
	public static final int[][] MAP_RAW = {
  {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
  {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
  {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
  {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
  {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
  {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
  {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
  {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
  {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
  {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
  {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
  {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
  {1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1},
  {1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
  {1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
  {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
  {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
  {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
  {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
  {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}

};
  	
	private static final BlockingQueue<Input> QUEUE = new LinkedBlockingQueue<Input>();
  	private static final Map MAP = new Map(MAP_RAW);
	
	private static final Entity[] ALL_AGENTS= {
			new Entity(true, 0, null),
			new Entity(false, 1, null),
			new Entity(false, 2, null),
			new Entity(false, 3, null),
			new Entity(false, 4, null)};

	@Test
	void testRunValid() {
		int[] ids = {4};
		for (int i = 0; i<ALL_AGENTS.length; i++) {
			ALL_AGENTS[i].setLocation(new Point2D.Double(1 + i, 1));
		}
		AILoopControl ailc = new AILoopControl(ALL_AGENTS,  ids, MAP, QUEUE);
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
		AILoopControl ailc = new AILoopControl(ALL_AGENTS, new int[0], MAP, QUEUE);
	}
	@Test
	void testAILoopControlFullControl() {
		int[] controlIDs = {0, 1, 2, 3, 4};
		@SuppressWarnings("unused")
		AILoopControl ailc = new AILoopControl(ALL_AGENTS, controlIDs, MAP, QUEUE);
	}
	@Test
	void testAILoopControlIdNotFound() {
		int[] controlIDs = {0, 1, 2, 3, 5};
		Executable e = () -> new AILoopControl(ALL_AGENTS, controlIDs, MAP, QUEUE);
		assertThrows(IllegalStateException.class, e);
	}
	@Test
	void testAILoopControlDuplicateID() {
		Entity[] entities= {
				new Entity(true, 0, null),
				new Entity(false, 1, null),
				new Entity(false, 2, null),
				new Entity(false, 3, null),
				new Entity(false, 2, null)};
		Executable e = () -> new AILoopControl(entities, new int[0], MAP, QUEUE);
		assertThrows(IllegalArgumentException.class, e);
	}
	@Test
	void testAILoopControlAgentRoleSwap() {
		int[] controlIDs = {0, 1, 2, 3, 4};
		AILoopControl ailc = new AILoopControl(ALL_AGENTS, controlIDs, MAP, QUEUE);
		ailc.start();
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		RouteFinder firstAgent = ALL_AGENTS[0].getRouteFinder();
		RouteFinder secondAgent = ALL_AGENTS[1].getRouteFinder();
		assertTrue(firstAgent.getClass()==MipsManRouteFinder.class);
		assertTrue(secondAgent.getClass()!=MipsManRouteFinder.class);
		
		ALL_AGENTS[0].setPacMan(false);
		ALL_AGENTS[1].setPacMan(true);
		
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(firstAgent.getClass()==MipsManRouteFinder.class);
		assertTrue(secondAgent.getClass()!=MipsManRouteFinder.class);
		
		ALL_AGENTS[1].setPacMan(false);
		ALL_AGENTS[0].setPacMan(true);
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(firstAgent.getClass()==MipsManRouteFinder.class);
		assertTrue(secondAgent.getClass()!=MipsManRouteFinder.class);
	}
	
	
	@Test
	void testKillAI() {
		int[] i = {0};
		AILoopControl ailc = new AILoopControl(ALL_AGENTS,  i, MAP, QUEUE);
		ailc.start();
		assertTrue(ailc.killAI());
		
		ailc = new AILoopControl(ALL_AGENTS,  i, MAP, QUEUE);
		assertFalse(ailc.killAI());
		ailc.start();
		assertTrue(ailc.killAI());
		
		i = new int[0];
		ailc = new AILoopControl(ALL_AGENTS,  i, MAP, QUEUE);
		ailc.start();
		assertTrue(ailc.killAI());
		
		ailc = new AILoopControl(ALL_AGENTS,  i, MAP, QUEUE);
		assertFalse(ailc.killAI());
		ailc.start();
		assertTrue(ailc.killAI());
	}

}
