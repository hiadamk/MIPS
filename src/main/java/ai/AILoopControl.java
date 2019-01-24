package ai;

import java.util.HashSet;

import objects.Entity;
import utils.enums.EntityType;

public class AILoopControl extends Thread {

	public AILoopControl() {
	}
	
	public static boolean validGameAgentArray(Entity[] gameAgents) {
		if (gameAgents.length != 5) {
			throw new IllegalArgumentException("gameAgents must have a length of exactly 5.");
		}
		HashSet<EntityType> s = new HashSet<EntityType>();
		for (Entity g : gameAgents) {
			if (s.contains(g.getType())) {
				return false;
			}
			s.add(g.getType());
		}
		return true;
	}
}
