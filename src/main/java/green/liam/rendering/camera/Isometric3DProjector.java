package green.liam.rendering.camera;

import green.liam.base.Game;
import green.liam.base.Transform;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Isometric3DProjector implements CameraProjector {
    private static final float Y_SCALE = 2f / (float) Math.sqrt(2);

    @Override
    public PMatrix2D getProjectionMatrix(Transform transform) {
        PMatrix2D matrix = new PMatrix2D();
        float rotation = (float) Math.toRadians(transform.rotation());
        PVector position = transform.position().rotate(rotation);
        PVector halfScreenDimensions = Game.getInstance().getScreenDimensions().mult(0.5f);

        matrix.rotate(-rotation);
        matrix.translate(-halfScreenDimensions.x - position.x,
                -halfScreenDimensions.y - transform.height() - position.y);
        matrix.scale(1f, Y_SCALE);
        return matrix;
    }

}
