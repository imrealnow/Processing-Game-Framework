package green.liam.events;

import green.liam.base.Transform;

public class TransformChangeEvent {
    public enum ChangeType {
        POSITION, ROTATION, SCALE, HEIGHT, PARENT
    }

    public final Transform transform;
    public final ChangeType changeType;
    public final Object oldValue;
    public final Object newValue;

    public TransformChangeEvent(Transform transform, ChangeType changeType, Object oldValue,
            Object newValue) {
        this.transform = transform;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
