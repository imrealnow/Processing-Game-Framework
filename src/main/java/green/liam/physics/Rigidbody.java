package green.liam.physics;

import green.liam.base.Component;
import green.liam.base.GameObject;
import green.liam.base.Time;
import green.liam.base.Transform;
import processing.core.PVector;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.List;

public class Rigidbody extends Component {
  public enum RigidbodyType {
    STATIC, DYNAMIC
  }

  protected PVector velocity = new PVector(0, 0, 0);
  protected float drag = 13f;
  protected float mass = 1f;
  protected RigidbodyType type = RigidbodyType.DYNAMIC;

  protected Collider collider;
  protected Transform transform;
  protected Queue<CollisionData> pendingCollisions = new ArrayDeque<>();

  private PVector appliedForce = new PVector(0, 0, 0);

  public Rigidbody(GameObject gameObject) {
    super(gameObject);
    this.collider = gameObject.getComponent(Collider.class);
    this.transform = gameObject.transform();
    if (this.collider == null) {
      throw new RuntimeException("Rigidbody requires a Collider component");
    }
  }

  public Rigidbody addVelocity(PVector force) {
    this.velocity.add(force);
    return this;
  }

  public Rigidbody setVelocity(PVector velocity) {
    this.velocity = velocity;
    return this;
  }

  public Rigidbody setDrag(float drag) {
    this.drag = drag;
    return this;
  }

  public Rigidbody setMass(float mass) {
    this.mass = mass;
    return this;
  }

  public Rigidbody setType(RigidbodyType type) {
    this.type = type;
    return this;
  }

  public PVector velocity() {
    return this.velocity;
  }

  public float drag() {
    return this.drag;
  }

  public RigidbodyType type() {
    return this.type;
  }

  public Rigidbody addForce(PVector force) {
    if (this.type == RigidbodyType.STATIC) {
      return this;
    }
    this.appliedForce.add(force);
    return this;
  }

  private void applyForces(float deltaTime) {
    // F = ma => a = F/m
    PVector acceleration = PVector.div(this.appliedForce, this.mass);
    // Update velocity: v = u + at
    this.velocity.add(PVector.mult(acceleration, deltaTime));
    // Reset appliedForce for next frame
    this.appliedForce.set(0, 0, 0);
  }

  public void moveOutOfCollision(CollisionData collision) {
    PVector positionDelta = collision.getCollisionNormal().copy().mult(collision.getPenetrationDepth() * -0.6f);
    if (this.type == RigidbodyType.STATIC) {
      positionDelta.mult(-1);
      collision.getOtherCollider().transform().translate(positionDelta);
    } else
      this.transform.translate(positionDelta);
  }

  private void applyCollisionImpulse(CollisionData collision) {
    if (this.type == RigidbodyType.STATIC) {
      return;
    }
    Rigidbody otherRigidbody = collision.getOtherCollider().gameObject().getComponent(Rigidbody.class);
    PVector collisionNormal = collision.getCollisionNormal();
    PVector relativeVelocity = collision.getRelativeVelocity();

    float e = 0f; // Coefficient of restitution

    // Calculating the impulse magnitude
    float dampeningFactor = 0.6f;
    float impulseMagnitude = -(1 + e) * dampeningFactor * relativeVelocity.dot(collisionNormal)
        / (1 / this.mass + (otherRigidbody.type() == RigidbodyType.DYNAMIC ? 1 / otherRigidbody.mass : 0));
    PVector impulse = collisionNormal.copy().mult(impulseMagnitude);

    // Applying the impulse to the velocities
    this.addVelocity(impulse.copy().mult(1 / this.mass));

    if (otherRigidbody.type() == RigidbodyType.DYNAMIC) {
      otherRigidbody.addVelocity(impulse.copy().mult(-1 / otherRigidbody.mass));
    }
  }

  private void applyDrag() {
    float dragMagnitude = this.velocity.mag();
    if (dragMagnitude > 0) {
      float dragScalar = this.drag * Time.INSTANCE.deltaTime();
      PVector drag = this.velocity.copy().normalize().mult(-dragScalar);
      this.velocity.add(drag);
    }
    if (this.velocity.mag() < 0.1f) {
      this.velocity = new PVector(0, 0, 0);
    }
  }

  public void queueCollision(CollisionData collision) {
    this.pendingCollisions.add(collision);
  }

  public void handlePendingCollisions() {
    while (!this.pendingCollisions.isEmpty()) {
      CollisionData collision = this.pendingCollisions.remove();
      this.handleCollision(collision);
    }
  }

  public void handleCollision(CollisionData collision) {
    this.applyCollisionImpulse(collision);
  }

  @Override
  public void update() {
    if (this.type == RigidbodyType.STATIC) {
      return;
    }
    this.applyDrag();
    this.applyForces(Time.INSTANCE.deltaTime());
    PVector positionDelta = this.velocity.copy();
    this.transform.translate(positionDelta);
  }

}
