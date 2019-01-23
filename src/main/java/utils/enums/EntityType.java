package utils.enums;

public enum EntityType {
  PACMAN {
    @Override
    public String toString() {
      return "pacman";
    }
  },
  GHOST1 {
    @Override
    public String toString() {
      return "ghost1";
    }
  },
  GHOST2 {
    @Override
    public String toString() {
      return "ghost2";
    }
  },
  GHOST3 {
    @Override
    public String toString() {
      return "ghost3";
    }
  },
  GHOST4 {
    @Override
    public String toString() {
      return "ghost4";
    }
  }
}
