package ai.routefinding;

import objects.Entity;
import utils.enums.Direction;
import java.awt.Point;
import java.util.Random;

public class RandomRouteFinder implements RouteFinder {
    
    private static final Random R = new Random();
    private static final Direction DEFAULT = Direction.UP;
    
    /**
     * Creates an instance of this routeFinder.
     */
    public RandomRouteFinder() {}
    
    
    /**
     * Returns the direction to travel in until the next junction is reached. Requires {@link
     * #setAgents(Entity[], int) setAgents()} method to have been called before use.
     *
     * @param pacmanID The main ID of the entity that is currently pacman.
     * @return The direction to travel in.
     * @throws IllegalStateException    The gameAgents have not been set. Call {@link
     *                                  #setAgents(Entity[], int) setAgents()} before calling this method.
     * @throws IllegalArgumentException PacmanID must be within the range of gameAgents Array.
     */
    @Override
    public Direction getRoute(Point myLocation, Point targetLocation) {
    	if (myLocation == null || targetLocation == null) {
    		throw new NullPointerException("One or both positions are not set.");
    	}
        Direction dir;
        int dirValue = R.nextInt(6);
        switch (dirValue) {
            case 0: {
                dir = Direction.UP;
                break;
            }
            case 1: {
                dir = Direction.DOWN;
                break;
            }
            case 2: {
                dir = Direction.LEFT;
                break;
            }
            case 3: {
                dir = Direction.RIGHT;
                break;
            }
            case 4: {
                // makes ghost twice as likely to move towards pacman as away from them
                if (myLocation.getY() > targetLocation.getY()) {
                    dir = Direction.UP;
                } else {
                    dir = Direction.DOWN;
                }
                break;
            }
            case 5: {
                if (myLocation.getX() > targetLocation.getX()) {
                    dir = Direction.LEFT;
                } else {
                    dir = Direction.RIGHT;
                }
                break;
            }
            default: {
                // state should not be reachable
                System.err.println("Value out of range. Default value given: " + DEFAULT);
                dir = DEFAULT;
            }
        }
        return dir;
    }
}
