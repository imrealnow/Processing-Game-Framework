package green.liam.rendering.lighting;

import green.liam.util.CardinalDirection;
import java.awt.Color;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Enum representing global lighting conditions.
 * Provides predefined lighting conditions for different times of day like Dawn, Noon, and Dusk.
 */
public enum GlobalLight {
  /**
   * Represents the lighting conditions at dawn.
   */
  Dawn(0.5f, new Color(255, 200, 180), CardinalDirection.East, 30.0f),

  /**
   * Represents the lighting conditions at noon.
   */
  Morning(0.5f, new Color(255, 255, 240), CardinalDirection.Northeast, 60.0f),
  Noon(1.0f, new Color(255, 255, 240), CardinalDirection.North, 90.0f),

  /**
   * Represents the lighting conditions at dusk.
   */
  Dusk(0.5f, new Color(240, 150, 200), CardinalDirection.West, 150.0f);

  private float intensity;
  private Color colour;
  private CardinalDirection direction;
  private float pitch;

  /**
   * Constructor for a GlobalLight instance.
   *
   * @param intensity Intensity of the light.
   * @param colour    Color of the light.
   * @param direction Direction of the light source.
   * @param pitch     Pitch angle of the light source.
   */
  GlobalLight(
    float intensity,
    Color colour,
    CardinalDirection direction,
    float pitch
  ) {
    this.intensity = intensity;
    this.colour = colour;
    this.direction = direction;
    this.pitch = pitch;
  }

  /**
   * @return Intensity of the light.
   */
  public float intensity() {
    return this.intensity;
  }

  /**
   * @return Color of the light.
   */
  public Color colour() {
    return this.colour;
  }

  /**
   * @return Direction of the light source.
   */
  public CardinalDirection direction() {
    return this.direction;
  }

  /**
   * @return Pitch angle of the light source.
   */
  public float pitch() {
    return this.pitch;
  }

  /**
   * Converts the yaw and pitch of the light source into a direction vector.
   * @param yawOffset The yaw offset of the light source.
   * @return A PVector representing the direction vector of the light source.
   */
  public PVector getDirectionVector(float yawOffset) {
    float yaw = this.direction.angle() + yawOffset;
    float pitchRadians = PApplet.radians(this.pitch);
    float[] directionVector = new float[3];
    directionVector[0] = PApplet.cos(pitchRadians) * PApplet.cos(yaw);
    directionVector[1] = PApplet.sin(pitchRadians);
    directionVector[2] = PApplet.cos(pitchRadians) * PApplet.sin(yaw);
    return new PVector(
      directionVector[0],
      directionVector[1],
      directionVector[2]
    );
  }
}
