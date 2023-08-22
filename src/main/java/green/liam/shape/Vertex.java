package green.liam.shape;

import green.liam.base.Game;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Vertex {

  final Transform transform;
  PVector localPosition;
  float height;

  private PVector cachedPosition;

  public Vertex(Transform transform, PVector localPosition, float height) {
    this.transform = transform;
    this.localPosition = localPosition;
    this.height = height;
  }

  public Vertex copy() {
    return new Vertex(this.transform, this.localPosition.copy(), this.height);
  }

  public PVector position() {
    return Transform.translateVector(this.localPosition);
  }

  public PVector translatedPosition() {
    if (this.cachedPosition != null) {
      return this.cachedPosition;
    }
    Camera camera = Game.getInstance().getCamera();
    PVector halfScreenDimensions = Game
      .getInstance()
      .getScreenDimensions()
      .mult(0.5f);
    PVector position = this.localPosition.copy();
    position.rotate(this.transform.rotationInRadians());
    // Transform by the object's local matrix
    PMatrix2D localMatrix2d = this.transform.getCombinedMatrix();
    PVector localTransformedPosition = new PVector();
    localMatrix2d.mult(position, localTransformedPosition);

    // Apply the camera's projection matrix
    PMatrix2D cameraProjectionMatrix2d = camera.getProjectionMatrix();
    PVector projectedPosition = new PVector();
    cameraProjectionMatrix2d.mult(localTransformedPosition, projectedPosition);

    // Apply the height offset and adjust for screen centering
    PVector heightOffset = new PVector(0, this.height(), 0);
    projectedPosition.add(heightOffset);
    projectedPosition.add(halfScreenDimensions);
    return projectedPosition;
  }

  public PVector localPosition() {
    return this.localPosition;
  }

  public Vertex setLocalPosition(PVector localPosition) {
    this.localPosition = localPosition;
    return this;
  }

  public float height() {
    Camera camera = Game.getInstance().getCamera();
    return (
      -(this.height + this.transform.height()) *
      this.transform.yScale() *
      camera.getYScale()
    );
  }

  public Vertex setHeight(float height) {
    this.height = height;
    return this;
  }
}
