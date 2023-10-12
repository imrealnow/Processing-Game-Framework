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

  public float getYScale() {
    return this.currentProjector.getYScale();
  }

  public float depthAlpha() {
    return this.currentProjector.depthAlpha();
  }

  private void updateMatrix() {
    this.projectionMatrix = this.currentProjector.getProjectionMatrix(this.transform);
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
