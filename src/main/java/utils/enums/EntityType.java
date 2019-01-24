package utils.enums;

public enum EntityType {
  PACMAN(0) {
    @Override
    public String toString() {
      return "pacman";
    }
  },
  GHOST1(1) {
    @Override
    public String toString() {
      return "ghost1";
    }
  },
  GHOST2(2) {
    @Override
    public String toString() {
      return "ghost2";
    }
  },
  GHOST3(3) {
    @Override
    public String toString() {
      return "ghost3";
    }
  },
  GHOST4(4) {
    @Override
    public String toString() {
      return "ghost4";
    }
  };

  private int id;

  EntityType(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
