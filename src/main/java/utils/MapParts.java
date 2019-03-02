package utils;

import java.util.Random;

public class MapParts {

  protected static final int[][] upL = {
      {0, 1, 1},
      {0, 1, 1},
      {0, 1, 1}
  };
  protected static final int[][] upM = {
      {1, 0, 1},
      {1, 0, 1},
      {1, 0, 1}
  };
  protected static final int[][] upR = {
      {1, 1, 0},
      {1, 1, 0},
      {1, 1, 0}
  };
  protected static final int[][] upDouble = {
      {0, 1, 0},
      {0, 1, 0},
      {0, 1, 0}
  };
  protected static final int[][] sideT = {
      {0, 0, 0},
      {1, 1, 1},
      {1, 1, 1}
  };
  protected static final int[][] sideM = {
      {1, 1, 1},
      {0, 0, 0},
      {1, 1, 1}
  };
  protected static final int[][] sideB = {
      {1, 1, 1},
      {1, 1, 1},
      {0, 0, 0}
  };
  protected static final int[][] cornerTR = {
      {0, 0, 0},
      {0, 1, 1},
      {0, 1, 1}
  };
  protected static final int[][] cornerBR = {
      {1, 1, 0},
      {1, 1, 0},
      {0, 0, 0}
  };
  protected static final int[][] cornerBL = {
      {0, 1, 1},
      {0, 1, 1},
      {0, 0, 0}
  };
  protected static final int[][] cornerTL = {
      {0, 0, 0},
      {0, 1, 1},
      {0, 1, 1}
  };
  protected static final int[][] cross = {
      {1, 0, 1},
      {0, 1, 0},
      {1, 0, 1}
  };
  protected static final int[][][] all = {upL, upM, upR, upDouble, sideT, sideM, sideB, cornerBL,
      cornerBR, cornerTL, cornerTR, cross};

  protected static int[][] getRandom() {
    Random r = new Random();
    return all[r.nextInt(all.length)];
  }
}
