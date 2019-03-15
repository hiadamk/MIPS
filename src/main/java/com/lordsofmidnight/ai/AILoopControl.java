package com.lordsofmidnight.ai;

import com.lordsofmidnight.ai.mapping.Mapping;
import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.ai.routefinding.SampleSearch;
import com.lordsofmidnight.ai.routefinding.routefinders.*;
import com.lordsofmidnight.ai.routefinding.routefinders.condition.ConditionalInterface;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.gamestate.points.PointSet;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Control class for all AI.
 *
 * @author Lewis Ackroyd
 */
public class AILoopControl extends Thread {

    private static final boolean DEBUG = false;
    private int counter = 0;

    private static final int INVINCIBILITY_AVOID_DISTANCE = 20;
    private static final int INVINCIBILITY_PREFER_MULTIPLIER = 2;
    private static final int OPPOSITE_DIRECTION_DIVISOR = 4;
    private static final long SLEEP_TIME = 1;
    private final Entity[] controlAgents;
    private final PointSet junctions;
    private final PointMap<PointSet> edges;
    private final BlockingQueue<Input> directionsOut;
    private final Map map;
    private final Entity[] gameAgents;
    private boolean runAILoop;
    private int mipsmanID;
    private HashMap<String, Pellet> pellets;


    /**Initialises the object prior to the AI loop being executed.
     * @param gameAgents The complete set of all entities that are controlled (by AI or players) in the game.
     * @param controlIds The set of main Ids that the AI will control.
     * @param map The map the game is being played on.
     * @param directionsOut The {@link BlockingQueue}<{@link Input}> That processes all agent direction instructions.
     * @throws IllegalArgumentException gameAgent array contains duplicate main IDs.
     * @throws IllegalStateException Cannot have more than one mipsman.
     * @throws IllegalStateException The control ID does not match an agent main ID.
     * @author Lewis Ackroyd*/
    public AILoopControl(
            Entity[] gameAgents, int[] controlIds, Map map, BlockingQueue<Input> directionsOut, HashMap<String, Pellet> pellets) {
        validateAgents(gameAgents);
        if (DEBUG) {
            for (int i : controlIds) {
                System.out.println("ID: " + i);
            }
        }
        this.setDaemon(true);
        this.runAILoop = true;
        this.gameAgents = gameAgents;
        this.controlAgents = new Entity[controlIds.length];
        this.junctions = Mapping.getJunctions(map);
        this.edges = Mapping.getEdges(map, junctions);
        this.directionsOut = directionsOut;
        this.map = map;
        this.pellets = pellets;

        generateRouteFinders();
        correctMipsmanRouteFinder();
        assignControlEntities(controlIds);
    }

    /**Checks that the entities given to the AI all have unique IDs and that only one is mipsman.
     * @param gameAgents The complete set of all entities that are controlled (by AI or players) in the game.
     * @throws IllegalArgumentException gameAgent array contains duplicate main IDs.
     * @throws IllegalStateException Cannot have more than one mipsman.
     * @author Lewis Ackroyd*/
    private void validateAgents(Entity[] gameAgents)
            throws IllegalArgumentException, IllegalStateException {
        HashSet<Integer> ids = new HashSet<Integer>();
        for (Entity e : gameAgents) {
            if (!ids.add(e.getClientId())) {
                throw new IllegalArgumentException("gameAgent array contains duplicate main IDs.");
            }
        }

        boolean mipsmanFound = false;
        for (Entity ent : gameAgents) {
            if (ent.isMipsman() && mipsmanFound) {
                throw new IllegalStateException("Cannot have more than one mipsman.");
            } else if (ent.isMipsman()) {
                mipsmanFound = true;
            }
        }
    }

    /**Initialises the {@link RouteFinder}s for every game agent, regardless of whether it is AI controlled or not.
     * @author Lewis Ackroyd*/
    private void generateRouteFinders() {
        for (int i = 0; i < gameAgents.length; i++) {
            RouteFinder routeFinder;
            switch (i) {
                case 0: {
                    routeFinder = new MipsManRouteFinder(pellets, gameAgents);
                    break;
                }
                case 1: {
                    routeFinder = new AStarRouteFinder(junctions, edges, map);
                    break;
                }
                case 2: {
                    routeFinder = new NextJunctionRouteFinder(gameAgents, map, junctions, edges);
                    break;
                }
                case 3: {
                    routeFinder = new PowerPelletPatrolRouteFinder();
                    break;
                }
                default: {
                    routeFinder = new RandomRouteFinder();
                    break;
                }
            }
            gameAgents[i].setRouteFinder(routeFinder);
        }
    }

