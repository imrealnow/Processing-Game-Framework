package green.liam.physics;

import green.liam.base.GameObject;
import green.liam.shape.Sphere;
import processing.core.PVector;

public class SphereCollider extends Collider {

    Sphere sphere;

    public SphereCollider(GameObject gameObject, Sphere sphere) {
        super(gameObject);
        this.sphere = sphere;
    }

    @Override
    public PVector center() {
        return this.sphere.centerVertex().worldPosition();
    }

    @Override
    public CollisionData collidesWith(Collider other) {
        if (other instanceof SphereCollider) {
            return this.collidesWithSphere((SphereCollider) other);
        } else if (other instanceof BoxCollider) {
            return this.collidesWithBox((BoxCollider) other);
        } else {
            throw new RuntimeException("Unimplemented collision detection for " + other.getClass().getName());
        }
    }

    @Override
    public boolean isPointInside(PVector point) {
        return this.center().dist(point) < this.sphere.radius();
    }

    @Override
    public PVector closestPointTo(PVector point) {
        PVector direction = PVector.sub(point, this.center());
        direction.normalize();
        direction.mult(this.sphere.radius());
        return PVector.add(this.center(), direction);
    }

    private CollisionData collidesWithSphere(SphereCollider collider) {
        PVector direction = PVector.sub(collider.center(), this.center());
        float distance = direction.mag();
        float radiusSum = this.sphere.radius() + collider.sphere.radius();
        if (distance < radiusSum) {
            PVector normal = PVector.sub(this.center(), collider.center());
            normal.normalize();
            float penetration = radiusSum - distance;
            return new CollisionData(collider, normal, this.closestPointTo(collider.center()), penetration,
                    this.getRelativeVelocity(collider));
        } else {
            return null;
        }
    }

    private CollisionData collidesWithBox(BoxCollider collider) {
        PVector closestPoint = collider.closestPointTo(this.center());
        if (this.isPointInside(closestPoint)) {
            PVector thisClosestPoint = this.closestPointTo(collider.center());
            PVector normal = PVector.sub(closestPoint, this.center());
            normal.normalize();
            float distance = thisClosestPoint.dist(closestPoint);
            return new CollisionData(collider, normal, closestPoint, distance, this.getRelativeVelocity(collider));
        } else {
            return null;
        }
    }
}
