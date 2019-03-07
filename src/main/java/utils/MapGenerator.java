package utils;

import java.util.Random;

public class MapGenerator {
  /*
   Rules:
   No unacessable parts
   Mirror'd shape
   Walls around the edge except a loop around
  */

  public static void main(String[] args) {

    int[][] test1 = {
        {1, 1, 1, 1, 1},
        {1, 0, 1, 1, 1},
        {1, 0, 1, 1, 1},
        {1, 0, 1, 1, 1},
        {1, 1, 1, 1, 1}
    };
    int[][] test2 = {
        {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1},
        {1, 0, 0, 0, 1},
        {1, 0, 0, 0, 1},
        {1, 1, 1, 1, 1}
    };
    int[][] test3 = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
        {1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
        {1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1},
        {1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
        {1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
        {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    if (validateMap(test1)) {
      System.out.println("test one true");
    }
    if (validateMap(test2)) {
      System.out.println("test two true");
    }
    if (validateMap(test3)) {
      System.out.println("test three true");
    }
    long[] diff = new long[1000];
    long t2;
    long t1;
    for (int c = 0; c < 1000; c++) {
      Random r = new Random();
      int x = 14 + r.nextInt(5) * 3;
      int half = 7 + r.nextInt(9) * 3;
      int y = half * 2 - 1;
      t1 = System.nanoTime();
      int[][] map = generateNewMap(x, y);
      t2 = System.nanoTime();
      diff[c] = t2 - t1;
      //System.out.println("****************");
      //for (int[] bit : map) {
      //  System.out.println(Arrays.toString(bit));
      //}
    }
    long avg = 0;
    for (long i : diff) {
      avg = avg + i;
    }
    avg = avg / 1000;
    System.out.println(avg);

  }

  public static int[][] newRandomMap(int x_factor, int y_factor) {
    //Run it on a new thread
    return generateNewMap(14 + 3 * x_factor, 14 + 3 * y_factor);
  }

  public static int[][] generateNewMap(int x, int y) {
    int[][] map = null;
    int c = 0;
    int half = (y + 1) / 2;
    while (!validateMap(map)) {
      //System.out.println("attempt " + c++);
      Random r = new Random();

      map = new int[x][y];
      for (int i = 0; i < x; i++) {
        for (int j = 0; j < y; j++) {
          map[i][j] = 1;
        }
      }
      for (int i = 1; i < x - 3; i += 3) {
        for (int j = 1; j < half - 2; j += 3) {
          map = apply(MapParts.getRandom(), map, i, j);
        }
      }

    }
    smoothDiagonals(map);
    for (int i = 1; i < map.length; i++) { //Reflects the map
      for (int j = 1; j < map[0].length / 2; j++) {
        map[i][map[0].length - j - 1] = map[i][j];
      }
    }
    addLoops(map);// Adds the loops round

    /*System.out.println("Map made ***************************************");
    for (int[] bit : map) {
      System.out.println(Arrays.toString(bit));
    }
    System.gc(); */
    return map;
  }

  private static void addLoops(int[][] map) {
    int x = map.length;
    int y = map[0].length;
    if (map[x / 2][1] == 0 && map[x / 2][y - 2] == 0) {
      map[x / 2][0] = 0;
      map[x / 2][y - 1] = 0;
    }
    if (map[1][y / 2] == 0 && map[x - 2][y / 2] == 0) {
      map[0][y / 2] = 0;
      map[x - 1][y / 2] = 0;
    }
  }

  public static boolean validateMap(int[][] map) {
    return map != null && checkConnected(map) && noDoubleLanes(map);
  }

  private static boolean checkConnected(int[][] map) {
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[0].length; y++) {
        if (map[x][y] == 0) {
          int[][] copy = new int[map.length][map[0].length];
          for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
              copy[i][j] = map[i][j];
            }
          }
          paint(copy, x, y);
          return !containsSpace(copy);
        }
      }
    }
    return false;
  }

  private static boolean noDoubleLanes(int[][] map) {
    for (int x = 1; x < map.length - 1; x++) {
      for (int y = 1; y < map[0].length - 1; y++) {
        if (map[x][y] == 0 && map[x + 1][y] == 0) {
          if (map[x][y + 1] == 0 && map[x + 1][y + 1] == 0) {
            return false;
          }
          if (map[x][y - 1] == 0 && map[x + 1][y - 1] == 0) {
            return false;
          }
        }
        if (map[x][y] == 0 && map[x][y + 1] == 0) {
          if (map[x + 1][y] == 0 && map[x + 1][y + 1] == 0) {
            return false;
          }
          if (map[x - 1][y] == 0 && map[x - 1][y + 1] == 0) {
            return false;
          }
        }
      }
    }
    return true;
  }

  private static void smoothDiagonals(int[][] map) {
    Random r = new Random();
    for (int x = 1; x < map.length - 1; x++) {
      for (int y = 1; y < map[0].length - 1; y++) {
        if (map[x][y] == 0 && map[x + 1][y + 1] == 0 && map[x + 1][y] == 1 && map[x][y + 1] == 1) {
          if (r.nextInt(1) == 0) {
            map[x + 1][y] = 0;
            if (!noDoubleLanes(map)) {
              map[x + 1][y] = 1;
              map[x][y + 1] = 0;
              if (!noDoubleLanes(map)) {
                map[x][y + 1] = 1;
              }
            }
          } else {
            map[x][y + 1] = 0;
            if (!noDoubleLanes(map)) {
              map[x][y + 1] = 1;
              map[x + 1][y] = 0;
              if (!noDoubleLanes(map)) {
                map[x + 1][y] = 1;
              }
            }
          }
        } else if (map[x][y] == 0 && map[x - 1][y + 1] == 0 && map[x][y + 1] == 1
            && map[x - 1][y] == 1) {
          if (r.nextInt(1) == 0) {
            map[x - 1][y] = 0;
            if (!noDoubleLanes(map)) {
              map[x - 1][y] = 1;
              map[x][y + 1] = 0;
              if (!noDoubleLanes(map)) {
                map[x][y + 1] = 1;
              }
            }
          } else {
            map[x][y + 1] = 0;
            if (!noDoubleLanes(map)) {
              map[x][y + 1] = 1;
              map[x - 1][y] = 0;
              if (!noDoubleLanes(map)) {
                map[x - 1][y] = 1;
              }
            }
          }
        }
      }
    }
  }
  private static boolean containsSpace(int[][] map) {
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[0].length; y++) {
        if (map[x][y] == 0) {
          return true;
        }
      }
    }
    return false;
  }

  private static void paint(int[][] map, int x, int y) {
    if (x == map.length || y == map[0].length || x < 0 || y < 0) {
      return;
    }
    if (map[x][y] == 1) {
      return;
    }
    map[x][y] = 1;
    paint(map, x + 1, y);
    paint(map, x - 1, y);
    paint(map, x, y - 1);
    paint(map, x, y + 1);
  }

  private static int[][] apply(int[][] part, int[][] map, int x, int y) {
    //System.out.println(part.length + " " + part[0].length + " " + map.length + " " + map[0].length + " " + x + " " + y);
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        map[x][y] = part[i][j];
        y++;
      }
      y -= 3;
      x++;
    }
    return map;
  }
}
