package server;

import javafx.animation.AnimationTimer;
import objects.Entity;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.enums.Direction;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Telemetry {
    
    private static final int AGENT_COUNT = 1;
    private BlockingQueue<Input> inputs;
    private BlockingQueue<Input> outputs;
    private Entity[] agents;
    private boolean singlePlayer;
    private Map map;
    private Queue<Input> clientQueue;
    private ServerGameplayHandler server;
    
    public Telemetry(Map map, ServerGameplayHandler server) {
        this.map = map;
        inputs = new LinkedBlockingQueue<>();
        outputs = new LinkedBlockingQueue<>();
        
        this.server = server;
        int aiCount = 5 - server.getPlayerCount();
        this.singlePlayer = false;
        if (aiCount > 0) {
            // Generate the AI to control each entity needed
        }
        agents = new Entity[AGENT_COUNT];
    
        agents[0] = new Entity(true, 0, new Double(1, 3));
        // agents[1] = new Entity(false, 1, new Double(1, 2));
        //agents[2] = new Entity(false, 2, new Double(1, 2));
        //agents[3] = new Entity(false, 3, new Double(1, 2));
        //agents[4] = new Entity(false, 4, new Double(1, 2));
    
        //startGame();
    }
    
    public Telemetry(Map map, Queue<Input> clientQueue) {
        this.map = map;
        inputs = new LinkedBlockingQueue<>();
        outputs = new LinkedBlockingQueue<>();
        singlePlayer = true;
        this.clientQueue = clientQueue;
    }
    
    /**
     * Static method for updating game state increments positions if valid, increments points, and
     * detects and treats entity collisions TODO: 30/1/19 increment points functionality
     *
     * @param agents array of entities in current state
     * @return array of entities in new state
     * @author Alex Banks, Matthew Jones
     * @see this#entityCollision(Entity, Entity, Double)
     */
    public static Entity[] processPhysics(Entity[] agents, Map m) {
        
        for (int i = 0; i < AGENT_COUNT; i++) {
            Point2D.Double tempLocation = new Point2D.Double(agents[i].getLocation().getX(),
                    agents[i].getLocation().getY());
            double offset = agents[i].getVelocity();
            if (agents[i].getDirection() == null) {
                continue;
            }
            switch (agents[i].getDirection()) {
                case RIGHT:
                  tempLocation
                      .setLocation((tempLocation.getX() + offset + m.getMaxX()) % m.getMaxX(),
                        tempLocation.getY());
                    break;
                case LEFT:
                  tempLocation
                      .setLocation((tempLocation.getX() - offset + m.getMaxX()) % m.getMaxX(),
                        tempLocation.getY());
                    break;
                case DOWN:
                    tempLocation.setLocation(tempLocation.getX(),
                        (tempLocation.getY() + offset + m.getMaxY()) % m.getMaxY());
                    break;
                case UP:
                    tempLocation.setLocation(tempLocation.getX(),
                        (tempLocation.getY() - offset + m.getMaxY()) % m.getMaxY());
                    break;
            }
            
            if (m.isWall(tempLocation)) {
                agents[i].setDirection(null);
                //agents[i].setVelocity(0);
                System.out.println("in wall");
                System.out.println(tempLocation);
                System.out.println(agents[i].getLocation());
                System.out.println(offset);
            } else {
                agents[i].setLocation(tempLocation);
                System.out.println("moved");
                System.out.println(offset);
            }
            
            // TODO add points for pellet collision
        }
        
        // separate loop for checking collision after iteration
    /*
    for (int i = 0; i < AGENT_COUNT; i++) {
      for (int j = i + 1; j < AGENT_COUNT; j++) {
          if ((int) agents[i].getLocation().getX() == (int) agents[j].getLocation().getX()
                  && (int) agents[i].getLocation().getY() == (int) agents[j].getLocation().getY()) {
          entityCollision(agents[i], agents[j], m.getSpawnPoint());
          System.out.println("collison");
        }
      }
    }
    */
        return agents;
    }
    
    /**
     * Static method for 'swapping' entities if they occupy the same square. Does nothing if both
     * entities are ghouls
     *
     * @param x            Entity one
     * @param y            Entity two
     * @param respawnPoint Point to relocate new ghoul too
     */
    private static void entityCollision(Entity x, Entity y, Double respawnPoint) {
        if (x.isPacman() && !y.isPacman()) {
            x.setPacMan(false);
            y.setPacMan(true);
            x.setLocation(respawnPoint);
            x.setDirection(Direction.UP);
        } else if (y.isPacman() && !y.isPacman()) {
            y.setPacMan(false);
            x.setPacMan(true);
            y.setLocation(respawnPoint);
            y.setDirection(Direction.UP);
        }
    }
    
    public Entity getEntity(int id) {
        return agents[id];
    }
    
    public void addInput(Input in) {
        inputs.add(in);
    }
    
    public void startGame() {
        // TODO implement
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                processInputs();
                informClients();
                agents = processPhysics(agents, map);
                updateClients();
            }
        }.start();
    }
    
    private void processInputs() {
        while (!inputs.isEmpty()) {
            Input input = inputs.poll();
            int id = input.getClientID();
            Direction d = input.getMove();
            if (Methods.validiateDirection(d, agents[id], map)) {
                agents[id].setDirection(d);
                outputs.add(input); //To send to the other clients
            }
        }
    }
    
    private void informClients() {
        while (!outputs.isEmpty()) {
            Input input = outputs.poll();
            if (singlePlayer) {
                clientQueue.add(input);
            } else {
                server.sendPacket(input);
            }
        }
    }
    
    private void updateClients() {
        // TODO implement
    }
}
