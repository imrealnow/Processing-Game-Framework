package green.liam.util;

public enum CardinalDirection {
  North(0, 1),
  East(1, 0),
  South(0, -1),
  West(-1, 0);

  private int x;
  private int y;

  CardinalDirection(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int x() {
    return this.x;
  }

  public int y() {
    return this.y;
  }

  public CardinalDirection opposite() {
    switch (this) {
      case North:
        return South;
      case East:
        return West;
      case South:
        return North;
      case West:
        return East;
      default:
        throw new RuntimeException("Invalid cardinal direction");
    }
  }

  public float angle() {
    switch (this) {
      case North:
        return 0;
      case East:
        return (float) (Math.PI / 2);
      case South:
        return (float) Math.PI;
      case West:
        return (float) (3 * Math.PI / 2);
      default:
        throw new RuntimeException("Invalid cardinal direction");
    }
  }
}
