package ai;

import ai.mapping.Mapping;
import ai.routefinding.MipsManRouteFinder;
import ai.routefinding.NoRouteFinderException;
import ai.routefinding.RandomRouteFinder;
import ai.routefinding.RouteFinder;
import objects.Entity;
import utils.Map;
import utils.enums.Direction;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;

public class AILoopControl extends Thread {

	private static final long SLEEP_TIME = 10;
	private final Entity[] controlAgents;
	private final HashSet<Point> junctions;
	private final HashMap<Point, HashSet<Point>> edges;
	private boolean runAILoop;
	private int pacmanId;

	/**
	 * Initialises the control for the AI Control Loop.
	 *
	 * @param gameAgents
	 *            All agents within the game.
	 * @param controlIds
	 *            The main ID of agents which will be AI controlled.
	 * @param map
	 *            The map on which route finding will occur.
	 * @throws IllegalArgumentException
	 *             gameAgent array contains duplicate client IDs.
	 * @throws IllegalStateException
	 *             Control ID does not match a gameAgent main ID.
	 */
	public AILoopControl(Entity[] gameAgents, int[] controlIds, Map map) {
		HashSet<Integer> ids = new HashSet<Integer>();
		for (Entity e : gameAgents) {
			if (!ids.add(e.getClientId())) {
				throw new IllegalArgumentException("gameAgent array contains duplicate main IDs.");
			}
		}
		this.runAILoop = true;
		this.controlAgents = new Entity[controlIds.length];
		for (int i = 0; i < controlIds.length; i++) {
			RouteFinder r;
			switch (i) {
			case 0: {
				// TODO
				r = new RandomRouteFinder();
				break;
			}
			case 1: {
				// TODO
				r = new RandomRouteFinder();
				break;
			}
			case 2: {
				// TODO
				r = new RandomRouteFinder();
				break;
			}
			case 3: {
				// TODO
				r = new RandomRouteFinder();
				break;
			}
			case 4: {
				r = new RandomRouteFinder();
				break;
			}
			default: {
				r = new RandomRouteFinder();
				break;
			}
			}
			boolean agentFound = false;
			for (Entity ent : gameAgents) {
				if (ent.getClientId() == controlIds[i]) {
					controlAgents[i] = ent;
					ent.setRouteFinder(r);
					agentFound = true;
					break;
				}
			}
			if (!agentFound) {
				throw new IllegalStateException("A control ID does not match an agent main ID.");
			}
		}
		this.junctions = Mapping.getJunctions(map);
		this.edges = Mapping.getEdges(map, junctions);
	}

	/**
	 * Runs the AI path-finding loop
	 */
	public void run() {
		RouteFinder lastGhostRouteFinder = null;
		while (runAILoop && (controlAgents.length > 0)) {
			ArrayBlockingQueue<Entity> fixRouteFinder = new ArrayBlockingQueue<Entity>(1);
			// every AI entity
			for (Entity ent : controlAgents) {
				// positions must be set
				if (ent.getLocation() != null) {
					// only route find on junctions
					if (junctions.contains(Mapping.point2DtoPoint(ent.getLocation()))) {
						try {
							updateRouteFinder(ent, lastGhostRouteFinder);
							executeRoute(ent);
						}
						catch (NoRouteFinderException e) {
							fixRouteFinder.add(ent);
						}
					}
				}
			}

			while (!fixRouteFinder.isEmpty()) {
				Entity ent = fixRouteFinder.remove();
				if (lastGhostRouteFinder != null) {
					ent.setRouteFinder(lastGhostRouteFinder);
				} else {
					throw new IllegalStateException("Mipsman routefinder incorrectly given to ghost.");
				}
			}

			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				runAILoop = false;
			}
		}
	}

	/*
	 * Updates the routefinder in the event that Mipsman is caught. The new Mipsman
	 * needs to have the Mipsman routefinder and the old Mipsman needs to have the
	 * new Mipsman's old routefinder.
	 */
	private RouteFinder updateRouteFinder(Entity ent, RouteFinder lastGhostRouteFinder) throws NoRouteFinderException {
		RouteFinder r = ent.getRouteFinder();
		//correct new Mipsman
		if (ent.isPacman() && !r.getClass().equals(MipsManRouteFinder.class)) {
			lastGhostRouteFinder = r;
			ent.setRouteFinder(new MipsManRouteFinder());
		}
		//correct old Mipsman if possible, if not fase is returned because this is the first capture.
		if (!ent.isPacman() && r.getClass().equals(MipsManRouteFinder.class)) {
			if (lastGhostRouteFinder != null) {
				ent.setRouteFinder(lastGhostRouteFinder);
			} else {
				throw new NoRouteFinderException();
			}
		}
		return lastGhostRouteFinder;
	}

	/*Executes the current routefinder and sets the next direction instruction for the agent.*/
	private void executeRoute(Entity ent) {
		RouteFinder r = ent.getRouteFinder();
		Point myLoc = Mapping.point2DtoPoint(ent.getLocation());
		Point mipsManLoc = Mapping.point2DtoPoint(controlAgents[pacmanId].getLocation());
		Direction direction;
		direction = r.getRoute(myLoc, mipsManLoc);
		// re-process a random direction if an invalid move is detected
		while (!Mapping.validMove(myLoc, edges, direction)) {
			direction = new RandomRouteFinder().getRoute(myLoc, mipsManLoc);
		}
		ent.setDirection(direction);
	}

	/**
	 * Terminates the AI route finding loop upon completion of the current
	 * iteration.
	 *
	 * @return True if the current thread is alive and so the AI can be terminated.
	 */
	public boolean killAI() {
		runAILoop = false;
		if (isAlive()) {
			return true;
		}
		return false;
	}
}
