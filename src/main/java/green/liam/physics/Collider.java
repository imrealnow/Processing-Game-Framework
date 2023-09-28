package green.liam.physics;

import green.liam.base.Component;
import green.liam.base.GameObject;
import processing.core.PVector;

public abstract class Collider extends Component {

  protected PVector centerOffset;

  public Collider(GameObject gameObject) {
    super(gameObject);
    this.centerOffset = gameObject.transform().position();
  }

  public PVector centerOffset() {
    return this.centerOffset;
  }

  public Collider setCenterOffset(PVector center) {
    this.centerOffset = center;
    return this;
  }

  public abstract boolean collidesWith(Collider other);

  public abstract boolean isPointInside(PVector point);

  public abstract PVector closestPointTo(PVector point);
}
