package green.liam;

import green.liam.base.Game;
import green.liam.rendering.Camera;
import green.liam.util.CardinalDirection;

public interface HasDirection {
    public CardinalDirection direction();

    default public CardinalDirection cameraRelativeDirection() {
        Camera camera = Game.getInstance().getCamera();
        float cameraAngle = camera.transform().rotation();
        return this.direction().rotate(cameraAngle);
    }
}
