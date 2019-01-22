package tests.ai;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

import org.junit.jupiter.api.Test;

import shared.GameAgent;
import shared.enums.GameAgentEnum;

class GameAgentTests {

	//Tests that objects can be initialised.
	@Test
	void initialization() {
		@SuppressWarnings("unused")
		GameAgent ga = new GameAgent(GameAgentEnum.MIPSMAN, new Point(10,10));
	}
	
	@Test
	void getCurrentPosition() {
		GameAgent ga = new GameAgent(GameAgentEnum.MIPSMAN, new Point(10,10));
		assertTrue((ga.getCurrentPosition().equals(new Point(10,10))));
	}

	@Test
	void getMyId() {
		GameAgent ga = new GameAgent(GameAgentEnum.MIPSMAN, new Point(10,10));
		assertEquals(ga.getMyId(), GameAgentEnum.MIPSMAN);
	}
	
	@Test
	void getDirections() {
		fail("Not yet implemented.");
	}
}
