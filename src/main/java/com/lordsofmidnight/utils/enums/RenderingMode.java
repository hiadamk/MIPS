package com.lordsofmidnight.utils.enums;

import com.lordsofmidnight.renderer.Renderer;

public enum RenderingMode {
  NO_SCALING("no_scaling"),
  INTEGER_SCALING("integer_scaling"),
  SMOOTH_SCALING("smooth_scaling"),
  STANDARD_SCALING("standard_scaling");


  RenderingMode(String name){
    this.name = name;
  }

  public String getName(){
    return name;
  }

  public static RenderingMode fromString(String s){
    for(RenderingMode r: RenderingMode.values()){
      if(r.getName().equals(s)){
        return r;
      }
    }
    return null;
  }

  private String name;
}
