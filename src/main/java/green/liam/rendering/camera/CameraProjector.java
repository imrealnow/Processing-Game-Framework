package green.liam.rendering.camera;

import green.liam.base.Transform;
import processing.core.PMatrix2D;

public interface CameraProjector {
    /**
     * Get the projection matrix for the camera.
     * 
     * @param transform transform of the camera.
     * @return projection matrix for the camera.
     */
    PMatrix2D getProjectionMatrix(Transform transform);

    /**
     * Get the scale of the y axis. 1 for normal, 0 for no y axis.
     * 
     * @return scale of the y axis.
     */
    float getYScale();

    /**
     * Get ratio of height to ground y position. 1 to prioritize height, 0 to prioritize ground y
     * position.
     * 
     * @return ratio of height to ground y position.
     */
    float depthAlpha();
}
