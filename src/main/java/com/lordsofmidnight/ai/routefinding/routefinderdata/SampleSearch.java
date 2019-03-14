package com.lordsofmidnight.ai.routefinding.routefinderdata;

import com.lordsofmidnight.ai.routefinding.routefinders.condition.ConditionalInterface;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.utils.enums.Direction;

public class SampleSearch {
    private final int sampleDepth;
    private final Map map;

    public SampleSearch(int sampleDepth, Map map) {
        this.sampleDepth = sampleDepth;
        this.map = map;
    }

    public Direction getRoute(Point startPosition, ConditionalInterface condition) {
        PointMap<SampleSearchData> visited = new PointMap<>(map);
        PointMap<SampleSearchData> unVisited = new PointMap<>(map);
        visited.put(startPosition, new SampleSearchData(startPosition, startPosition, 0));
        while () {

        }
        return null;
    }
}
