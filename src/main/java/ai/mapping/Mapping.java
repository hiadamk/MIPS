package ai.mapping;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import utils.enums.Direction;

public abstract class Mapping {

	private static final int PATH = 0;

	/**
	 * Calculates the location of all junctions within the map. A value of 0 is
	 * classified as "Path".
	 * 
	 * @param map
	 *            The map having junctions identified on.
	 * @return A {@link HashSet}<{@link Point Point}> containing all points of
	 *         junctions.
	 * @throws IllegalArgumentException
	 *             Map must be at least 1x1.
	 */
	public static HashSet<Point> getJunctions(int[][] map) {
		if (map.length < 1 || map[0].length < 1) {
			throw new IllegalArgumentException("Map must be at least 1x1.");
		}
		HashSet<Point> junctions = new HashSet<Point>();
		for (int x = 0; x < map.length; x++) {
			int[] currentRow = map[x];
			for (int y = 0; y < currentRow.length; y++) {
				// left right down up
				boolean[] isPath = { false, false, false, false };
				if (map[x][y] == PATH) {
					if (x > 0) { // left
						int[] previousRow = map[x - 1];
						if (previousRow[y] == PATH) {
							isPath[0] = true;
						}
					}
					if (x < (map.length - 1)) { // right
						int[] nextRow = map[x + 1];
						if (nextRow[y] == PATH) {
							isPath[1] = true;
						}
					}
					if (y > 0) { // down
						if (currentRow[y - 1] == PATH) {
							isPath[2] = true;
						}
					}
					if (y < (currentRow.length - 1)) { // up
						if (currentRow[y + 1] == PATH) {
							isPath[3] = true;
						}
					}

					// a point is classified as a junction if there are at least 2 adjacent path
					// points to the current one that between them do not share a common x or y
					// coordinate (i.e. they are diagonal to each other
					if ((isPath[0] || isPath[1]) && (isPath[2] || isPath[3])) {
						junctions.add(new Point(x, y));
					}
				}
			}
		}
		return junctions;
	}

	/**
	 * Produces a map from all the junctions to all other junctions that are in a
	 * direct line to them along path with no obstructions (including walls and
	 * other junctions)
	 * 
	 * @param map
	 *            The map to produce the junction pairs from.
	 * @return A mapping of every junction to all connected junctions.
	 * @throws IllegalArgumentException
	 *             The map must be at least 1x1.
	 */
	public static HashMap<Point, HashSet<Point>> getEdges(int[][] map) throws IllegalArgumentException {
		return getEdges(map, getJunctions(map));
	}

	/**
	 * Produces a map from all the junctions to all other junctions that are in a
	 * direct line to them along path with no obstructions (including walls and
	 * other junctions)
	 * 
	 * @param map
	 *            The map to produce the junction pairs from.
	 * @param junctions
	 *            All the junctions within the map. These can be generated using the
	 *            {@link #getJunctions()} method.
	 * @return A mapping of every junction to all connected junctions.
	 * @throws IllegalArgumentException
	 *             The map must be at least 1x1.
	 */
	public static HashMap<Point, HashSet<Point>> getEdges(int[][] map, HashSet<Point> junctions) {
		if (map.length < 1 || map[0].length < 1) {
			throw new IllegalArgumentException("Map must be at least 1x1.");
		}
		HashMap<Point, HashSet<Point>> edgeMap = new HashMap<Point, HashSet<Point>>();
		// generates links for every junction
		for (Point p : junctions) {
			HashSet<Point> edgeSet = new HashSet<Point>();
			int currentX = p.x - 1;
			// a wall or junction will terminate the search
			while (currentX > 0 && map[currentX][p.y] == PATH) {
				Point testPoint = new Point(currentX, p.y);
				if (junctions.contains(testPoint)) {
					// if a junction is found it is added to the edge pairing
					edgeSet.add(testPoint);
					break;
				}
				currentX--;
			}
			currentX = p.x + 1;
			// a wall or junction will terminate the search
			while (currentX < map.length && map[currentX][p.y] == PATH) {
				Point testPoint = new Point(currentX, p.y);
				if (junctions.contains(testPoint)) {
					// if a junction is found it is added to the edge pairing
					edgeSet.add(testPoint);
					break;
				}
				currentX++;
			}
			int currentY = p.y - 1;
			// a wall or junction will terminate the search
			while (currentY > 0 && map[p.x][currentY] == PATH) {
				Point testPoint = new Point(p.x, currentY);
				if (junctions.contains(testPoint)) {
					// if a junction is found it is added to the edge pairing
					edgeSet.add(testPoint);
					break;
				}
				currentY--;
			}
			currentY = p.y + 1;
			// a wall or junction will terminate the search
			while (currentY < map[0].length && map[p.x][currentY] == PATH) {
				Point testPoint = new Point(p.x, currentY);
				if (junctions.contains(testPoint)) {
					// if a junction is found it is added to the edge pairing
					edgeSet.add(testPoint);
					break;
				}
				currentY++;
			}
			edgeMap.put(p, edgeSet);
		}
		return edgeMap;
	}

