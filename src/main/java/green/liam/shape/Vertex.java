package green.liam.shape;

import green.liam.base.Transform;
import processing.core.PVector;

public class Vertex {
    PVector localPosition;
    float height;
    final Transform transform;

    public Vertex(Transform transform, PVector localPosition, float height) {
        this.transform = transform;
        this.localPosition = localPosition;
        this.height = height;
    }

    public Vertex copy() {
        return new Vertex(this.transform, this.localPosition.copy(), this.height);
    }

    public PVector position() {
        return this.transform.transformVertex(this.localPosition);
    }

    public PVector translatedPosition() {
        PVector position = this.position();
        return Transform.inverseTranslateVector(position).add(0,
                -(this.height + this.transform.height()) * this.transform.yScale());
    }

    public PVector localPosition() {
        return this.localPosition;
    }

    public Vertex setLocalPosition(PVector localPosition) {
        this.localPosition = localPosition;
        return this;
    }

    public float height() {
        return this.height;
    }

    public Vertex setHeight(float height) {
        this.height = height;
        return this;
    }
}
