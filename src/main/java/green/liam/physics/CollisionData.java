package green.liam.physics;

import processing.core.PVector;

public class CollisionData {
    private Collider otherCollider;
    private PVector collisionNormal;
    private PVector collisionPoint;
    private float penetrationDepth;
    private PVector relativeVelocity;

    // Private constructor to enforce the use of factory methods.
    public CollisionData(Collider otherCollider, PVector collisionNormal, PVector collisionPoint,
            float penetrationDepth, PVector relativeVelocity) {
        this.otherCollider = otherCollider;
        this.collisionNormal = collisionNormal;
        this.collisionPoint = collisionPoint;
        this.penetrationDepth = penetrationDepth;
        this.relativeVelocity = relativeVelocity;
    }

    public static CollisionData reverse(CollisionData data, Collider originalCollider) {
        PVector reversedCollisionNormal = data.getCollisionNormal().copy().mult(-1);
        return new CollisionData(originalCollider, reversedCollisionNormal,
                data.getCollisionPoint(),
                data.getPenetrationDepth(), data.getRelativeVelocity().copy().mult(-1));
    }

    public Collider getOtherCollider() {
        return this.otherCollider;
    }

    public PVector getCollisionNormal() {
        return this.collisionNormal;
    }

    public PVector getCollisionPoint() {
        return this.collisionPoint;
    }

    public PVector getRelativeVelocity() {
        return this.relativeVelocity;
    }

    public float getPenetrationDepth() {
        return this.penetrationDepth;
    }
}
