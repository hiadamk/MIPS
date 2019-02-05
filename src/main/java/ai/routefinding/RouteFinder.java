package ai.routefinding;

import java.awt.Point;

import objects.Entity;
import utils.enums.Direction;

public interface RouteFinder {
    
    /**
     * Returns the direction to travel in until the next junction is reached.
     *
     * @param pacmanID The ID for the current pacman.
     * @return The direction to travel in.
     */
    public Direction getRoute(int pacmanID, Point myLocation);
    
    /**
     * Set all agents in the game for reference and set which of those agents the current route is
     * being generated for.
     *
     * @param gameAgents The array containing all agents within the game exactly once only.
     * @param myAgent    The enum referring to the current agent for which this routefinder is being
     *                   assigned.
     */
    public void setAgents(Entity[] gameAgents, int myAgent);
}
