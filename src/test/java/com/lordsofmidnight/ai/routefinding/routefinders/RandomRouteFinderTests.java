package com.lordsofmidnight.ai.routefinding.routefinders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link RandomRouteFinder} class.
 *
 * @author Lewis Ackroyd
 */
class RandomRouteFinderTests {

  @Test
  void initialization() {
    @SuppressWarnings("unused")
    RandomRouteFinder rrf = new RandomRouteFinder();
  }

  @Test
  void getRouteValid() {
    RandomRouteFinder rrf = new RandomRouteFinder();
    Entity[] gas = {
        new Entity(true, 0, null),
        new Entity(false, 1, null),
        new Entity(false, 2, null),
        new Entity(false, 3, null),
        new Entity(false, 4, null)
    };
    for (int i = 0; i < gas.length; i++) {
      gas[i].setLocation(0, i);
    }
    rrf.getRoute(gas[1].getLocation(), gas[0].getLocation());
  }

  @Test
  void directionProbabilities() {
    RandomRouteFinder rrf = new RandomRouteFinder();
    Entity[] gas = {
        new Entity(true, 0, null),
        new Entity(false, 1, null),
        new Entity(false, 2, null),
        new Entity(false, 3, null),
        new Entity(false, 4, null)
    };

    for (int i = 0; i < gas.length; i++) {
      gas[i].setLocation(i, i);
    }

    HashMap<Direction, Integer> counters = new HashMap<Direction, Integer>();
    counters.put(Direction.DOWN, 0);
    counters.put(Direction.UP, 0);
    counters.put(Direction.LEFT, 0);
    counters.put(Direction.RIGHT, 0);
    for (int count = 0; count < 100000; count++) {
      Direction dir = rrf.getRoute(gas[1].getLocation(), gas[0].getLocation());
      counters.put(dir, counters.get(dir) + 1);
    }

    boolean vertical = (counters.get(Direction.UP) > ((counters.get(Direction.DOWN) - 1000) * 2));
    boolean horizontal =
        counters.get(Direction.LEFT) > ((counters.get(Direction.RIGHT) - 1000) * 2);
    assertTrue(vertical);
    assertTrue(horizontal);
  }
}
