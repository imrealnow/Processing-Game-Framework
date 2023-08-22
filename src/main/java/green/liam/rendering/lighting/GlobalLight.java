package green.liam.rendering.lighting;

import green.liam.util.CardinalDirection;
import java.awt.Color;
import processing.core.PVector;

public enum GlobalLight {
  Dawn(0.5f, new Color(255, 200, 180), CardinalDirection.East, 30.0f),
  Noon(1.0f, new Color(255, 255, 240), CardinalDirection.North, 90.0f),
  Dusk(0.5f, new Color(240, 150, 200), CardinalDirection.West, 150.0f);

  private float intensity;
  private Color colour;
  private CardinalDirection direction;
  private float pitch;

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

  public float intensity() {
    return this.intensity;
  }

  public Color colour() {
    return this.colour;
  }

  public CardinalDirection direction() {
    return this.direction;
  }

  public float pitch() {
    return this.pitch;
  }

  public PVector getDirectionVector() {
    float yaw = this.direction.angle();
    float pitchRadians = (float) Math.toRadians(this.pitch);
    float[] directionVector = new float[3];
    directionVector[0] = (float) (Math.cos(pitchRadians) * Math.cos(yaw));
    directionVector[1] = (float) Math.sin(pitchRadians);
    directionVector[2] = (float) (Math.cos(pitchRadians) * Math.sin(yaw));
    return new PVector(
      directionVector[0],
      directionVector[1],
      directionVector[2]
    );
  }
}
