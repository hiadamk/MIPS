package shared;

import java.awt.Point;

import ai.routefinding.RandomRouteFinder;
import ai.routefinding.RouteFinder;
import shared.enums.GameAgentEnum;

public class GameAgent {
	private final GameAgentEnum id;
	private RouteFinder rf;
	private Point currentPosition;
	
	public GameAgent(GameAgentEnum identifier, Point startPosition) {
		this.id = identifier;
		this.currentPosition = startPosition;
		switch (this.id) {
		case MIPSMAN: {
			
		}
		case EINY: {
			this.rf = new RandomRouteFinder();
		}
		case DOSY: {
			
		}
		case SANY: {
			
		}
		case FOURY:{
			
		}
		}
	}
	
	public Point getCurrentPosition() {
		return currentPosition;
	}
	
	
}
