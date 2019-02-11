package ai.routefinding;

import java.awt.geom.Point2D;

public class AStarData {
	private final Point2D.Double myPosition;
	private final Point2D.Double parent;
	private final double cost;
	
	public AStarData(Point2D.Double myPosition, Point2D.Double parent, double cost) {
		this.myPosition = myPosition;
		this.parent = parent;
		this.cost = cost;
	}
	
	public Point2D.Double getMyPosition() {
		return myPosition;
	}
	
	public Point2D.Double getParentPosition() {
		return parent;
	}
	
	public double getCost() {
		return cost;
	}
}
