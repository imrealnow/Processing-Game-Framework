package green.liam.physics;

import green.liam.base.Component;
import green.liam.base.GameObject;

public abstract class Collider extends Component {
    public Collider(GameObject gameObject) {
        super(gameObject);
    }
}
