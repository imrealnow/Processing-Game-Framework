package green.liam.rendering.camera;

import green.liam.base.Transform;
import processing.core.PApplet;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Isometric3DProjector implements CameraProjector {

  private static final float Y_SCALE = 1f / (float) Math.sqrt(2);

  @Override
  public PMatrix2D getProjectionMatrix(Transform transform) {
    PMatrix2D updatedCameraMatrix = new PMatrix2D();
    PVector cameraPosition = transform.position();
    float rotation = transform.rotationInRadians();

    updatedCameraMatrix.scale(1, Y_SCALE); // Scale
    updatedCameraMatrix.rotate(PApplet.round(rotation * 1000f) / 1000f); // Rotate
    updatedCameraMatrix.translate(-cameraPosition.x, -cameraPosition.y); // translate

    return updatedCameraMatrix;
  }

  @Override
  public float getYScale() {
    return 1;
  }

  @Override
  public float depthAlpha() {
    // consider both transform height and y position to calculate depth
    return 0.5f;
  }
}