    /**Assigns the reference for the entities controlled by the AI.
     * @param controlIds The IDs of all agents to be controlled by the AI.
     * @throws IllegalStateException The control ID does not match an agent main ID.
     * @author Lewis Ackroyd*/
    private void assignControlEntities(int[] controlIds) throws IllegalStateException {
        for (int i = 0; i < controlIds.length; i++) {
            boolean agentNotFound = true;
            for (Entity ent : gameAgents) {
                if (ent.getClientId() == controlIds[i]) {
                    controlAgents[i] = ent;
                    agentNotFound = false;
                    break;
                }
            }
            if (agentNotFound) {
                throw new IllegalStateException("The control ID does not match an agent main ID.");
            }
        }
    }

    /**Corrects the {@link RouteFinder}s after a collision between mipsman and a ghoul.*/
    private synchronized void correctMipsmanRouteFinder() {
        Entity mipsman = null;
        Entity mipsmanRoute = null;
        for (Entity ent : gameAgents) {
            if (ent.isMipsman()) {
                mipsman = ent;
                mipsmanID = ent.getClientId();
            }
            if (ent.getRouteFinder().getClass() == MipsManRouteFinder.class) {
                mipsmanRoute = ent;
            }
        }
        if (mipsman != null && mipsmanRoute != null) {
            RouteFinder r = mipsman.getRouteFinder();
            mipsman.setRouteFinder(mipsmanRoute.getRouteFinder());
            mipsmanRoute.setRouteFinder(r);
        }
    }

