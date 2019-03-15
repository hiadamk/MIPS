package com.lordsofmidnight.utils.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public enum Awards {
  MOST_KILLS("Killmonger","killed","players"),
  MOST_DEATHS("Punching Bag","died","times"),
  MOST_POINTS("Money Maker","collected","points"),
  MOST_POINTS_STOLEN("Aspiring Thief","killed","players"),
  MOST_POINTS_LOST("Everyone's Prey","lost","to other players"),
  MOST_ITEMS_USED("Shop Keeper","used","items");

  String name;
  String verb;
  String noun;

  Awards(String name,String verb,String noun){
    this.name = name;
    this.noun = noun;
    this.verb = verb;
  }

  public String getName(){
    return name;
  }

  public String getMessage(int score){
    return this.verb + " " + score + " " + this.noun;
  }

  public static Awards[] getTwoRandomAwards(){
    Awards[] awards = new Awards[2];
    Random r = new Random();
    ArrayList<Awards> allAwards = new ArrayList(Arrays.asList(Awards.values()));
    allAwards.remove(Awards.MOST_POINTS);
    awards[0] = allAwards.get(r.nextInt(allAwards.size()));
    allAwards.remove(awards[0]);
    awards[1] = allAwards.get(r.nextInt(allAwards.size()));
    return awards;
  }
}
