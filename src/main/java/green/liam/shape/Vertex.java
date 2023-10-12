package green.liam.shape;

import green.liam.base.Game;
import green.liam.base.Transform;
import green.liam.events.Observer;
import green.liam.events.TransformChangeEvent;
import green.liam.rendering.Camera;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Vertex {

  Transform transform;
  PVector localPosition;
  float height;

  boolean transformChanged = false;

  long translatedPositionCacheFrame = -1;
  long worldPositionCacheFrame = -1;
  PVector translatedPositionCache = null;
  PVector worldPositionCache = null;

  public Vertex(Transform transform, PVector localPosition, float height) {
    this.transform = transform;
    this.localPosition = localPosition;
    this.height = height;
  }

  public void destroy() {
    this.transform = null;
    this.localPosition = null;
    this.worldPositionCache = null;
    this.translatedPositionCache = null;
  }

  public Vertex copy() {
    return new Vertex(this.transform, this.localPosition.copy(), this.height);
  }

  public PVector position() {
    return Transform.translateVector(this.localPosition);
  }

  public Transform transform() {
    return this.transform;
  }

  public Vertex setTransform(Transform transform) {
    this.transform = transform;
    return this;
  }

  public PVector pseudo3DPosition() {
    PVector pos2D = this.translatedPosition(); // Get the 2D position (x, y)
    return new PVector(pos2D.x, this.height(), pos2D.y);
  }

  public PVector translatedPosition() {
    long currentFrame = Game.getInstance().getFrameCount();
    if (this.translatedPositionCacheFrame != currentFrame) {
      this.translatedPositionCacheFrame = currentFrame;
      this.translatedPositionCache = this.calculateTranslatedPosition();
    }
    return this.translatedPositionCache;
  }

  public PVector calculateTranslatedPosition() {
    Camera camera = Game.getInstance().getCamera();
    PVector halfScreenDimensions = Game
        .getInstance()
        .getScreenDimensions()
        .mult(0.5f);
    // Transform by the object's local matrix
    PVector localTransformedPosition = this.worldPosition();

    // Apply the camera's projection matrix
    PMatrix2D cameraProjectionMatrix2d = camera.getProjectionMatrix();
    PVector projectedPosition = new PVector();
    cameraProjectionMatrix2d.mult(localTransformedPosition, projectedPosition);

    // Apply the height offset and adjust for screen centering
    PVector heightOffset = new PVector(0, this.height(), 0)
        .mult(camera.getYScale());
    projectedPosition.add(heightOffset);
    projectedPosition.add(halfScreenDimensions);
    return projectedPosition;

  }

  public PVector worldPosition() {
    long currentFrame = Game.getInstance().getFrameCount();
    if (this.worldPositionCacheFrame != currentFrame) {
      this.worldPositionCacheFrame = currentFrame;
      this.worldPositionCache = this.calculateWorldPosition();
    }
    return this.worldPositionCache;
  }

  public PVector calculateWorldPosition() {
    PMatrix2D localMatrix2d = this.transform.getCombinedMatrix();
    PVector localTransformedPosition = new PVector();
    localMatrix2d.mult(this.localPosition, localTransformedPosition);
    return localTransformedPosition;
  }

  public PVector localPosition() {
    return this.localPosition;
  }

  public Vertex setLocalPosition(PVector localPosition) {
    this.localPosition = localPosition;
    return this;
  }

  public float height() {
    return (-(this.height + this.transform.height()) * this.transform.yScale());
  }

  public Vertex setHeight(float height) {
    this.height = height;
    return this;
  }

  public float yPos() {
    return this.translatedPosition().y - this.height();
  }
}
