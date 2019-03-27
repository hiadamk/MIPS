package com.lordsofmidnight.gamestate.maps;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.renderer.ResourceLoader;
import com.lordsofmidnight.utils.enums.MapElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
   * @see this#getRandomSpawnPoint(Entity[])
   * @see ResourceLoader#loadMap(String)
   */
  public Map(int[][] map_) {
    MAP = map_;
    MAX_X = MAP.length;
    MAX_Y = MAP[0].length;
    SPAWN_POINTS = loadSpawnPoints();
  }

  /**
   * If the given {@link Point} is within the bounds of the {@link Map}.
   *
   * @param map The map being checked
   * @param point The point being checked
   * @return True if within the bounds of the map
   * @author Lewis Ackroyd
   */
  public static boolean withinBounds(Map map, Point point) {
    return withinBounds(map.getMaxX(), map.getMaxY(), point);
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
   * If the given {@link Point} is within the bounds of the {@link Map}.
   *
   * @param maxX The upper limit of the x-axis
   * @param maxY The upper limit of the y-axis
   * @param point The point being checked
   * @return True if within the bounds of the map
   * @author Lewis Ackroyd
   */
  public static boolean withinBounds(int maxX, int maxY, Point point) {
    boolean x = point.getX() >= 0 && point.getX() < maxX;
    boolean y = point.getY() >= 0 && point.getY() < maxY;
    return x && y;
  }

  /**
   * @param map The map to be serialised
   * @return a serialised map String * @author Tim Cheung
   */
  public static String serialiseMap(Map map) {
    String serializedMap = "";
    int[][] rawMap = map.raw();
    for (int[] row : rawMap) {
      for (int cell : row) {
        serializedMap += cell + serializedMapDelimiters.CELL_SEPARATOR.delimiter;
      }
      serializedMap = serializedMap.substring(0, serializedMap.length() - 1);
      serializedMap += serializedMapDelimiters.ROW_END.delimiter;
    }
    serializedMap = serializedMap.substring(0, serializedMap.length() - 1);
    return serializedMap;
  }

  /**
   * @param serializedMap The map to be deserialised
   * @return a deserialised map Object * @author Tim Cheung
   */
  public static Map deserialiseMap(String serializedMap) {
    String[] rows = serializedMap.split(serializedMapDelimiters.ROW_END.delimiter);
    ArrayList<ArrayList<String>> stringMap = new ArrayList<>();
    for (String row : rows) {
      stringMap.add(
          new ArrayList<>(
              Arrays.asList(row.split(serializedMapDelimiters.CELL_SEPARATOR.delimiter))));
    }

    int[][] deserialisedMap = new int[stringMap.size()][stringMap.get(0).size()];

    for (int i = 0; i < deserialisedMap.length; i++) {
      for (int j = 0; j < deserialisedMap[i].length; j++) {
        deserialisedMap[i][j] = Integer.parseInt(stringMap.get(i).get(j));
      }
    }
    return new Map(deserialisedMap);
  }

  /**
   * Returns random spawnpoint that's not near any other entities
   *
   * @return random Point to position entities upon respawn
   * @author Alex Banks
   */
  public Point getRandomSpawnPoint(Entity[] agents) {
    final int MIN_DIST = 2;

    boolean found = false;
    Point p = null;
    while (!found) {
      p = SPAWN_POINTS.get((new Random()).nextInt(SPAWN_POINTS.size()));
      found = true;
      for (Entity agent : agents) {
        if (agent == null) {
          continue;
        }
        if (agent.getLocation().distance(p) < MIN_DIST) {
          found = false;
        }
      }
    }

    return p;
  }

  @Override
  public boolean equals(Object _map) {
    if (!(_map instanceof Map)) {
      return false;
    }
    return Arrays.deepEquals(this.MAP, ((Map) _map).raw());
  }

  private enum serializedMapDelimiters {
    ROW_END("-"),
    CELL_SEPARATOR("&");

    private final String delimiter;

    serializedMapDelimiters(String delimiter) {
      this.delimiter = delimiter;
    }
  }
}
