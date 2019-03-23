package com.lordsofmidnight.ui;

import javafx.scene.text.Text;

public class TextGenerator {

  public static Text generate(String text, UIColours colour, int size){
    Text result = new Text(text);
    result.setStyle(" -fx-fill: " + colour.getHex() + ";"
        + "-fx-font-size: " + size + ";");
    return result;
  }
}
