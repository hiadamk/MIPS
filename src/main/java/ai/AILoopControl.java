package ai;

import ai.mapping.Mapping;
import ai.routefinding.NoRouteFinderException;
import ai.routefinding.RouteFinder;
import ai.routefinding.routefinders.*;
import objects.Entity;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.enums.Direction;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

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
    private boolean runAILoop;
    private int mipsmanID;
    private final BlockingQueue<Input> directionsOut;
    private final Map map;

    /**
     * Initialises the control for the AI Control Loop.
     *
     * @param gameAgents
     *            All agents within the game.
     * @param controlIds
     *            The main ID of agents which will be AI controlled.
     * @param map
     *            The map on which route finding will occur.
     * @throws IllegalArgumentException
     *             gameAgent array contains duplicate client IDs.
     * @throws IllegalStateException
     *             Control ID does not match a gameAgent main ID.
     * @throws IllegalStateException
     *             Cannot have more than one Mipsman.
     */
    public AILoopControl(Entity[] gameAgents, int[] controlIds, Map map, BlockingQueue<Input> directionsOut) {
        validateInputs(gameAgents);

        this.setDaemon(true);
        this.runAILoop = true;
        this.controlAgents = new Entity[controlIds.length];
        this.junctions = Mapping.getJunctions(map);
        this.edges = Mapping.getEdges(map, junctions);
        this.directionsOut = directionsOut;
        this.map = map;

        generateRouteFinders(gameAgents, controlIds);
        correctMipsmanRouteFinder();
        for (Entity ent : gameAgents) {
            if (ent.isPacman()) {
                mipsmanID = ent.getClientId();
                break;
            }
        }
    }

    /*
     * Generates a {@link RouteFinder} for all entities that are controlled by the
     * AI.
     *
     * @param gameAgents The array of all agents within the game.
     *
     * @param controlIds The array of all main IDs that the AI will control.
     *
     * @throws IllegalStateException A main ID in controlIds does not exist for an
     * {@link Entity} in gameAgents.
     */
    private void generateRouteFinders(Entity[] gameAgents, int[] controlIds) throws IllegalStateException {
        for (int i = 0; i < controlIds.length; i++) {
            RouteFinder routeFinder;
            switch (i) {
                case 0: {
                    // TODO
                    routeFinder = new RandomRouteFinder();
                    break;
                }
                case 1: {
                    // TODO
                    routeFinder = new RandomRouteFinder();//new AStarRouteFinder(junctions,edges,map);
                    break;
                }
                case 2: {
                    // TODO
                    routeFinder = new RandomRouteFinder();
                    break;
                }
                case 3: {
                    // TODO
                    routeFinder = new RandomRouteFinder();
                    break;
                }
                case 4: { // Mipsman - no players
                    routeFinder = new RandomRouteFinder();//new MipsManRouteFinder();
                    break;
                }
                default: {
                    routeFinder = new RandomRouteFinder();
                    break;
                }
            }
            validateId(gameAgents, controlIds, i, routeFinder);
        }
    }

    /*
     * Checks that the given ID exists as the main ID of an {@link Entity} in
     * gameAgents.
     *
     * @param gameAgents The array of all agents within the game.
     *
     * @param controlIds The array of all main IDs that the AI will control.
     *
     * @param i The current index within the controlAgents array.
     *
     * @param routeFinder The {@link RouteFinder} that is being assigned to the
     * {@link Entity} with the corresponding main ID.
     *
     * @throws IllegalStateException The control ID does not match an agent main ID.
     */
    private void validateId(Entity[] gameAgents, int[] controlIds, int i, RouteFinder routeFinder)
            throws IllegalStateException {
        boolean agentNotFound = true;
        for (Entity ent : gameAgents) {
            if (ent.getClientId() == controlIds[i]) {
                controlAgents[i] = ent;
                ent.setRouteFinder(routeFinder);
                agentNotFound = false;
                break;
            }
        }
        if (agentNotFound) {
            throw new IllegalStateException("The control ID does not match an agent main ID.");
        }
    }

    /*
     * Checks that the given array does not contain any duplicate IDs and that
     * Mipsman is only declared once.
     *
     * @param gameAgents The array of all agents within the game.
     *
     * @throws IllegalArgumentException gameAgent array contains duplicate main IDs.
     *
     * @throws IllegalStateException Cannot have more than one Mipsman.
     */
    private void validateInputs(Entity[] gameAgents) throws IllegalArgumentException, IllegalStateException {
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

    /*
     * If Mipsman is one of the control agents, ensures that agent has the {@link
     * MipsManRouteFinder}.
     */
    private void correctMipsmanRouteFinder() {
        Entity mipsman = null;
        Entity mipsRoute = null;
        for (Entity ent : controlAgents) {
            if (ent.isPacman()) {
                mipsman = ent;
            }
            // an entity has the Mipsman RouteFinder but is not Mipsman
            else if (ent.getRouteFinder().getClass() == MipsManRouteFinder.class) {
                mipsRoute = ent;
            }
        }
        if (mipsman != null) {
            if (mipsman.getRouteFinder().getClass() != MipsManRouteFinder.class) {
                // only one MipsManRouteFinder will be created so if it exists then it can be
                // swapped
                if (mipsRoute != null) {
                    RouteFinder r = mipsman.getRouteFinder();
                    mipsman.setRouteFinder(mipsRoute.getRouteFinder());
                    mipsRoute.setRouteFinder(r);
                } else {
                    mipsman.setRouteFinder(new MipsManRouteFinder());
                }
            }
        }
    }

    /**
     * Runs the AI path-finding loop.
     *
     * @throws IllegalStateException
     *             Mipsman routefinder incorrectly given to ghost.
     */
    @Override
    public void run() {
        RouteFinder lastGhostRouteFinder = null;
        System.out.println("Starting AI loop...");
        while (runAILoop && (controlAgents.length > 0)) {
            Entity fixRouteFinder = null;
            // every AI entity
            for (Entity ent : controlAgents) {
                // positions must be set
                try {
                    lastGhostRouteFinder = updateRouteFinder(ent, lastGhostRouteFinder);
                } catch (NoRouteFinderException e) {
                    fixRouteFinder = ent;
                }
                // TODO only calculate new route if not at last coordinate OR current direction is invalid
                if (ent.getLocation() != null && !atPreviousCoordinate(ent) || !Methods.validiateDirection(ent.getDirection(), ent, map)){
                    ent.setLastGridCoord(Mapping.getGridCoord(ent.getLocation()));
                    // only route find on junctions
                    if (junctions.contains(Mapping.getGridCoord(ent.getLocation()))) {
                        executeRoute(ent);
                    }
                    else {
                        if (ent.getDirection()==null || !Methods.validiateDirection(ent.getDirection(), ent, map)) {
                            Point2D.Double nearestJunct = Mapping.findNearestJunction(ent.getLocation(), map, junctions);
                            try {
                                if (nearestJunct!=ent.getLocation()) {
                                    ent.setDirection(Mapping.directionBetweenPoints(ent.getLocation(), nearestJunct));
                                }
                                else {
                                    Direction dir = new RandomRouteFinder().getRoute(ent.getLocation(), controlAgents[mipsmanID].getLocation());
                                    while (!Methods.validiateDirection(dir,ent,map)) {
                                        dir = new RandomRouteFinder().getRoute(ent.getLocation(), controlAgents[mipsmanID].getLocation());
                                    }
                                    ent.setDirection(dir);
                                }
                            }
                            catch (NullPointerException e) {
                                System.out.println("Images not set");
                            }
                        }
                    }
                }
            }



            if (fixRouteFinder != null) {
                if (lastGhostRouteFinder != null) {
                    fixRouteFinder.setRouteFinder(lastGhostRouteFinder);
                } else {
                    throw new IllegalStateException("Mipsman routefinder incorrectly given to ghost.");
                }
            }

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                runAILoop = false;
            }
        }
    }

    private boolean atPreviousCoordinate(Entity ent) {
        return !(ent.getLastGridCoord()==null||!ent.getLastGridCoord().equals(Mapping.getGridCoord(ent.getLocation())));
    }

    /*
     * Updates the {@link RouteFinder} in the event that Mipsman is caught. The new
     * Mipsman needs to have the Mipsman {@link RouteFinder} and the old Mipsman
     * needs to have the new Mipsman's old {@link RouteFinder}.
     *
     * @param ent The entity having it's {@link RouteFinder} updated.
     *
     * @param lastGhostRouteFinder The {@link RouteFinder} that will replace this
     * {@link Entity}'s {@link RouteFinder} if this {@link Entity} is Mipsman.
     *
     * @throws NoRouteFinderException The value of lastGhostRouteFinder is null and
     * so the {@link Entity} that caught Mipsman has not yet been identified.
     */
    private RouteFinder updateRouteFinder(Entity ent, RouteFinder lastGhostRouteFinder) throws NoRouteFinderException {
        RouteFinder r = ent.getRouteFinder();
        // correct new Mipsman
        if (ent.isPacman() && !r.getClass().equals(MipsManRouteFinder.class)) {
            lastGhostRouteFinder = r;
            ent.setRouteFinder(new MipsManRouteFinder());
            mipsmanID = ent.getClientId();
        }
        // correct old Mipsman if possible, if not false is returned because this is the
        // first capture.
        if (!ent.isPacman() && r.getClass().equals(MipsManRouteFinder.class)) {
            if (lastGhostRouteFinder != null) {
                ent.setRouteFinder(lastGhostRouteFinder);
            } else {
                throw new NoRouteFinderException();
            }
        }
        return lastGhostRouteFinder;
    }

    /*
     * Executes the current {@link RouteFinder} and sets the next direction
     * instruction for the agent.
     */
    private void executeRoute(Entity ent) {
        RouteFinder r = ent.getRouteFinder();
        Point2D.Double myLoc = ent.getLocation();
        Point2D.Double mipsManLoc = controlAgents[mipsmanID].getLocation();
        Direction direction;
        direction = r.getRoute(myLoc, mipsManLoc);
        // re-process a random direction if an invalid move is detected
        while (!Methods.validiateDirection(direction, ent, map)) {
            direction = new RandomRouteFinder().getRoute(myLoc, mipsManLoc);
        }
        try {
            ent.setDirection(direction);
            directionsOut.add(new Input(ent.getClientId(), direction));
        }
        catch (NullPointerException e) {
            System.err.println("image file null in entity" + ent.getClientId());
        }
    }

    /**
     * Terminates the AI route finding loop upon completion of the current
     * iteration.
     *
     * @return True if the current thread is alive and so the AI can be terminated.
     */
    public boolean killAI() {
        runAILoop = false;
        if (isAlive()) {
            return true;
        }
        return false;
    }
}