package tests.ai;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ai.routefinding.RandomRouteFinder;
import shared.GameAgent;
import shared.enums.GameAgentEnum;

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
		GameAgent[] gas = {
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(0,0)),
				new GameAgent(GameAgentEnum.DOSY, new Point(1,0)),
				new GameAgent(GameAgentEnum.EINY, new Point(2,0)),
				new GameAgent(GameAgentEnum.SANY, new Point(3,0)),
				new GameAgent(GameAgentEnum.FOURY, new Point(5,0))};
	}
	
	@Test
	void setGameAgentsNullPointer() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Executable e = () -> rrf.setAgents(null, GameAgentEnum.MIPSMAN);
		assertThrows(NullPointerException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalArgumentLengthShort() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		GameAgent[] gas = {
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(0,0)),
				new GameAgent(GameAgentEnum.DOSY, new Point(1,0))};
		Executable e = () -> rrf.setAgents(gas, GameAgentEnum.MIPSMAN);
		assertThrows(IllegalArgumentException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalArgumentLengthLong() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		GameAgent[] gas = {
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(0,0)),
				new GameAgent(GameAgentEnum.DOSY, new Point(1,0)),
				new GameAgent(GameAgentEnum.EINY, new Point(2,0)),
				new GameAgent(GameAgentEnum.SANY, new Point(3,0)),
				new GameAgent(GameAgentEnum.FOURY, new Point(4,0)),
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(5,0))};
		Executable e = () -> rrf.setAgents(gas, GameAgentEnum.MIPSMAN);
		assertThrows(IllegalArgumentException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalArgumentDuplicates() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		GameAgent[] gas = {
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(0,0)),
				new GameAgent(GameAgentEnum.DOSY, new Point(1,0)),
				new GameAgent(GameAgentEnum.EINY, new Point(2,0)),
				new GameAgent(GameAgentEnum.SANY, new Point(3,0)),
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(5,0))};
		Executable e = () -> rrf.setAgents(gas, GameAgentEnum.MIPSMAN);
		assertThrows(IllegalArgumentException.class, e);
	}
	
	@Test
	void setGameAgentsIllegalState() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		GameAgent[] gas = {
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(0,0)),
				new GameAgent(GameAgentEnum.DOSY, new Point(1,0)),
				new GameAgent(GameAgentEnum.EINY, new Point(2,0)),
				new GameAgent(GameAgentEnum.SANY, new Point(3,0)),
				new GameAgent(GameAgentEnum.FOURY, new Point(5,0))};
		rrf.setAgents(gas, GameAgentEnum.MIPSMAN);
		Executable e = () -> rrf.setAgents(gas, GameAgentEnum.MIPSMAN);
		assertThrows(IllegalStateException.class, e);
	}

	@Test
	void getRouteValid() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		GameAgent[] gas = {
				new GameAgent(GameAgentEnum.MIPSMAN, new Point(0,0)),
				new GameAgent(GameAgentEnum.DOSY, new Point(1,0)),
				new GameAgent(GameAgentEnum.EINY, new Point(2,0)),
				new GameAgent(GameAgentEnum.SANY, new Point(3,0)),
				new GameAgent(GameAgentEnum.FOURY, new Point(5,0))};
		rrf.setAgents(gas, GameAgentEnum.MIPSMAN);
		rrf.getRoute();
	}
	
	@Test
	void getRouteNoAgentsSet() {
		RandomRouteFinder rrf = new RandomRouteFinder();
		Executable e = () -> rrf.getRoute();
		assertThrows(IllegalStateException.class, e);
	}
}
