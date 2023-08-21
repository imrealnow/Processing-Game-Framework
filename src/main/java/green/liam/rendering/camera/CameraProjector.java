package green.liam.rendering.camera;

import green.liam.base.Transform;
import processing.core.PMatrix2D;

public interface CameraProjector {
    PMatrix2D getProjectionMatrix(Transform transform);
}
