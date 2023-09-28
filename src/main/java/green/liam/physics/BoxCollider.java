package green.liam.physics;

import green.liam.base.GameObject;
import processing.core.PVector;

public class BoxCollider extends Collider {

  float width;
  float length;
  float height;

  private static final PVector[] AXES = {
      new PVector(1, 0, 0),
      new PVector(0, 1, 0),
      new PVector(0, 0, 1)
  };

  public BoxCollider(GameObject gameObject) {
    super(gameObject);
    this.width = 1;
    this.length = 1;
    this.height = 1;
  }

  public BoxCollider(GameObject gameObject, float width, float length, float height) {
    super(gameObject);
    this.width = width;
    this.length = length;
    this.height = height;
  }

  public BoxCollider setDimensions(float width, float length, float height) {
    this.width = width;
    this.length = length;
    this.height = height;
    return this;
  }

  public float width() {
    return this.width;
  }

  public float length() {
    return this.length;
  }

  public float height() {
    return this.height;
  }

  public PVector centerPosition() {
    return this.gameObject().transform().position().copy().add(this.centerOffset);
  }

  public float minX() {
    return this.centerPosition().x - this.width / 2;
  }

  public float maxX() {
    return this.centerPosition().x + this.width / 2;
  }

  public float minY() {
    return this.centerPosition().y - this.length / 2;
  }

  public float maxY() {
    return this.centerPosition().y + this.length / 2;
  }

  public float minZ() {
    return this.centerPosition().z - this.height / 2;
  }

  public float maxZ() {
    return this.centerPosition().z + this.height / 2;
  }

  @Override
  public boolean isPointInside(PVector point) {
    PVector center = this.centerPosition();
    float halfWidth = this.width / 2;
    float halfLength = this.length / 2;
    float halfHeight = this.height / 2;
    return point.x >= center.x - halfWidth && point.x <= center.x + halfWidth
        && point.y >= center.y - halfLength && point.y <= center.y + halfLength
        && point.z >= center.z - halfHeight && point.z <= center.z + halfHeight;
  }

  @Override
  public boolean collidesWith(Collider other) {
    if (other instanceof BoxCollider) {
      BoxCollider otherBox = (BoxCollider) other;
      return this.isBoxColliding(otherBox);
    } else {
      return other.collidesWith(this);
    }
  }

  @Override
  public PVector closestPointTo(PVector point) {
    float closestX = Math.min(Math.max(point.x, this.minX()), this.maxX());
    float closestY = Math.min(Math.max(point.y, this.minY()), this.maxY());
    float closestZ = Math.min(Math.max(point.z, this.minZ()), this.maxZ());
    return new PVector(closestX, closestY, closestZ);
  }

  private boolean isBoxColliding(BoxCollider other) {
    PVector[] axes = this.getAxes();
    PVector[] otherAxes = other.getAxes();
    float[] thisProjection;
    float[] otherProjection;

    for (PVector axis : axes) {
      thisProjection = this.project(axis);
      otherProjection = other.project(axis);
      if (!this.overlap(thisProjection, otherProjection)) {
        return false;
      }
    }
    for (PVector axis : otherAxes) {
      thisProjection = this.project(axis);
      otherProjection = other.project(axis);
      if (!this.overlap(thisProjection, otherProjection)) {
        return false;
      }
    }
    return true;
  }

  public PVector[] getAxes() {
    return AXES;
  }

  public float[] project(PVector axis) {
    PVector center = this.centerPosition();
    float halfWidth = this.width / 2;
    float halfLength = this.length / 2;
    float halfHeight = this.height / 2;
    float centerDot = center.dot(axis);
    float[] projection = new float[6];
    projection[0] = centerDot - halfWidth;
    projection[1] = centerDot + halfWidth;
    projection[2] = centerDot - halfLength;
    projection[3] = centerDot + halfLength;
    projection[4] = centerDot - halfHeight;
    projection[5] = centerDot + halfHeight;
    return projection;
  }

  public boolean overlap(float[] projection1, float[] projection2) {
    return (projection1[0] >= projection2[0] && projection1[0] <= projection2[1])
        || (projection1[1] >= projection2[0] && projection1[1] <= projection2[1])
        || (projection2[0] >= projection1[0] && projection2[0] <= projection1[1])
        || (projection2[1] >= projection1[0] && projection2[1] <= projection1[1]);
  }
}
