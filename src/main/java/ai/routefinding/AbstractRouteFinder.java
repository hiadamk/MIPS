package ai.routefinding;

import java.util.HashSet;
import objects.Entity;

public abstract class AbstractRouteFinder implements RouteFinder {
	
	Entity[] gameAgents;
    int myId;
    boolean agentsSet;
	
    /**
     * Creates an instance of this routeFinder.
     */
	public AbstractRouteFinder() {
		this.agentsSet = false;
	}
    
    /**
     * Set all agents in the game for reference and set which of those agents the current route is
     * being generated for.
     *
     * @param gameAgents The array containing all agents within the game exactly once only.
     * @param myId       The Enum referring to the current agent for which this routefinder is being
     *                   assigned.
     * @throws IllegalArgumentException gameAgent cannot contain duplicate IDs.
     * @throws IllegalStateException    The game agents cannot be re-assigned (this method can only be
     *                                  called once).
     */
    @Override
    public void setAgents(Entity[] gameAgents, int myId) {
        HashSet<Integer> ids = new HashSet<Integer>();
        // basic validation
        for (Entity e : gameAgents) {
            if (!ids.add(e.getClientId())) {
                throw new IllegalArgumentException("gameAgent array contains duplicate IDs.");
            }
        }
        // does not allow agents to be changed once started
        if (this.agentsSet) {
            throw new IllegalStateException("gameAgents already assigned.");
        }
        this.gameAgents = gameAgents;
        this.myId = myId;
        this.agentsSet = true;
    }
}