package com.lordsofmidnight.ai.routefinding.routefinders;

import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.ai.routefinding.SampleSearch;
import com.lordsofmidnight.ai.routefinding.routefinders.condition.ConditionalInterface;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.PointMap;
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

  private static final int DEPTH = 20;
  private PointMap<Pellet> pellets;
  private Entity[] gameAgents;
  private final Map map;

  public MipsManRouteFinder(PointMap<Pellet> pellets, Entity[] gameAgents, Map map) {
    this.pellets = pellets;
    this.gameAgents = gameAgents;
    this.map = map;
  }

  @Override
  public Direction getRoute(Point myLocation, Point targetLocation) {
    if (!COMPLETE) {
      return new RandomRouteFinder().getRoute(myLocation, targetLocation);
    }
    SampleSearch sampleSearch = new SampleSearch(DEPTH, map);
    class GhoulCountCondition implements ConditionalInterface {
      @Override
      public boolean condition(Point position) {
        for (Entity entity : gameAgents) {
          if (entity.getLocation().getGridCoord().equals(position)) {
            if (!entity.isMipsman()) {
              return true;
            }
          }
        }
        return false;
      }
    }
    int[] ghoulCounts = sampleSearch.getDirectionCounts(myLocation, new GhoulCountCondition());

    class PowerPelletCountCondition implements ConditionalInterface {
      @Override
      public boolean condition(Point position) {
        if (pellets.containsKey(position)) {
            Pellet pellet = pellets.get(position);
            if (pellet.isPowerPellet()) {
                return true;
            }
        }
        return false;
      }
    }
    return DEFAULT;
  }
}
