package com.lordsofmidnight.ai.routefinding.routefinderdata;

import com.lordsofmidnight.gamestate.points.Point;

public class SampleSearchData {
    private final Point myPosition;
    private final Point parent;
    private final int moveCost;

    public SampleSearchData(Point myPosition, Point parent, int moveCost) {
        this.myPosition = myPosition;
        this.parent = parent;
        this.moveCost = moveCost;
    }

    public Point getMyPosition() {
        return myPosition;
    }

    public Point getParentPosition() {
        return parent;
    }

    public int getMoveCost() {
        return moveCost;
    }
}
