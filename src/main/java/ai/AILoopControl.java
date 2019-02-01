package ai;

import ai.mapping.Mapping;
import ai.routefinding.RandomRouteFinder;
import ai.routefinding.RouteFinder;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import objects.Entity;
import utils.Map;
import utils.enums.Direction;

public class AILoopControl extends Thread {

  private static final int SLEEP_TIME = 1;
  private final Entity[] controlAgents;
  private final HashSet<Point> junctions;
  private final HashMap<Point, HashSet<Point>> edges;
  private boolean runAILoop;
  private int pacmanId;

  /**
   * Initialises the control for the AI Control Loop.
   *
   * @param gameAgents All agents within the game.
   * @param controlIds The main ID of agents which will be AI controlled.
   * @param map The map on which route finding will occur.
   * @throws IllegalArgumentException gameAgent array contains duplicate client IDs.
   * @throws IllegalStateException Control ID does not match a gameAgent main ID.
   */
  public AILoopControl(Entity[] gameAgents, int[] controlIds, Map map) {
    HashSet<Integer> ids = new HashSet<Integer>();
    for (Entity e : gameAgents) {
      if (!ids.add(e.getClientId())) {
        throw new IllegalArgumentException("gameAgent array contains duplicate main IDs.");
      }
    }
    this.runAILoop = true;
    this.controlAgents = new Entity[controlIds.length];
    for (int i = 0; i < controlIds.length; i++) {
      RouteFinder r;
      switch (i) {
        case 0: {
          // TODO
          r = new RandomRouteFinder();
          r.setAgents(gameAgents, controlIds[i]);
          break;
        }
        case 1: {
          // TODO
          r = new RandomRouteFinder();
          r.setAgents(gameAgents, controlIds[i]);
          break;
        }
        case 2: {
          // TODO
          r = new RandomRouteFinder();
          r.setAgents(gameAgents, controlIds[i]);
          break;
        }
        case 3: {
          // TODO
          r = new RandomRouteFinder();
          r.setAgents(gameAgents, controlIds[i]);
          break;
        }
        case 4: {
          r = new RandomRouteFinder();
          r.setAgents(gameAgents, controlIds[i]);
          break;
        }
        default: {
          // TODO
          r = new RandomRouteFinder();
          r.setAgents(gameAgents, controlIds[i]);
          break;
        }
      }
      boolean agentFound = false;
      for (Entity ent : gameAgents) {
        if (ent.getClientId() == controlIds[i]) {
          controlAgents[i] = ent;
          ent.setRouteFinder(r);
          agentFound = true;
          break;
        }
      }
      if (!agentFound) {
        throw new IllegalStateException("A control ID does not match an agent main ID.");
      }
    }
    this.junctions = Mapping.getJunctions(map);
    this.edges = Mapping.getEdges(map, junctions);
  }

  /**
   * Runs the AI path-finding loop
   */
  public void run() {
    while (runAILoop && (controlAgents.length > 0)) {
      // every AI entity
      for (Entity ent : controlAgents) {
        Point2D absPos = ent.getLocation();
        // positions must be set
        if (absPos != null) {
          Point gridPos =
              new Point((int) Math.round(absPos.getX()), (int) Math.round(absPos.getY()));
          // only route find on junctions
          if (junctions.contains(gridPos)) {
            RouteFinder r = ent.getRouteFinder();
            Direction direction = r.getRoute(pacmanId);
            // re-process if an invalid move is detected
            while (!Mapping.validMove(gridPos, edges, direction)) {
              direction = r.getRoute(pacmanId);
            }
            ent.setDirection(direction);
          }
        }
      }

      try {
        Thread.sleep(SLEEP_TIME);
      } catch (InterruptedException e) {
        runAILoop = false;
      }
    }
  }

  /**
   * Terminates the AI route finding loop upon completion of the current iteration.
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
