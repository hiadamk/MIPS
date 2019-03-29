package com.lordsofmidnight.utils.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Enums to represent the different awards to be awarded
 */
public enum Awards {
  MOST_KILLS("Killmonger", "killed", "players"),
  MOST_DEATHS("Punching Bag", "died", "times"),
  MOST_POINTS("Money Maker", "collected", "points"),
  MOST_POINTS_STOLEN("Aspiring Thief", "stole", "points"),
  MOST_POINTS_LOST("Everyone's Prey", "lost", "pts to other players"),
  MOST_ITEMS_USED("Shop Keeper", "used", "items");

  String name;
  String verb;
  String noun;

  /**
   * @param name The name of the award
   * @param verb The verb to be used when displaying
   * @param noun the noun to be used when displaying
   */
  Awards(String name, String verb, String noun) {
    this.name = name;
    this.noun = noun;
    this.verb = verb;
  }

  /**
   * Selects two random awards to show at the end
   *
   * @return An array of the wards
   */
  public static Awards[] getTwoRandomAwards() {
    Awards[] awards = new Awards[2];
    Random r = new Random();
    ArrayList<Awards> allAwards = new ArrayList(Arrays.asList(Awards.values()));
    allAwards.remove(Awards.MOST_POINTS);
    awards[0] = allAwards.get(r.nextInt(allAwards.size()));
    allAwards.remove(awards[0]);
    awards[1] = allAwards.get(r.nextInt(allAwards.size()));
    return awards;
  }

  /**
   * @return The name of the award
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the message to display
   *
   * @param score The score for the award
   * @return The message
   */
  public String getMessage(int score) {
    return this.verb + " " + score + " " + this.noun;
  }
}
