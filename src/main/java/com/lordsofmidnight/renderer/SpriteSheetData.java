package com.lordsofmidnight.renderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Class to hold the data on the sprite sheet
 */
public class SpriteSheetData {

  private static HashMap<String, Integer> spriteSheetDimensions =
      new HashMap<String, Integer>() {
        {
          put(SpriteDimensions.PLAYABLE_SPRITE_WIDTH.getIdentifier(), 39);
          put(SpriteDimensions.PLAYABLE_SPRITE_HEIGHT.getIdentifier(), 36);
          put(SpriteDimensions.END_SPRITE_WIDTH.getIdentifier(), 156);
          put(SpriteDimensions.END_SPRITE_HEIGHT.getIdentifier(), 144);
          put(SpriteDimensions.POWERUP_WEB_WIDTH.getIdentifier(), 39);
          put(SpriteDimensions.POWERUP_WEB_HEIGHT.getIdentifier(), 37);
          put(SpriteDimensions.POWERUP_ROCKET_WIDTH.getIdentifier(), 20);
          put(SpriteDimensions.POWERUP_ROCKET_HEIGHT.getIdentifier(), 30);
        }
      };

  /**
   * @param s get width/height of a sprite
   * @return pixel dimension of sprite
   */
  public static int getDimension(SpriteDimensions s) {
    return spriteSheetDimensions.get(s.getIdentifier());
  }

  /**
   * @param dimensionsFile file that contains the sprite data
   */
  public static void updateSpriteDimensions(File dimensionsFile) {
    BufferedReader reader;
    Set<String> dimensionKeys = spriteSheetDimensions.keySet();
    try {
      reader = new BufferedReader(new FileReader(dimensionsFile));
      String line = reader.readLine();
      while (line != null) {
        String key = line.substring(0, line.indexOf("="));
        if (dimensionKeys.contains(key)) {
          String value = line.substring(line.indexOf("=") + 1, line.length());
          value = value.replace(System.lineSeparator(), "");
          spriteSheetDimensions.put(key, Integer.parseInt(value));
        }
        line = reader.readLine();
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
  }

  /**
   * Enumeration of the data
   */
  public enum SpriteDimensions {
    PLAYABLE_SPRITE_WIDTH("PLAYABLE_SPRITE_WIDTH"),
    PLAYABLE_SPRITE_HEIGHT("PLAYABLE_SPRITE_HEIGHT"),
    END_SPRITE_WIDTH("END_SPRITE_WIDTH"),
    END_SPRITE_HEIGHT("END_SPRITE_HEIGHT"),
    POWERUP_WEB_WIDTH("POWERUP_WEB_WIDTH"),
    POWERUP_WEB_HEIGHT("POWERUP_WEB_HEIGHT"),
    POWERUP_ROCKET_WIDTH("POWERUP_ROCKET_WIDTH"),
    POWERUP_ROCKET_HEIGHT("POWERUP_ROCKET_HEIGHT");

    private final String identifier;

    SpriteDimensions(String identifier) {
      this.identifier = identifier;
    }

    /**
     * @return The identifier
     */
    public String getIdentifier() {
      return identifier;
    }
  }
}