    @Override
    public void run() {
        System.out.println("Starting AI loop...");

        while (runAILoop && controlAgents.length > 0) {
            for (Entity ent : controlAgents) {
                Point currentLocation = ent.getLocation().getCopy();
                Point currentGridLocation = currentLocation.getGridCoord();
                if (currentLocation.isCentered()) {
                  boolean atLastCoord = atPreviousCoordinate(ent, currentGridLocation);
                    if (!ent.getDirection().isMovementDirection()
                        || !Methods.validateDirection(ent.getDirection(), currentLocation, map) || (
                        junctions.contains(currentGridLocation) && !atLastCoord)) {
                      if (atLastCoord) {
                            Point nearestJunction = Mapping.findNearestJunction(currentLocation, map, junctions);
                            Direction dir;
                            if (!nearestJunction.equals(currentGridLocation)) {
                                dir = Mapping.directionBetweenPoints(currentLocation, nearestJunction);
                            } else {
                                dir =
                                    new RandomRouteFinder()
                                        .getRoute(currentLocation,
                                            gameAgents[mipsmanID].getLocation());
                            }
                            dir = confirmOrReplaceDirection(ent.getDirection(), currentLocation, dir);
                            setDirection(dir, ent);

                        } else {
                            ent.setLastGridCoord(currentGridLocation);
                            if (junctions.contains(currentGridLocation)) {
                                executeRoute(ent, currentLocation);
                            }
                        }
                    }
                }
                //processPowerUps(ent);
            }

            correctMipsmanRouteFinder();

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                runAILoop = false;
            }
        }
        System.out.println("AI safely terminated.");
    }

    private void executeRoute(Entity ent, Point currentLocation) {
        RouteFinder r = ent.getRouteFinder();
        Point mipsManLoc = gameAgents[mipsmanID].getLocation();
        Direction direction = r.getRoute(currentLocation, mipsManLoc);
        direction = accountForPowerUps(currentLocation, direction);
        direction = confirmOrReplaceDirection(ent.getDirection(), currentLocation, direction);
        setDirection(direction, ent);
    }

    private Direction accountForPowerUps(Point position, Direction direction) {
        Direction previousDirection = direction;
        direction = invincibilityAdjust(position, direction);
        return direction;
    }

    private Direction invincibilityAdjust(Point position, Direction direction) {
        class InvincibleAgentCondition implements ConditionalInterface {
            private final Entity[] agents;

            public InvincibleAgentCondition(Entity[] agents) {
                this.agents = agents;
            }

            @Override
            public boolean condition(Point position) {
                for (Entity ent : agents) {
                    if (ent.getLocation().getGridCoord().equals(position.getGridCoord())) {
                        if (ent.isInvincible()) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        int[] directionValues = new SampleSearch(INVINCIBILITY_AVOID_DISTANCE, map).getDirectionCounts(position, new InvincibleAgentCondition(gameAgents));
        Random r = new Random();
        int total = 0;
        for (int i : directionValues) { total += i; }
        if (total>0) {
            int probability = r.nextInt(total);
            probability -= directionValues[Direction.UP.toInt()];
            if (probability<=0) {
                direction = reRoll(Direction.UP, direction, position);
            }
            probability -= directionValues[Direction.DOWN.toInt()];
            if (probability<=0) {
                direction = reRoll(Direction.DOWN, direction, position);
            }
            probability -= directionValues[Direction.LEFT.toInt()];
            if (probability<=0) {
                direction = reRoll(Direction.LEFT, direction, position);
            }
            probability -= directionValues[Direction.RIGHT.toInt()];
            if (probability<=0) {
                direction = reRoll(Direction.RIGHT, direction, position);
            }
        }
        return direction;
    }

    private Direction reRoll(Direction avoidDirection, Direction preferDirection, Point currentLoc) {
        ArrayList<Direction> validDirections = getValidDirections(currentLoc, map);
        if (validDirections.contains(avoidDirection)&&validDirections.size()>1) {
            validDirections.remove(avoidDirection);
        }
        if (validDirections.contains(preferDirection)) {
            for (int i = 1; i<INVINCIBILITY_PREFER_MULTIPLIER; i++) {
                validDirections.add(preferDirection);
            }
        }
        Random r = new Random();
        int val = r.nextInt(validDirections.size());
        return validDirections.get(val);
    }

    private void processPowerUps(Entity ent) {
        List<PowerUp> powerUpList = ent.getItems();
        if (!powerUpList.isEmpty()&&!ent.isPowerUpUsed()) {
            ent.setPowerUpUsedFlag(true);
            setDirection(Direction.USE, ent);
        }
    }

    private void setDirection(Direction direction, Entity ent) {
        if (direction == null) {
            return;
        }
        if(direction!=ent.getDirection()&&!ent.isDirectionSet()){
            if (DEBUG) {
                System.out.println(counter++ + " " + direction + " " + ent.getClientId() + " " + ent.getLastGridCoord() + " " + ent.getLocation());
            }
            ent.setDirectionSetFlag(true);
            directionsOut.add(new Input(ent.getClientId(), direction));
        }
    }

    private boolean atPreviousCoordinate(Entity ent, Point currentLocation) {
        if (ent.getLastGridCoord() == null) {
            return false;
        }
        return ent.getLastGridCoord().equals(currentLocation);
    }

    private Direction confirmOrReplaceDirection(Direction oldDirection, Point currentLocation, Direction dir) {
        ArrayList<Direction> validDirections = getValidDirections(currentLocation, map);
        Random r = new Random();
        if (validDirections.size()<=0) {
            if (DEBUG) {
                System.err.println("NO VALID DIRECTIONS");
                System.err.println(currentLocation);
            }
            return null;
        }
        if (!Methods.validateDirection(dir, currentLocation, map)) {
            if (validDirections.size()>0) {
                int randI = r.nextInt(validDirections.size());
                dir = validDirections.get(randI);
            }
        }
        if ((oldDirection==null ||oldDirection.getInverse()==dir) && validDirections.size()>1) {
            int randI = r.nextInt(OPPOSITE_DIRECTION_DIVISOR);
            if (randI==0) {
                return dir;
            }
            validDirections.remove(dir);
            randI = r.nextInt(validDirections.size());
            dir = validDirections.get(randI);
        }
        if (!Methods.validateDirection(dir, currentLocation, map)) {
            throw new IllegalStateException("ERROR");
        }
        return dir;
    }

    public boolean killAI() {
        runAILoop = false;
        return isAlive();
    }

    private static final ArrayList<Direction> getValidDirections(Point p, Map map) {
        ArrayList<Direction> validDirections = new ArrayList<>();
        if (Methods.validateDirection(Direction.UP, p, map)) validDirections.add(Direction.UP);
        if (Methods.validateDirection(Direction.DOWN, p, map)) validDirections.add(Direction.DOWN);
        if (Methods.validateDirection(Direction.LEFT, p, map)) validDirections.add(Direction.LEFT);
        if (Methods.validateDirection(Direction.RIGHT, p, map)) validDirections.add(Direction.RIGHT);
        return validDirections;
    }
}
