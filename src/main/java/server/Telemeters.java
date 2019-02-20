package server;

import objects.Entity;
import objects.Pellet;
import utils.Input;
import utils.Map;

import java.util.HashMap;

public interface Telemeters {
  
  Entity[] getAgents();
  
  Map getMap();
  
  void startAI();
  
  HashMap<String, Pellet> getPellets();
  
  void setMipID(int ID);
  
  void addInput(Input in);
  
}
