package green.liam.rendering.camera;

import green.liam.base.Transform;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Isometric3DProjector implements CameraProjector {
    private static final float Y_SCALE = 1f / (float) Math.sqrt(2);

    @Override
    public PMatrix2D getProjectionMatrix(Transform transform) {
        PMatrix2D updatedCameraMatrix = new PMatrix2D();
        PVector cameraPosition = transform.position();
        float rotation = (float) Math.toRadians(transform.rotation()) * -1f;
        // cameraPosition.rotate(rotation); // Rotate camera's position

        updatedCameraMatrix.scale(1, Y_SCALE); // Scale
        updatedCameraMatrix.rotate(-rotation); // Rotate
        updatedCameraMatrix.translate(-cameraPosition.x, -cameraPosition.y); // Translate to
        // camera's
        // position
        // updatedCameraMatrix.translate(-cameraPosition.x, -cameraPosition.y); // Translate back to
        // origin

        return updatedCameraMatrix;
    }

}
