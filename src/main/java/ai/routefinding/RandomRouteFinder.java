package ai.routefinding;

import objects.Entity;
import utils.enums.Direction;
import java.awt.geom.Point2D;
import java.util.Random;

public class RandomRouteFinder extends AbstractRouteFinder {
    
    private static final Random R = new Random();
    private static final Direction DEFAULT = Direction.UP;
    
    /**
     * Creates an instance of this routeFinder. As it is random the map is irrelevant.
     */
    public RandomRouteFinder() {
        super();
    }
    
    
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
    public Direction getRoute(int pacmanID) {
        if (!agentsSet) {
            throw new IllegalStateException("gameAgents have not been set.");
        }
        if (pacmanID < 0 || pacmanID >= gameAgents.length) {
            throw new IllegalArgumentException("Pacman ID must be within range of array.");
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
                if (gameAgents[pacmanID].getLocation() != null) {
                    Point2D.Double mmanPos = gameAgents[pacmanID].getLocation();
                    Point2D.Double myPos = gameAgents[myId].getLocation();
                    if (myPos.getY() > mmanPos.getY()) {
                        dir = Direction.UP;
                    } else {
                        dir = Direction.DOWN;
                    }
                } else {
                    dir = DEFAULT;
                }
                break;
            }
            case 5: {
                // makes ghost twice as likely to move towards pacman as away from them
                if (gameAgents[pacmanID].getLocation() != null) {
                    Point2D.Double mmanPos = gameAgents[pacmanID].getLocation();
                    Point2D.Double myPos = gameAgents[myId].getLocation();
                    if (myPos.getX() > mmanPos.getX()) {
                        dir = Direction.LEFT;
                    } else {
                        dir = Direction.RIGHT;
                    }
                } else {
                    dir = DEFAULT;
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
