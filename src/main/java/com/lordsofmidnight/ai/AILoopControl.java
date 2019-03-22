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

    private static final int POWER_UP_USE_PROBABILITY = 10;         //chance that a powerUp is used
    private static final int SPEED_POWER_UP_ACTIVATE_DEPTH = 20;    //distance at which the AI will use a speed boost when in proximity to MIPSMan
    private static final int INVINCIBILITY_AVOID_DISTANCE = 20;     //distance that will be searched in avoidance of an invincible agent
    private static final int INVINCIBILITY_PREFER_MULTIPLIER = 2;   //the preference given to the current direction
    private static final int OPPOSITE_DIRECTION_DIVISOR = 4;
    private static final long SLEEP_TIME = 1;

    private final ArrayList<Entity> controlAgents;
    private final PointSet junctions;
    private final PointMap<PointSet> edges;
    private final BlockingQueue<Input> directionsOut;
    private final Map map;
    private final Entity[] gameAgents;
    private final PointMap<Pellet> pellets;

    private ArrayList<Entity> newClient;
    private ArrayList<Entity> removeClient;

    private boolean runAILoop;
    private int mipsmanID;


    /**Initialises the object prior to the AI loop being executed.
     * @param gameAgents The complete set of all entities that are controlled (by AI or players) in the game.
     * @param controlIds The set of main Ids that the AI will control.
     * @param map The map the game is being played on.
     * @param directionsOut The {@link BlockingQueue}<{@link Input}> That processes all agent direction instructions.
     * @param pellets The {@link PointMap}<{@link Pellet}> that will hold all pellets in the current game.
     * @throws IllegalArgumentException gameAgent array contains duplicate main IDs.
     * @throws IllegalStateException Cannot have more than one mipsman.
     * @throws IllegalStateException The control ID does not match an agent main ID.
     * @author Lewis Ackroyd*/
    public AILoopControl(
            Entity[] gameAgents, int[] controlIds, Map map, BlockingQueue<Input> directionsOut, PointMap<Pellet> pellets) {
        validateAgents(gameAgents);
        this.setDaemon(true);
        this.runAILoop = true;
        this.gameAgents = gameAgents;
        this.controlAgents = new ArrayList<>();
        this.junctions = Mapping.getJunctions(map);
        this.edges = Mapping.getEdges(map, junctions);
        this.directionsOut = directionsOut;
        this.map = map;
        this.pellets = pellets;
        this.newClient = new ArrayList<>();
        this.removeClient = new ArrayList<>();
        assignControlEntities(controlIds);

        generateRouteFinders();
        correctMipsmanRouteFinder();
    }

    /**The AI execution loop*/
    @Override
    public void run() {
        System.out.println("Starting AI loop...");

        while (runAILoop) {
            for (Entity ent : controlAgents) {  //for all game agents
                Point currentLocation = ent.getLocation().getCopy();
                Point currentGridLocation = currentLocation.getGridCoord();
                if (currentLocation.isCentered()) { //only when in the centre of a grid square
                    boolean atLastCoord = atPreviousCoordinate(ent, currentGridLocation);
                    if (!ent.getDirection().isMovementDirection()   //direction is not a movement direction
                            || !Methods.validateDirection(ent.getDirection(), currentLocation, map) ||  //movement direction is no longer valid
                                (junctions.contains(currentGridLocation) && !atLastCoord)) {     //at a junction, but not the last coordinate
                        generateNewDirection(ent, currentLocation, currentGridLocation, atLastCoord);
                    }
                }
                processPowerUps(ent, currentGridLocation);
            }

            correctMipsmanRouteFinder();

            updateControlList();

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                runAILoop = false;
            }
        }
        System.out.println("AI safely terminated.");
    }

    /**Processes the list of clients to add and remove from AI control.*/
    private void updateControlList() {
        while (!newClient.isEmpty()) {
            Entity ent = newClient.remove(newClient.size()-1);
            boolean canAdd = true;
            for (Entity entity : controlAgents) {
                if (entity.getClientId()==ent.getClientId()) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                controlAgents.add(ent);
            }
        }
        while (!removeClient.isEmpty()) {
            Entity ent = removeClient.remove(removeClient.size()-1);
            for (Entity entity : controlAgents) {
                if (ent.getClientId()==entity.getClientId()) {
                    controlAgents.remove(ent);
                    break;
                }
            }
        }
    }

    /**Produces a new {@link Direction} for the specified entity and outputs this to the inputs queue*/
    private void generateNewDirection(Entity ent, Point currentLocation, Point currentGridLocation, boolean atLastCoord) {
        if (atLastCoord) {  //direction invalid, produce a random valid direction instruction
            Point nearestJunction = Mapping.findNearestJunction(currentLocation, map, junctions);
            Direction dir;
            if (!nearestJunction.equals(currentGridLocation)) { //go to nearest junction
                dir = Mapping.directionBetweenPoints(currentLocation, nearestJunction);
            } else {    //generate random direction to travel in from current location
                dir =
                        new RandomRouteFinder()
                                .getRoute(currentLocation,
                                        gameAgents[mipsmanID].getLocation());
            }
            dir = confirmOrReplaceDirection(ent.getDirection(), currentLocation, dir);  //validate direction
            setDirection(dir, ent);

        } else {
            ent.setLastGridCoord(currentGridLocation);      //prevents multiple directions being produced for the same grid coordinate
            if (junctions.contains(currentGridLocation)) {  //if the current grid coordinate is a junction
                executeRoute(ent, currentLocation);
            }
        }
    }

    /**Terminates the AI loop*/
    public boolean killAI() {
        runAILoop = false;
        return isAlive();
    }

    /**An array controlling all IDs of agents being controlled by the AI.
     * @return An array of IDs controlled by the AI.*/
    public int[] getControlledIDs() {
        int[] controlledIDs = new int[controlAgents.size()];
        for (int i = 0; i<controlAgents.size(); i++) {
            controlledIDs[i] = controlAgents.get(i).getClientId();
        }
        return controlledIDs;
    }

    /**Give control for the specified ID to the AI.
     * @param id The id of the game agent to be controlled.
     * @return True if control has been given to the AI.*/
    public boolean addClient(int id) {
        Entity entity = null;
        for (Entity ent : gameAgents) {
            if (ent.getClientId()==id) {
                entity = ent;
                break;
            }
        }
        if (entity==null) {
            return false;
        }
        for (Entity ent : controlAgents) {
            if (ent.getClientId()==id) {
                return false;
            }
        }
        newClient.add(entity);
        return true;
    }

    /**Remove control for the specified ID from the AI.
     * @param id The id of the game agent to have AI control removed from.
     * @return True if control is currently with the AI and will be removed. False if AI does not control the given ID.*/
    public boolean removeClient(int id) {
        for (Entity ent : controlAgents) {
            if (ent.getClientId()==id) {
                removeClient.add(ent);
                return true;
            }
        }
        return false;
    }

    /**Checks that the entities given to the AI all have unique IDs and that only one is mipsman.
     * @param gameAgents The complete set of all entities that are controlled (by AI or players) in the game.
     * @throws IllegalArgumentException gameAgent array contains duplicate main IDs.
     * @throws IllegalStateException Cannot have more than one mipsman.
     * @author Lewis Ackroyd*/
    private void validateAgents(Entity[] gameAgents)
            throws IllegalArgumentException, IllegalStateException {
        HashSet<Integer> ids = new HashSet<>();
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
                    routeFinder = new MipsManRouteFinder(pellets, gameAgents, map);
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
                    routeFinder = new PowerPelletPatrolRouteFinder(map, pellets);
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

    /**Initialises the {@link RouteFinder}s for every game agent, regardless of whether it is AI controlled or not.
     * All {@link RouteFinder}s are {@link RandomRouteFinder}.
     * @author Lewis Ackroyd*/
    private void generateEasyRouteFinders() {
        for (int i = 0; i < gameAgents.length; i++) {
            gameAgents[i].setRouteFinder(new RandomRouteFinder());
        }
    }

    /**Initialises the {@link RouteFinder}s for every game agent, regardless of whether it is AI controlled or not.
     * All {@link RouteFinder}s are {@link AStarRouteFinder}, with the exception of one {@link MipsManRouteFinder}.
     * @author Lewis Ackroyd*/
    private void generateHardRouteFinders() {
        for (int i = 0; i < gameAgents.length; i++) {
            RouteFinder routeFinder;
            switch (i) {
                case 0: {
                    routeFinder = new MipsManRouteFinder(pellets, gameAgents, map);
                    break;
                }
                default: {
                    routeFinder = new AStarRouteFinder(junctions, edges, map);
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
                    controlAgents.add(ent);
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
            }
            if (ent.getRouteFinder().getClass() == MipsManRouteFinder.class) {
                mipsmanRoute = ent;
            }
        }
        if (mipsman != null && mipsmanRoute != null) {
            mipsmanID = mipsmanRoute.getClientId();
            RouteFinder r = mipsman.getRouteFinder();
            mipsman.setRouteFinder(mipsmanRoute.getRouteFinder());
            mipsmanRoute.setRouteFinder(r);
        }
    }

    /**Executes the {@link RouteFinder} associated with the given {@link Entity}. Validates the {@link Direction} produced and then outputs a valid {@link Direction}.
     * @param ent The current {@link Entity} who's route is being calculated.
     * @param currentLocation The current location of this {@link Entity} when it began being processed.*/
    private void executeRoute(Entity ent, Point currentLocation) {
        RouteFinder r = ent.getRouteFinder();
        Point mipsManLoc = gameAgents[mipsmanID].getLocation();
        Direction direction = r.getRoute(currentLocation, mipsManLoc);
        direction = accountForPowerUps(currentLocation, direction);
        direction = confirmOrReplaceDirection(ent.getDirection(), currentLocation, direction);
        setDirection(direction, ent);
    }

    /**Adjusts the given {@link Direction} to allow the entity to account for any active {@link com.lordsofmidnight.utils.enums.PowerUp}s that warrant a course adjustment.
     * @param position The position of the {@link Entity}.
     * @param direction The current direction of travel.
     * @return The corrected direction.*/
    private Direction accountForPowerUps(Point position, Direction direction) {
        direction = invincibilityAdjust(position, direction);
        return direction;
    }

    private Direction invincibilityAdjust(Point position, Direction direction) {
        class InvincibleAgentCondition implements ConditionalInterface {
            @Override
            public boolean condition(Point position) {
                for (Entity ent : gameAgents) {
                    if (ent.getLocation().getGridCoord().equals(position.getGridCoord())) {
                        if (ent.isInvincible()) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        int[] directionValues = new SampleSearch(INVINCIBILITY_AVOID_DISTANCE, map).getDirectionCounts(position, new InvincibleAgentCondition());
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

    private void processPowerUps(Entity ent, Point currentLocation) {
        List<PowerUp> powerUpList = ent.getItems();
        if (!powerUpList.isEmpty()&&!ent.isPowerUpUsed()) {
            Random r = new Random();
            if (r.nextInt(POWER_UP_USE_PROBABILITY)>ent.powerUpUseAttempts()) {
                ent.setPowerUpUsedFlag(true);
                setDirection(Direction.USE, ent);
            }
            else {
                try {
                    if (powerUpList.get(0).getType() == com.lordsofmidnight.utils.enums.PowerUp.SPEED) {
                        class MipsmanProximityCondition implements ConditionalInterface {
                            @Override
                            public boolean condition(Point position) {
                                return position.equals(gameAgents[mipsmanID].getLocation());
                            }
                        }
                        SampleSearch sampleSearch = new SampleSearch(SPEED_POWER_UP_ACTIVATE_DEPTH, map);
                        int[] mipsmanProximities = sampleSearch.getDirectionCounts(currentLocation, new MipsmanProximityCondition());
                        for (int i : mipsmanProximities) {
                            if (i > 0) {
                                ent.setPowerUpUsedFlag(true);
                                setDirection(Direction.USE, ent);
                                break;
                            }
                        }
                    }
                    if (!ent.isPowerUpUsed()) {
                        ent.incrementPowerUpUseChance();
                    }
                }
                catch (NullPointerException e) {} //PowerUp removed whilst processing. Skip this cycle
                catch (IndexOutOfBoundsException e) {}
            }
        }
    }

    private void setDirection(Direction direction, Entity ent) {
        if (direction == null) {
            return;
        }
        if(direction!=ent.getDirection()&&!ent.isDirectionSet()){
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
            if (randI == 0) {
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

    private static final ArrayList<Direction> getValidDirections(Point p, Map map) {
        ArrayList<Direction> validDirections = new ArrayList<>();
        if (Methods.validateDirection(Direction.UP, p, map)) validDirections.add(Direction.UP);
        if (Methods.validateDirection(Direction.DOWN, p, map)) validDirections.add(Direction.DOWN);
        if (Methods.validateDirection(Direction.LEFT, p, map)) validDirections.add(Direction.LEFT);
        if (Methods.validateDirection(Direction.RIGHT, p, map)) validDirections.add(Direction.RIGHT);
        return validDirections;
    }
}
