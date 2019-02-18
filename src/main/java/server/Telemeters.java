package server;

import objects.Entity;
import utils.Map;

public interface Telemeters {
    
    Entity[] getAgents();
    
    Map getMap();
    
    void startAI();
}
