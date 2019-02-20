package server;

import java.util.HashMap;
import objects.Entity;
import objects.Pellet;
import utils.Input;
import utils.Map;

public interface Telemeters {

  Entity[] getAgents();

  Map getMap();

  void startAI();

  HashMap<String, Pellet> getPellets();

  void setMipID(int ID);

  void addInput(Input in);
}
