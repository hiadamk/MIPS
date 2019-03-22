package com.lordsofmidnight.ai;

import com.lordsofmidnight.ai.mapping.Mapping;
import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.ai.routefinding.SampleSearch;
import com.lordsofmidnight.ai.routefinding.routefinders.*;
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
    private static final int INVINCIBILITY_PREFER_MULTIPLIER = 2;   //the preference given to the current direction of travel when avoiding invincible agents
    private static final int OPPOSITE_DIRECTION_DIVISOR = 4;        //the probability as 1/OPPOSITE_DIRECTION_DIVISOR of travelling in the opposite direction to previous direciton of travel
    private static final long SLEEP_TIME = 1;                       //how long to sleep between full AI agent cycle

    private final ArrayList<Entity> controlAgents;      //agents controlled by AI
    private final PointSet junctions;                   //all the junctions on the map, including 90 degree corners and dead ends
    private final PointMap<PointSet> edges;             //all connedtions between directly adjacent junctions on the map, not uncluding loops around the map boundaries
    private final BlockingQueue<Input> directionsOut;   //output queue for game instructions
    private final Map map;                              //the map being played on
    private final Entity[] gameAgents;                  //all agents present in the game
    private final PointMap<Pellet> pellets;             //the locations of all pellets in the game

    private ArrayList<Entity> newClient;                //list of clients to be given AI control when the current full AI agent cycle completes
    private ArrayList<Entity> removeClient;             //list of clients to have AI control removed when the current full AI agent cycle completes

    private boolean runAILoop;      //will run the AI loop until false
    private Entity mipsman;          //the index of mipsmanID in the gameAgents array


    /**Initialises the object prior to the AI loop being executed.
     *
     * @param gameAgents The complete set of all entities that are controlled (by AI or players) in the game.
     * @param controlIds The set of main Ids that the AI will control.
     * @param map The map the game is being played on.
     * @param directionsOut The {@link BlockingQueue}<{@link Input}> That processes all agent direction instructions.
     * @param pellets The {@link PointMap}<{@link Pellet}> that will hold all pellets in the current game.
     *
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

    /**The AI execution loop
     * @author Lewis Ackroyd*/
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

    /**Terminates the AI loop
     * @author Lewis Ackroyd*/
    public boolean killAI() {
        runAILoop = false;
        return isAlive();
    }

    /**An array controlling all IDs of agents being controlled by the AI.
     *
     * @return An array of IDs controlled by the AI.
     * @author Lewis Ackroyd*/
    public int[] getControlledIDs() {
        int[] controlledIDs = new int[controlAgents.size()];
        for (int i = 0; i<controlAgents.size(); i++) {
            controlledIDs[i] = controlAgents.get(i).getClientId();
        }
        return controlledIDs;
    }

    /**Give control for the specified ID to the AI.
     *
     * @param id The id of the game agent to be controlled.
     *
     * @return True if control has been given to the AI.
     * @author Lewis Ackroyd*/
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
     *
     * @param id The id of the game agent to have AI control removed from.
     *
     * @return True if control is currently with the AI and will be removed. False if AI does not control the given ID.
     * @author Lewis Ackroyd*/
    public boolean removeClient(int id) {
        for (Entity ent : controlAgents) {
            if (ent.getClientId()==id) {
                removeClient.add(ent);
                return true;
            }
        }
        return false;
    }

    /**Processes the list of clients to add and remove from AI control.
     * @author Lewis Ackroyd*/
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

    /**Produces a new {@link Direction} for the specified entity and outputs this to the inputs queue.
     *
     * @param ent The entity currently being processed
     * @param currentLocation The absolute position of the given {@link Entity} at the start of processing
     * @param currentGridLocation The grid position of the given {@link Entity} at the start of processing
     * @param atLastCoord If the {@link Entity} is at the same coordinate as the last time a route was calculated
     * @author Lewis Ackroyd*/
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
                                        mipsman.getLocation());
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

    /**Checks that the entities given to the AI all have unique IDs and that only one is MIPsman.
     *
     * @param gameAgents The complete set of all entities that are controlled (by AI or players) in the game.
     *
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
     *
     * @param controlIds The IDs of all agents to be controlled by the AI.
     *
     * @throws IllegalStateException The control ID does not match an agent main ID.
     * @author Lewis Ackroyd*/
    private void assignControlEntities(int[] controlIds) throws IllegalStateException {
        for (int i = 0; i < controlIds.length; i++) {
            boolean agentNotFound = true;
            for (Entity ent : gameAgents) {
                if (ent.getClientId() == controlIds[i]) {
                    if (i==0) {
                        mipsman = ent;  //default to having a mipsman which will be corrected later
                    }
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

    /**Corrects the {@link RouteFinder}s after a collision between mipsman and a ghoul.
     * @author Lewis Ackroyd*/
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
            this.mipsman = mipsmanRoute;
            RouteFinder r = mipsman.getRouteFinder();
            mipsman.setRouteFinder(mipsmanRoute.getRouteFinder());
            mipsmanRoute.setRouteFinder(r);
        }
    }

    /**Executes the {@link RouteFinder} associated with the given {@link Entity}. Validates the {@link Direction} produced and then outputs a valid {@link Direction}.
     *
     * @param ent The current {@link Entity} who's route is being calculated.
     * @param currentLocation The current location of this {@link Entity} when it began being processed.
     * @author Lewis Ackroyd*/
    private void executeRoute(Entity ent, Point currentLocation) {
        RouteFinder r = ent.getRouteFinder();
        Point mipsManLoc = mipsman.getLocation();
        Direction direction = r.getRoute(currentLocation, mipsManLoc);
        direction = accountForPowerUps(currentLocation, direction);
        direction = confirmOrReplaceDirection(ent.getDirection(), currentLocation, direction);
        setDirection(direction, ent);
    }

    /**Adjusts the given {@link Direction} to allow the entity to account for any active {@link com.lordsofmidnight.utils.enums.PowerUp PowerUp}s that warrant a course adjustment.
     *
     * @param position The position of the {@link Entity}.
     * @param direction The current direction of travel.
     *
     * @return The corrected direction.
     * @author Lewis Ackroyd*/
    private Direction accountForPowerUps(Point position, Direction direction) {
        direction = invincibilityAdjust(position, direction);
        return direction;
    }

    /**Determines if any of the agents in the near vicinity have invincibility powerup active and will try to avoid them.
     *
     * @param position The current position
     * @param direction The direction that will be travelled in next currently
     *
     * @return The adjusted direction.
     * @author Lewis Ackroyd*/
    private Direction invincibilityAdjust(Point position, Direction direction) {
        class InvincibleAgentCondition implements SampleSearch.ConditionalInterface {
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

    /**Regenerates the direction to be travelled based on all possible directions, but not avoidDirection (if alternatives are available) and weighted towards preferDirection.
     *
     * @param avoidDirection The direction that will not be travelled in unless no other alternatives are available.
     * @param preferDirection The direction which will have the highest weight of being chosen.
     * @param currentLoc The current position.
     *
     * @return The re-rolled direction.
     * @author Lewis Ackroyd*/
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

    /**Will use the current {@link Entity Entities} {@link com.lordsofmidnight.utils.enums.PowerUp PowerUp} with probability of 1/10 at first attempted use, increasing by 1/10 in probability for every consecutive attempted use. Will use {@link com.lordsofmidnight.utils.enums.PowerUp#SPEED Speed PowerUp} regardless if within {@link #SPEED_POWER_UP_ACTIVATE_DEPTH} squares of MIPsman.
     *
     * @param ent The current entity who's {@link com.lordsofmidnight.utils.enums.PowerUp PowerUp} is being processed.
     * @param currentLocation The current location.
     * @author Lewis Ackroyd*/
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
                        class MipsmanProximityCondition implements SampleSearch.ConditionalInterface {
                            @Override
                            public boolean condition(Point position) {
                                return position.equals(mipsman.getLocation());
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

    /**Adds the specified direction to be processed by the server.
     *
     * @param direction The direction to be moved in.
     * @param ent The entity the direction is associated with.
     * @author Lewis Ackroyd*/
    private void setDirection(Direction direction, Entity ent) {
        if (direction == null) {
            return;
        }
        if(direction!=ent.getDirection()&&!ent.isDirectionSet()){
            ent.setDirectionSetFlag(true);
            directionsOut.add(new Input(ent.getClientId(), direction));
        }
    }

    /**Determines if the current {@link Entity} is at the same coordinate as the last time it was checked in the AI loop.
     *
     * @param ent The current entity.
     * @param currentLocation The location of this {@link Entity} at the start of this AI process iteration.
     *
     * @return True if this {@link Entity} is at the same grid coordinate as in the last AI iteration.
     * @author Lewis Ackroyd*/
    private boolean atPreviousCoordinate(Entity ent, Point currentLocation) {
        if (ent.getLastGridCoord() == null) {
            return false;
        }
        return ent.getLastGridCoord().equals(currentLocation);
    }

    /**Checks if the given direction is valid, and if not replaces it with a random valid direction. Also reduces the liklihood of choosing a direction that would result in a 180 by the current agent.
     *
     * @param oldDirection The direction previously travelled by this {@link Entity}.
     * @param currentLocation The location of this {@link Entity} at the start of this AI process iteration.
     * @param dir The current direction to be set next.
     *
     * @return The corrected direction.
     * @author Lewis Ackroyd*/
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

    /**Produces all valid directions from the current position.
     *
     * @param p The current position.
     * @param map The current map.
     *
     * @return The list of all valid directions.
     * @author Lewis Ackroyd*/
    private static final ArrayList<Direction> getValidDirections(Point p, Map map) {
        ArrayList<Direction> validDirections = new ArrayList<>();
        if (Methods.validateDirection(Direction.UP, p, map)) validDirections.add(Direction.UP);
        if (Methods.validateDirection(Direction.DOWN, p, map)) validDirections.add(Direction.DOWN);
        if (Methods.validateDirection(Direction.LEFT, p, map)) validDirections.add(Direction.LEFT);
        if (Methods.validateDirection(Direction.RIGHT, p, map)) validDirections.add(Direction.RIGHT);
        return validDirections;
    }
}
