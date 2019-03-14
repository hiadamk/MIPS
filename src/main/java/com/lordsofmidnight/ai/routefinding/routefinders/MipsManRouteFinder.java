package com.lordsofmidnight.ai.routefinding.routefinders;

import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;

import java.util.HashMap;

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
    return DEFAULT;
  }
}
