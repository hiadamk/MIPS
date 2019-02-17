package ai.routefinding;

import utils.Point;

public class AStarData {

	private final Point myPosition;
	private final Point parent;
	private final double cost;

	public AStarData(Point myPosition, Point parent, double cost) {
		this.myPosition = myPosition;
		this.parent = parent;
		this.cost = cost;
	}

	public Point getMyPosition() {
		return myPosition;
	}

	public Point getParentPosition() {
		return parent;
	}

	public double getCost() {
		return cost;
	}
}
