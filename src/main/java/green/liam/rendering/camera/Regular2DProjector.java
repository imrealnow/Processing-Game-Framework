package green.liam.rendering.camera;

import green.liam.base.Transform;
import processing.core.PMatrix2D;

public class Regular2DProjector implements CameraProjector {

    @Override
    public PMatrix2D getProjectionMatrix(Transform transform) {
        PMatrix2D matrix = new PMatrix2D();
        matrix.translate(-transform.position().x, -transform.position().y);
        return matrix;
    }

}
