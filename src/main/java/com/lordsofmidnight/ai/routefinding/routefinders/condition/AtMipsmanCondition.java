package com.lordsofmidnight.ai.routefinding.routefinders.condition;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;

public class AtMipsmanCondition implements ConditionalInterface {

    private final Entity[] agents;

    public AtMipsmanCondition(Entity[] agents) {
        this.agents = agents;
    }

    @Override
    public boolean condition(Point position) {
        for (Entity ent : agents) {
            if (ent.getLocation().getGridCoord().equals(position.getGridCoord())) {
                if (ent.isMipsman()) {
                    return true;
                }
            }
        }
        return false;
    }
}
