package green.liam.rendering.camera;

import green.liam.base.Transform;
import processing.core.PApplet;
import processing.core.PMatrix2D;

public class Regular2DProjector implements CameraProjector {

    @Override
    public PMatrix2D getProjectionMatrix(Transform transform) {
        PMatrix2D matrix = new PMatrix2D();
        matrix.scale(1, 1);
        matrix.rotate(PApplet.round(transform.rotationInRadians() * 1000) / 1000f);
        matrix.translate(-transform.position().x, -transform.position().y);
        return matrix;
    }

    @Override
    public float getYScale() {
        return 0f;
    }
}
