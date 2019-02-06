package ai;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import ai.mapping.Mapping;
import utils.Map;
import utils.enums.Direction;

class MappingTests {
	private static final int[][] testMap1Raw = {
			{ 1, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 1 },
			{ 1, 0, 1, 0, 1 },
			{ 1, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1 }};
	private static final Map testMap1 = new Map(testMap1Raw);

	private static final int[][] testMap2Raw = {
			{ 1, 1, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 0, 1 },
			{ 1, 0, 1, 1, 0, 1 },
			{ 1, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1 }};
	private static final Map testMap2 = new Map(testMap2Raw);

	@Test
	void testGetJunctions() {
		Point[] pointsArr1 = { new Point(1, 1), new Point(1, 3), new Point(3, 1), new Point(3, 3) };
		HashSet<Point> result = Mapping.getJunctions(testMap1);
		for (Point p : pointsArr1) {
			assertTrue(result.contains(p));
		}
		Point[] pointsArr2 = { new Point(1, 1), new Point(1, 4), new Point(3, 1), new Point(3, 4) };
		result = Mapping.getJunctions(testMap2);
		for (Point p : pointsArr2) {
			assertTrue(result.contains(p));
		}
	}

	@Test
	void testGetEdgesMap() {
		HashMap<Point, HashSet<Point>> testEdges = new HashMap<Point, HashSet<Point>>();
		HashSet<Point> nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(1, 4));
		nextPoint.add(new Point(3, 1));
		testEdges.put(new Point(1, 1), nextPoint);

		nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(3, 4));
		nextPoint.add(new Point(1, 1));
		testEdges.put(new Point(1, 4), nextPoint);

		nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(3, 4));
		nextPoint.add(new Point(1, 1));
		testEdges.put(new Point(3, 1), nextPoint);

		nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(1, 4));
		nextPoint.add(new Point(3, 1));
		testEdges.put(new Point(3, 4), nextPoint);

		HashMap<Point, HashSet<Point>> result = Mapping.getEdges(testMap2);

		assertTrue(result.equals(testEdges));
	}

	@Test
	void testGetEdgesMapHashSetOfPoint() {
		HashMap<Point, HashSet<Point>> testEdges = new HashMap<Point, HashSet<Point>>();
		HashSet<Point> nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(1, 4));
		nextPoint.add(new Point(3, 1));
		testEdges.put(new Point(1, 1), nextPoint);

		nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(3, 4));
		nextPoint.add(new Point(1, 1));
		testEdges.put(new Point(1, 4), nextPoint);

		nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(3, 4));
		nextPoint.add(new Point(1, 1));
		testEdges.put(new Point(3, 1), nextPoint);

		nextPoint = new HashSet<Point>();
		nextPoint.add(new Point(1, 4));
		nextPoint.add(new Point(3, 1));
		testEdges.put(new Point(3, 4), nextPoint);
		HashSet<Point> junctions = Mapping.getJunctions(testMap2);
		HashMap<Point, HashSet<Point>> result = Mapping.getEdges(testMap2, junctions);
		assertTrue(result.equals(testEdges));
	}

	@Test
	void testValidMovePointHashMapOfPointHashSetOfPointDirection() {
		HashMap<Point, HashSet<Point>> edges = Mapping.getEdges(testMap2);
		Point testPoint = new Point(1, 1);

		assertTrue(Mapping.validMove(testPoint, edges, Direction.UP));
		assertTrue(Mapping.validMove(testPoint, edges, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.DOWN));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.LEFT));

		
		//will fail all because given point is not a junction
		assertFalse(Mapping.validMove(new Point(0, 1), edges, Direction.RIGHT));

		testPoint = new Point(2, 1);

		assertFalse(Mapping.validMove(testPoint, edges, Direction.LEFT));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.UP));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.DOWN));

		testPoint = new Point(0, 0);

		assertFalse(Mapping.validMove(testPoint, edges, Direction.LEFT));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.UP));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.DOWN));

		testPoint = new Point(4, 5);

		assertFalse(Mapping.validMove(testPoint, edges, Direction.LEFT));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.UP));
		assertFalse(Mapping.validMove(testPoint, edges, Direction.DOWN));
	}

	@Test
	void testValidMovePointMapDirection() {
		Point testPoint = new Point(1, 1);

		assertTrue(Mapping.validMove(testPoint, testMap2, Direction.UP));
		assertTrue(Mapping.validMove(testPoint, testMap2, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.DOWN));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.LEFT));

		
		//will fail all because given point is not a junction
		assertTrue(Mapping.validMove(new Point(0, 1), testMap2, Direction.RIGHT));

		testPoint = new Point(2, 1);

		assertTrue(Mapping.validMove(testPoint, testMap2, Direction.LEFT));
		assertTrue(Mapping.validMove(testPoint, testMap2, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.UP));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.DOWN));

		testPoint = new Point(0, 0);

		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.LEFT));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.UP));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.DOWN));

		testPoint = new Point(4, 5);

		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.LEFT));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.RIGHT));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.UP));
		assertFalse(Mapping.validMove(testPoint, testMap2, Direction.DOWN));
	}

	@Test
	void testPointConversion() {
		Point testPoint = new Point(1, 1);
		Point2D.Double testPoint2D = new Point2D.Double(1, 1);
		assertTrue(Mapping.point2DtoPoint(Mapping.pointToPoint2D(testPoint)).equals(testPoint));
		assertTrue(Mapping.pointToPoint2D(Mapping.point2DtoPoint(testPoint2D)).equals(testPoint2D));

		assertTrue(Mapping.point2DtoPoint(Mapping.pointToPoint2D(null))==null);
		assertTrue(Mapping.pointToPoint2D(Mapping.point2DtoPoint(null))==null);
	}
}
