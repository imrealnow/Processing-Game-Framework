package green.liam;

import green.liam.base.Component;
import green.liam.base.GameObject;
import green.liam.base.Time;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import processing.core.PVector;

public class CameraController extends Component {

    Camera camera;
    Transform target = new Transform();

    public CameraController(GameObject gameObject) {
        super(gameObject);
        if (gameObject instanceof Camera) {
            this.camera = (Camera) gameObject;
        } else {
            throw new IllegalArgumentException(
                    "CameraController must be attached to a Camera");
        }
    }

    public void setTarget(Transform target) {
        this.target = target;
    }

    @Override
    public void update() {
        PVector targetPosition = this.target.position();
        PVector cameraPosition = this.camera.transform().position();
        PVector targetDirection = PVector
                .sub(targetPosition, cameraPosition)
                .mult(Time.INSTANCE.deltaTime() * 4f);
        PVector newPosition = PVector.add(cameraPosition, targetDirection);
        this.camera.transform().setPosition(newPosition);
    }
}