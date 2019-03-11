package ai.routefinding.routefinders;

import ai.routefinding.RouteFinder;
import objects.Entity;
import objects.Pellet;
import utils.Point;
import utils.enums.Direction;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * Route finding algorithm that controls Mipsman. Will aim to reach the nearest pellet whilst
 * avoiding any ghouls.
 *
 * @author Lewis Ackroyd
 */
public class MipsManRouteFinder implements RouteFinder {

  private static final boolean COMPLETE = false;

  private HashMap<String, Pellet> pellets;
  private Entity[] gameAgents;

  public MipsManRouteFinder(HashMap<String, Pellet> pellets, Entity[] gameAgents) {
    this.pellets = pellets;
    this.gameAgents = gameAgents;
  }

  @Override
  public Direction getRoute(Point myLocation, Point targetLocation) {
    if (!COMPLETE) {
      return new RandomRouteFinder().getRoute(myLocation, targetLocation);
    }
    //BlockingQueue<Point>
    return null;
  }
}
