package ai;

import utils.PointMap;
import utils.PointSet;
import ai.mapping.Mapping;
import org.junit.jupiter.api.Test;
import utils.Map;
import utils.Point;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Mapping} class.
 *
 * @author Lewis Ackroyd
 */
class MappingTests {

	private static final int[][] testMap1Raw = {
			{1, 1, 1, 1, 1},
			{1, 0, 0, 0, 1},
			{1, 0, 1, 0, 1},
			{1, 0, 0, 0, 1},
			{1, 1, 1, 1, 1}
	};
	private static final Map testMap1 = new Map(testMap1Raw);

	private static final int[][] testMap2Raw = {
			{1, 1, 1, 1, 1, 1},
			{1, 0, 0, 0, 0, 1},
			{1, 0, 1, 1, 0, 1},
			{1, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1}
	};
	private static final Map testMap2 = new Map(testMap2Raw);

	@Test
	void testGetJunctions() {
		Point[] pointsArr1 = {new Point(1, 1), new Point(1, 3), new Point(3, 1), new Point(3, 3)};
		PointSet result = Mapping.getJunctions(testMap1);
		for (Point p : pointsArr1) {
			assertTrue(result.contains(p));
		}
		Point[] pointsArr2 = {new Point(1, 1), new Point(1, 4), new Point(3, 1), new Point(3, 4)};
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

		PointMap<PointSet> result = Mapping.getEdges(testMap2);

		assertEquals(result, testEdges);
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
		PointSet junctions = Mapping.getJunctions(testMap2);
		PointMap<PointSet> result = Mapping.getEdges(testMap2, junctions);
		assertEquals(result, testEdges);
	}
}
