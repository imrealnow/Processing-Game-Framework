package green.liam.base;

import java.util.HashSet;
import java.util.Set;
import green.liam.rendering.Camera;
import green.liam.util.Helper;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Transform extends Component {
    protected PVector position = new PVector();
    protected float height = 0f;
    protected float rotation = 0f;
    protected PVector scale = new PVector(1, 1, 1);
    protected Transform parent;
    protected Set<Transform> children = new HashSet<>();

    protected PMatrix2D scaleMatrix = new PMatrix2D();
    protected PMatrix2D rotationMatrix = new PMatrix2D();
    protected PMatrix2D translationMatrix = new PMatrix2D();
    protected PMatrix2D transformationMatrix = new PMatrix2D();

    public Transform(GameObject gameObject) {
        super(gameObject);
        this.recalculateMatrix();
    }

    public Transform(GameObject gameObject, Transform parent) {
        super(gameObject);
        this.parent = parent;
        this.parent.addChild(this);
        this.recalculateMatrix();
    }

    public Transform getParent() {
        return this.parent;
    }

    public Set<Transform> getChildren() {
        return this.children;
    }

    public void addChild(Transform child) {
        this.children.add(child);
        child.setParent(this);
    }

    public void removeChild(Transform child) {
        this.children.remove(child);
        child.setParent(null);
    }

    private void updateChildren() {
        for (Transform child : this.children) {
            child.recalculateMatrix();
        }
    }

    @Override
    public GameObject gameObject() {
        return this.gameObject;
    }

    public PVector position() {
        if (this.parent != null)
            return PVector.add(this.parent.position(), this.position);
        return this.position.copy();
    }

    public PVector screenPosition() {
        PVector copy = this.position.copy();
        return Transform.translateVector(copy).add(0, this.height());
    }

    public float rotation() {
        if (this.parent != null)
            return this.parent.rotation() + this.rotation;
        return this.rotation;
    }

    public PVector scale() {
        if (this.parent != null)
            return new PVector(this.parent.scale().x * this.scale.x,
                    this.parent.scale().y * this.scale.y, this.parent.scale().z * this.scale.z);
        return this.scale.copy();
    }

    public float yScale() {
        if (this.parent != null)
            return this.parent.yScale() * this.scale.y;
        return this.scale.y;
    }

    public float height() {
        if (this.parent != null)
            return this.parent.height() + this.height;
        return this.height;
    }

    public float setHeight(float height) {
        this.height = height;
        return this.height;
    }

    public void setParent(Transform parent) {
        this.parent = parent;
        this.recalculateMatrix();
    }

    public void setPosition(PVector position) {
        this.position = position;
        this.recalculateMatrix();
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        this.recalculateMatrix();
    }

    public void setScale(PVector scale) {
        this.scale = scale;
        this.recalculateMatrix();
    }

    public void setLocalPosition(PVector position) {
        if (this.parent != null) {
            PVector parentPosition = this.parent.position();
            this.position = PVector.sub(position, parentPosition);
        } else {
            this.position = position;
        }
        this.recalculateMatrix();
    }

    public void setLocalRotation(float rotation) {
        if (this.parent != null) {
            this.rotation = rotation - this.parent.rotation();
        } else {
            this.rotation = rotation;
        }
        this.recalculateMatrix();
    }

    public void setLocalScale(PVector scale) {
        if (this.parent != null) {
            PVector parentScale = this.parent.scale();

            this.scale = new PVector(Helper.safeDivide(scale.x, parentScale.x),
                    Helper.safeDivide(scale.y, parentScale.y),
                    Helper.safeDivide(scale.z, parentScale.z));
        } else {
            this.scale = scale;
        }
        this.recalculateMatrix();
    }

    private void recalculateMatrix() {
        this.translationMatrix = new PMatrix2D();
        this.scaleMatrix = new PMatrix2D();

        this.scaleMatrix.scale(this.scale.x, this.scale.z);
        this.rotationMatrix.rotate(this.rotation);
        this.translationMatrix.translate(this.position.x, this.position.y);


        PMatrix2D combinedMatrix = new PMatrix2D();
        combinedMatrix.apply(this.scaleMatrix);
        combinedMatrix.apply(this.translationMatrix);

        if (this.parent != null) {
            combinedMatrix.preApply(this.parent.getTransformationMatrix());
        }

        this.transformationMatrix = combinedMatrix;
        this.updateChildren();
    }

    public PMatrix2D getTransformationMatrix() {
        return this.transformationMatrix;
    }

    public PVector transformVertex(PVector vertex) {
        PVector transformedVertex = new PVector();
        PMatrix2D matrixCopy = this.transformationMatrix.get();
        matrixCopy.translate(this.position.x, this.position.y);
        matrixCopy.rotate((float) Math.toRadians(this.rotation));
        matrixCopy.mult(vertex, transformedVertex);
        return transformedVertex;
    }

    public static PVector translateVector(PVector vector) {
        PMatrix2D cameraMatrix = Camera.MAIN.getMatrix();
        PVector translatedVector = new PVector();
        cameraMatrix.mult(vector, translatedVector);
        return translatedVector;
    }

    public static PVector inverseTranslateVector(PVector vector) {
        PMatrix2D cameraMatrix = Camera.MAIN.getMatrix();
        cameraMatrix.invert();
        PVector translatedVector = new PVector();
        cameraMatrix.mult(vector, translatedVector);
        return translatedVector;
    }
}
