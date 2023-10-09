package green.liam.rendering;

import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.events.Observer;
import green.liam.events.TransformChangeEvent;
import green.liam.rendering.camera.CameraProjector;
import green.liam.util.Pair;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Camera
    extends GameObject
    implements Observer<TransformChangeEvent>, AutoCloseable {

  private CameraProjector currentProjector;
  private PMatrix2D projectionMatrix;
  private PMatrix2D inverseMatrix;

  public Camera(CameraProjector initialProjector) {
    super();
    this.transform.addChangeObserver(this);
    this.currentProjector = initialProjector;
    this.updateMatrix();
  }

  public Pair<PVector, PVector> getScreenBounds() {
    // use projection matrix to get screen bounds
    Game game = Game.getInstance();
    float halfWidth = game.width / 2;
    float halfHeight = game.height / 2;
    PVector topLeft = new PVector(-halfWidth, -halfHeight);
    PVector bottomRight = new PVector(halfWidth, halfHeight);
    PVector transformedTopLeft = new PVector();
    PVector transformedBottomRight = new PVector();
    this.inverseMatrix.mult(topLeft, transformedTopLeft);
    this.inverseMatrix.mult(bottomRight, transformedBottomRight);
    return new Pair<>(transformedTopLeft, transformedBottomRight);
  }

  public float getYScale() {
    return this.currentProjector.getYScale();
  }

  public float depthAlpha() {
    return this.currentProjector.depthAlpha();
  }

  private void updateMatrix() {
    this.projectionMatrix = this.currentProjector.getProjectionMatrix(this.transform);
    this.inverseMatrix = this.projectionMatrix.get();
    this.inverseMatrix.invert();
  }

  public PMatrix2D getProjectionMatrix() {
    return this.projectionMatrix;
  }

  public void switchProjector(CameraProjector newProjector) {
    this.currentProjector = newProjector;
    this.updateMatrix();
  }

  @Override
  public void onNotify(TransformChangeEvent event) {
    this.updateMatrix();
  }

  @Override
  public void close() throws Exception {
    this.transform.removeChangeObserver(this);
  }
}
