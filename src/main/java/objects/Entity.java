package objects;


import server.Server;
import utils.enums.Direction;
import utils.enums.EntityType;

import java.awt.geom.Point2D;

public class Entity {
	
	private Point2D.Double location;
	private double velocity;
	private Direction direction;
	private int score;
	private int clientId;
	private Server server;
	private EntityType type;
	
	public Point2D.Double getLocation() {
		return location;
	}
	
	public void setLocation(Point2D.Double location) {
		this.location=location;
	}
	
	public double getVelocity() {
		return velocity;
	}
	
	public void setVelocity(double velocity) {
		this.velocity=velocity;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void setDirection(Direction direction) {
		this.direction=direction;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score=score;
	}
	
	public int getClientId() {
		return clientId;
	}
	
	public EntityType getType() {
		return type;
	}
	
	public void setType(EntityType type) {
		this.type=type;
	}
	
	public Entity(EntityType type, int clientId, Server server) {
		this.type=type;
		this.clientId=clientId;
		this.server=server;
		this.score=0;
		this.velocity=0;
	}
}
