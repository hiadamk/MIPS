package ai;

import ai.mapping.Mapping;
import ai.routefinding.RouteFinder;
import ai.routefinding.routefinders.MipsManRouteFinder;
import ai.routefinding.routefinders.RandomRouteFinder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import objects.Entity;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.Point;
import utils.enums.Direction;

/**
 * Control class for all AI.
 *
 * @author Lewis Ackroyd
 */
public class AILoopControl extends Thread {

    private static final long SLEEP_TIME = 1;
    private final Entity[] controlAgents;
    private final HashSet<Point> junctions;
    private final HashMap<Point, HashSet<Point>> edges;
    private final BlockingQueue<Input> directionsOut;
    private final Map map;
    private final Entity[] gameAgents;
    private boolean runAILoop;
    private int mipsmanID;

    public AILoopControl(Entity[] gameAgents, int[] controlIds, Map map, BlockingQueue<Input> directionsOut) {
        validateAgents(gameAgents);

        this.setDaemon(true);
        this.runAILoop = true;
        this.gameAgents = gameAgents;
        this.controlAgents = new Entity[controlIds.length];
        this.junctions = Mapping.getJunctions(map);
        this.edges = Mapping.getEdges(map, junctions);
        this.directionsOut = directionsOut;
        this.map = map;

        generateRouteFinders();
        correctMipsmanRouteFinder();
        assignControlEntities(controlIds);

    }

    private void validateAgents(Entity[] gameAgents) throws IllegalArgumentException, IllegalStateException{
        HashSet<Integer> ids = new HashSet<Integer>();
        for (Entity e : gameAgents) {
            if (!ids.add(e.getClientId())) {
                throw new IllegalArgumentException("gameAgent array contains duplicate main IDs.");
            }
        }

        boolean mipsmanFound = false;
        for (Entity ent : gameAgents) {
            if (ent.isPacman() && mipsmanFound) {
                throw new IllegalStateException("Cannot have more than one mipsman.");
            } else if (ent.isPacman()) {
                mipsmanFound = true;
            }
        }
    }

    private void generateRouteFinders() throws IllegalStateException {
        for (int i = 0; i< gameAgents.length; i++) {
            RouteFinder routeFinder;
            switch (i) {
                case 0: {
                    // TODO
                    routeFinder = new RandomRouteFinder();
                    break;
                }
                case 1: {
                    // TODO
                    routeFinder = new RandomRouteFinder(); // new AStarRouteFinder(junctions,edges,map);
                    break;
                }
                case 2: {
                    // TODO
                    routeFinder = new RandomRouteFinder();
                    break;
                }
                case 3: {
                    routeFinder = new RandomRouteFinder();
                    break;
                }
                case 4: { // Mipsman - no players
                    routeFinder = new RandomRouteFinder(); // new MipsManRouteFinder();
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

    private void assignControlEntities(int[] controlIds) {
        for (int i = 0; i<controlIds.length; i++) {
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

    private synchronized void correctMipsmanRouteFinder() {
        Entity mipsman = null;
        Entity mipsmanRoute = null;
        for (Entity ent : gameAgents) {
            if (ent.isPacman()) {
                mipsman = ent;
                mipsmanID = ent.getClientId();
            }
            if (ent.getRouteFinder().getClass() == MipsManRouteFinder.class) {
                mipsmanRoute = ent;
            }
        }
        if (mipsman!=null && mipsmanRoute!=null) {
            RouteFinder r = mipsman.getRouteFinder();
            mipsman.setRouteFinder(mipsmanRoute.getRouteFinder());
            mipsmanRoute.setRouteFinder(r);
        }
    }

    @Override
    public void run() {
        System.out.println("Starting AI loop...");

        while (runAILoop && controlAgents.length>0) {
            //System.out.println("StartAI");
            for (Entity ent : controlAgents) {
                Point currentLocation = ent.getLocation().getCopy();
                Point currentGridLocation = Mapping.getGridCoord(currentLocation);
                if (Methods.centreOfSquare(currentLocation)) {
                    if (ent.getDirection()==null||!Methods.validiateDirection(ent.getDirection(), ent, currentLocation, map)) {
                        if (atPreviousCoordinate(ent, currentGridLocation)) {
                            Point nearestJunction = Mapping.findNearestJunction(currentLocation, map, junctions);

                            Direction dir;
                            if (!nearestJunction.equals(currentGridLocation)) {
                                dir = Mapping.directionBetweenPoints(currentLocation, nearestJunction);
                            }
                            else {
                                dir = new RandomRouteFinder().getRoute(currentLocation, gameAgents[mipsmanID].getLocation());
                            }
                            dir = confirmOrReplaceDirection(ent, currentLocation, dir);
                            directionsOut.add(new Input(ent.getClientId(), dir));
                        }
                        else {
                            ent.setLastGridCoord(currentGridLocation);
                            if (junctions.contains(currentGridLocation)) {
                                executeRoute(ent, currentLocation);
                            }
                        }
                    }
                }
                else if (!Methods.validiateDirection(ent.getDirection(), ent, currentLocation, map)) {
                    Direction dir = new RandomRouteFinder().getRoute(currentLocation, gameAgents[mipsmanID].getLocation());
                    dir = confirmOrReplaceDirection(ent, currentLocation, dir);
                    directionsOut.add(new Input(ent.getClientId(), dir));
                }
            }

            correctMipsmanRouteFinder();

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                runAILoop = false;
            }
            //System.out.println("endAI");
        }
    }

    private void executeRoute(Entity ent, Point currentLocation) {
        RouteFinder r = ent.getRouteFinder();
        Point mipsManLoc = gameAgents[mipsmanID].getLocation();
        Direction direction = r.getRoute(currentLocation, mipsManLoc);
        direction = confirmOrReplaceDirection(ent, currentLocation, direction);
        directionsOut.add(new Input(ent.getClientId(), direction));
    }

    private boolean atPreviousCoordinate(Entity ent, Point currentLocation) {
        return ent.getLastGridCoord()==null||ent.getLastGridCoord().equals(currentLocation);
    }

    private Direction confirmOrReplaceDirection(Entity ent, Point currentLocation, Direction dir) {
        boolean[] dirs = {false, false, false, false};
        while (!Methods.validiateDirection(dir, ent, currentLocation, map)) {
            dir = new RandomRouteFinder().getRoute(currentLocation, gameAgents[mipsmanID].getLocation());
            dirs[dir.toInt()] = true;
            boolean allTried = true;
            for (int i = 0; i < 4; i++) {
                if (!dirs[i]) {
                    allTried = false;
                    break;
                }
            }

            if (allTried) {
                System.err.println("ALL DIRECTIONS TRIED");
                System.err.println(ent.getClientId());
                System.err.println(currentLocation);
                System.err.println(ent.getLocation());
                return null;
            }
        }
        if (!Methods.validiateDirection(dir, ent, currentLocation, map)) {
            throw new IllegalStateException("ERROR");
        }
        return dir;
    }

    public boolean killAI() {
        runAILoop = false;
        return isAlive();
    }
}