	/**
	 * Checks if a direction is a valid move from the current position. If the
	 * current position is not a junction then false will be returned. Use
	 * {@link #validMove(Point, HashMap, Direction)} for this case.
	 * 
	 * @param position
	 *            The current position.
	 * @param edges
	 *            The mapping of all valid junctions.
	 * @param direction
	 *            The proposed direction of travel.
	 * @return True if the direction is a valid direction of travel. False otherwise
	 *         or if the current position is not a junction.
	 */
	public static boolean validMove(Point position, HashMap<Point, HashSet<Point>> edges, Direction direction) {
		// if the position is not a junction then no edges will be found.
		if (!edges.containsKey(position)) {
			return false;
		}
		HashSet<Point> junctions = edges.get(position);
		switch (direction) {
		case UP: {
			// identifies if any of the junctions are in correct direction
			for (Point p : junctions) {
				if (position.y < p.y) {
					return true;
				}
			}
			break;
		}
		case DOWN: {
			// identifies if any of the junctions are in correct direction
			for (Point p : junctions) {
				if (position.y > p.y) {
					return true;
				}
			}
			break;
		}
		case LEFT: {
			// identifies if any of the junctions are in correct direction
			for (Point p : junctions) {
				if (position.x > p.x) {
					return true;
				}
			}
			break;
		}
		case RIGHT: {
			// identifies if any of the junctions are in correct direction
			for (Point p : junctions) {
				if (position.x < p.x) {
					return true;
				}
			}
			break;
		}
		}
		return false;
	}

	/**
	 * Checks if a direction is a valid move from the current position. If the
	 * current position is not a junction then false will be returned. Use
	 * {@link #validMove(Point, HashMap, Direction)} for this case.
	 * 
	 * @param position
	 *            The current position.
	 * @param edges
	 *            The mapping of all valid junctions.
	 * @param direction
	 *            The proposed direction of travel.
	 * @return True if the direction is a valid direction of travel. False otherwise
	 *         or if the current position is not a junction.
	 * @throws IllegalArgumentException
	 *             The map must be at least 1x1.
	 */
	public static boolean validMove(Point position, int[][] map, Direction direction) {
		if (map.length < 1 || map[0].length < 1) {
			throw new IllegalArgumentException("Map must be at least 1x1.");
		}
		switch (direction) {
		case UP: {
			// identifies if any of the adjacent squares in correct direction are path
			if (position.y + 1 < map[0].length && map[position.x][position.y + 1] == PATH) {
				return true;
			}
		}
		case DOWN: {
			// identifies if any of the adjacent squares in correct direction are path
			if (position.y - 1 > 0 && map[position.x][position.y - 1] == PATH) {
				return true;
			}
		}
		case LEFT: {
			// identifies if any of the adjacent squares in correct direction are path
			if (position.x - 1 > 0 && map[position.x - 1][position.y] == PATH) {
				return true;
			}
		}
		case RIGHT: {
			// identifies if any of the adjacent squares in correct direction are path
			if (position.x + 1 < map.length && map[position.x + 1][position.y] == PATH) {
				return true;
			}
		}
		}
		return false;
	}

}
