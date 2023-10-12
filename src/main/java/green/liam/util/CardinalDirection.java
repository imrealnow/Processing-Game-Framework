package green.liam.util;

import processing.core.PVector;

/**
 * Represents the cardinal directions and their intermediate directions.
 */
public enum CardinalDirection {
  /** North direction. */
  North(0, 1),

  /** Northeast direction. */
  Northeast(1, 1),

  /** East direction. */
  East(1, 0),

  /** Southeast direction. */
  Southeast(1, -1),

  /** South direction. */
  South(0, -1),

  /** Southwest direction. */
  Southwest(-1, -1),

  /** West direction. */
  West(-1, 0),

  /** Northwest direction. */
  Northwest(-1, 1);

  private int x;
  private int y;

  /**
   * Constructs a new cardinal direction.
   *
   * @param x
   *          The X component of the direction.
   * @param y
   *          The Y component of the direction.
   */
  CardinalDirection(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * @return The X component of the direction.
   */
  public int x() {
    return this.x;
  }

  /**
   * @return The Y component of the direction.
   */
  public int y() {
    return this.y;
  }

  /**
   * @return The direction as a vector.
   */
  public PVector toVector() {
    return new PVector(this.x, this.y);
  }

  /**
   * Gets the opposite of the current cardinal direction.
   *
   * @return Opposite cardinal direction.
   */
  public CardinalDirection opposite() {
    switch (this) {
      case North:
        return South;
      case Northeast:
        return Southwest;
      case East:
        return West;
      case Southeast:
        return Northwest;
      case South:
        return North;
      case Southwest:
        return Northeast;
      case West:
        return East;
      case Northwest:
        return Southeast;
      default:
        throw new RuntimeException("Invalid cardinal direction");
    }
  }

  public static CardinalDirection fromDegrees(float degrees) {
    float angle = ((degrees + 180) % 360 + 360) % 360 - 180;
    int rounded = (int) Math.round(angle / 45) * 45;
    switch (rounded) {
      case -180, 180:
        return North;
      case -135:
        return Northeast;
      case -90:
        return East;
      case -45:
        return Southeast;
      case 0:
        return South;
      case 45:
        return Southwest;
      case 90:
        return West;
      case 135:
        return Northwest;
      default:
        throw new RuntimeException("Invalid cardinal direction:" + rounded);
    }
  }

  public static CardinalDirection fromNormalVector(PVector vector) {
    float angle = vector.heading();
    return fromDegrees(angle);
  }

  /**
   * Gets the angle (in radians) corresponding to the cardinal direction.
   *
   * @return Angle in radians.
   */
  public float angle() {
    switch (this) {
      case North:
        return 0;
      case Northeast:
        return (float) (Math.PI / 4);
      case East:
        return (float) (Math.PI / 2);
      case Southeast:
        return (float) (3 * Math.PI / 4);
      case South:
        return (float) Math.PI;
      case Southwest:
        return (float) (5 * Math.PI / 4);
      case West:
        return (float) (3 * Math.PI / 2);
      case Northwest:
        return (float) (7 * Math.PI / 4);
      default:
        throw new RuntimeException("Invalid cardinal direction");
    }
  }

  public CardinalDirection rotate(float degrees) {
    float angle = (float) Math.toDegrees(this.angle()) + degrees;
    return CardinalDirection.fromDegrees(angle);
  }
}
