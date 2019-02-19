package server;

import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.Map;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.Direction;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class DumbTelemetry implements Telemeters {
    
    private static final int AGENT_COUNT = 5;
    private BlockingQueue<String> inputs;
    private Entity[] agents;
    private Map map;
    private Queue<Input> clientQueue;
    private ResourceLoader resourceLoader;
    
    
    //dumb telemetry is like telemetry but it relies on information from the server to set it's entites
    //rather than using any AI.
    //it is the client's telemetry.
    public DumbTelemetry(Map map, Queue<String> inputQueue, ResourceLoader resourceLoader) {
        this.map = map;
        this.resourceLoader = resourceLoader;
        inputs = (BlockingQueue<String>) inputQueue;
        initialise();
        startGame();
    }
    
    /**
     * Static method for updating game state increments positions if valid, increments points, and
     * detects and treats entity collisions
     *
     * <p>TODO: increment points functionality
     *
     * @param agents array of entities in current state
     * @author Alex Banks, Matthew Jones
     * @see this#detectEntityCollision(Entity, Entity, ResourceLoader)
     */
    public static void processPhysics(Entity[] agents, Map m, ResourceLoader resourceLoader) {
        
        for (int i = 0; i < AGENT_COUNT; i++) {
            if (agents[i].getDirection() != null) {
                Point prevLocation = agents[i].getLocation();
                agents[i].move();
                Point faceLocation = agents[i].getFaceLocation();

//Dumb telemetry is allowed to process its own physics at the moment
                if (m.isWall(faceLocation)) {
                    System.err.println(i + "prev: " + prevLocation);
                    System.err.println(i + "face: " + faceLocation);
                    agents[i].setLocation(prevLocation);
                    agents[i].setDirection(null);
//        } else {
//          System.out.println(i + "face: " + faceLocation);
                }
            }
        }
        // TODO add points for pellet collision
        
        //dumb telemetry is calculating its own collisions
        // separate loop for checking collision after iteration
        
        for (int i = 0; i < AGENT_COUNT; i++) {
            for (int j = (i + 1); j < AGENT_COUNT; j++) {
                
                if (agents[i].isPacman() && !agents[j].isPacman()) {
                    detectEntityCollision(agents[i], agents[j], resourceLoader);
                }
                
                if (agents[j].isPacman() && !agents[i].isPacman()) {
                    detectEntityCollision(agents[j], agents[i], resourceLoader);
                }
            }
        }
    }
    
    /**
     * Static method for 'swapping' a pacman and ghoul if they occupy the same area.
     *
     * @param pacman Entity currently acting as pacman
     * @param ghoul  Entity currently running as ghoul
     * @author Alex Banks, Matthew Jones
     */
    private static void detectEntityCollision(
            Entity pacman, Entity ghoul, ResourceLoader resourceLoader) {
        Point pacmanCenter = pacman.getLocation();
        Point ghoulFace = ghoul.getFaceLocation();
        
        if (pacmanCenter.inRange(ghoulFace)) {
            
            pacman.setPacMan(false);
            ghoul.setPacMan(true);
            pacman.setLocation(resourceLoader.getMap().getRandomSpawnPoint());
            pacman.setDirection(Direction.UP);
            pacman.updateImages(resourceLoader);
            ghoul.updateImages(resourceLoader);
            
            System.out.println(
                    "ghoul " + ghoul.getClientId() + " collided with mipsman " + pacman.getClientId());
        }
    }
    
    private void initialise() {
        agents = new Entity[AGENT_COUNT];
        agents[0] = new Entity(false, 0, new Point(1.5, 2.5, map));
        agents[1] = new Entity(false, 1, new Point(1.5, 18.5, map));
        agents[2] = new Entity(false, 2, new Point(1.5, 16.5, map));
        agents[3] = new Entity(false, 3, new Point(11.5, 2.5, map));
        agents[4] = new Entity(false, 4, new Point(14.5, 11.5, map));
        agents[(new Random()).nextInt(AGENT_COUNT)].setPacMan(true);
    }
    
    public Map getMap() {
        return map;
    }
    
    public Entity getEntity(int id) {
        return agents[id];
    }
    
    
    private void startGame() {
        final long DELAY = 1000000;
        // TODO implement
        
        new AnimationTimer() {
            long change;
            long oldTime = System.nanoTime();
            
            @Override
            public void handle(long now) {
                change = now - oldTime;
                if (change >= DELAY) {
                    oldTime = now;
                    // System.out.println(change);
                    processInputs();
                    //  informClients(); process inputs informs clients
                    processPhysics(agents, map, resourceLoader);
                    //        } else {
                    //           System.out.println("skipped");
                }
            }
        }.start();
    }
    
    private void processInputs() {
        while (!inputs.isEmpty()) {
            String input = inputs.poll();
            switch (input.substring(0, 4)) { //looks at first 4 characters
                case "POS1":
                    setEntityMovement(input.substring(4));
                case "POS3":
                    setEntityPositions(input.substring(4));
                case NetworkUtility.STOP_CODE:
                    //TODO - add code for game end procedures down the game on the server end
                default:
                    throw new IllegalArgumentException();
            }
            
        }
    }
    
    //takes a packet string as defined in
    // NetworkUtility.makeEntitiesPositionPacket(Entity[])
//without the starting POSx code
    private void setEntityPositions(String s) {
        String[] positions = s.split("\\|");
        for (String position : positions) {
            String[] ls = position.split(":");
            int id = Integer.parseInt(ls[0]);
            Double x = Double.valueOf(ls[1]);
            Double y = Double.valueOf(ls[2]);
            agents[id].setLocation(new Point(x, y));
        }
    }
    
    //takes a packet string as defined in
    // NetworkUtility.makeEntityMovementPacket(Input, Point)
    //without the starting POSx code
    private void setEntityMovement(String s) {
        String[] ls = s.split("\\|");
        Input input = Input.fromString(ls[0]);
        int id = input.getClientID();
        double x = Double.valueOf(ls[1]);
        double y = Double.valueOf(ls[2]);
        agents[id].setLocation(new Point(x, y));
        agents[id].setDirection(input.getMove());
    }
    
    public void startAI() {
        //haha trick this does nothing.
        // shouldn't actually be called from client if this object exists
    }
    
    public Entity[] getAgents() {
        return agents;
    }
}
