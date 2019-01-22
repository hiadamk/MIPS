package ai;

import java.util.HashSet;

import shared.GameAgent;
import shared.enums.GameAgentEnum;

public class AILoopControl extends Thread {

	public AILoopControl() {
	}
	
	public static boolean validGameAgentArray(GameAgent[] gameAgents) {
		if (gameAgents.length != 5) {
			throw new IllegalArgumentException("gameAgents must have a length of exactly 5.");
		}
		HashSet<GameAgentEnum> s = new HashSet<GameAgentEnum>();
		for (GameAgent g : gameAgents) {
			if (s.contains(g.getMyId())) {
				return false;
			}
			s.add(g.getMyId());
		}
		return true;
	}
}
