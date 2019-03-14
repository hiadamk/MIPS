package com.lordsofmidnight.ai.routefinding.routefinders.condition;

import com.lordsofmidnight.gamestate.points.Point;

public interface ConditionalInterface {
    boolean condition(Point position);
}
