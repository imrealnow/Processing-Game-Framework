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

  protected PVector getRelativeVelocity(Collider other) {
    PVector relativeVelocity = new PVector(0, 0, 0);
    if (this.gameObject.hasComponent(Rigidbody.class)) {
      Rigidbody rigidbody = this.gameObject.getComponent(Rigidbody.class);
      relativeVelocity = rigidbody.velocity().copy();
    }
    if (other.gameObject().hasComponent(Rigidbody.class)) {
      Rigidbody rigidbody = other.gameObject().getComponent(Rigidbody.class);
      relativeVelocity.sub(rigidbody.velocity());
    }
    return relativeVelocity;
  }

  public abstract PVector center();

  public abstract CollisionData collidesWith(Collider other);

  public abstract boolean isPointInside(PVector point);

  public abstract PVector closestPointTo(PVector point);
}
