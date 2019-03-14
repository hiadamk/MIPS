package com.lordsofmidnight.gamestate.maps;

import java.util.ArrayList;
import java.util.Random;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.enums.MapElement;

/**
 * Encapsulates map with utilities methods
 *
 * @author Alex Banks
 */
public class Map {

  private final int MAX_X;
  private final int MAX_Y;

  private final int[][] MAP;
  private final ArrayList<Point> SPAWN_POINTS;

  /**
   * basic constructor that takes raw ints and performs preprocessing
   *
   * @param map_ 2d array of ints
   * @see this#getRandomSpawnPoint()
   * @see ResourceLoader#loadMap(String)
   */
  public Map(int[][] map_) {
    MAP = map_;
    MAX_X = MAP.length;
    MAX_Y = MAP[0].length;
    SPAWN_POINTS = loadSpawnPoints();
  }

  /**
   * called on construction to find and load spawn point TODO: convert back to SPAWNPOINT.toInt()
   *
   * @return Array of Point2D.Double spawnPoint.
   * @see this#Map(int[][])
   */
  private ArrayList<Point> loadSpawnPoints() {
    ArrayList<Point> spawnPoints = new ArrayList<>();
    for (int i = 0; i < MAX_X; i++) {
      for (int j = 0; j < MAX_Y; j++) {
        if (MAP[i][j] != MapElement.WALL.toInt()) { // SPAWNPOINT.toInt()
          spawnPoints.add(new Point(i, j, this).centralise());
        }
      }
    }
    return spawnPoints;
  }

  public int getMaxX() {
    return MAX_X;
  }

  public int getMaxY() {
    return MAX_Y;
  }

  /**
   * calculates if point is out of bounds
   *
   * @param point com.lordsofmidnight.gamestate to be checked, assumed to be in range
   * @return true if wall, false otherwise
   */
  public boolean isWall(Point point) {
    Point p =
        new Point(point.getX(), point.getY(), this); // TODO remove line once all points use mod
    return MAP[(int) p.getX()][(int) p.getY()] == MapElement.WALL.toInt();
  }

  /**
   * package method for raw processing
   *
   * @return 2D Array of ints
   */
  public int[][] raw() {
    return MAP;
  }

  /**
   * @return random com.lordsofmidnight.gamestate to position ghouls at if caught
   */
  public Point getRandomSpawnPoint() {
    return SPAWN_POINTS.get((new Random()).nextInt(SPAWN_POINTS.size()));
  }
}
