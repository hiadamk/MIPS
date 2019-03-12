package ai.routefinding;

import utils.Point;

public class AStarData {

  private final Point myPosition;
  private final Point parent;
  private final double moveCost;
  private final double estimatedCost;

  public AStarData(Point myPosition, Point parent, double moveCost, double estimatedCost) {
    this.myPosition = myPosition;
    this.parent = parent;
    this.moveCost = moveCost;
    this.estimatedCost = estimatedCost;
  }

  public Point getMyPosition() {
    return myPosition;
  }

  public Point getParentPosition() {
    return parent;
  }

  public double getMoveCost() {
    return moveCost;
  }

  public double getEstimatedCost() {
    return estimatedCost;
  }
}
