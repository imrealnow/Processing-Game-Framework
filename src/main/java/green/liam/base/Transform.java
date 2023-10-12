package green.liam.base;

import green.liam.events.EventManager;
import green.liam.events.EventManagerFactory;
import green.liam.events.Observer;
import green.liam.events.TransformChangeEvent;
import green.liam.events.TransformChangeEvent.ChangeType;
import green.liam.rendering.Camera;
import green.liam.util.Helper;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import processing.core.PApplet;
import processing.core.PMatrix2D;
import processing.core.PVector;

public class Transform extends Component {
  static {
    IDENTITY = new RootTransform();
  }

  protected static Transform IDENTITY;

  protected final EventManager<TransformChangeEvent> changeEventManager = EventManagerFactory.getEventManager(
      TransformChangeEvent.class);

  protected Transform parent;
  protected PVector position = new PVector();
  protected float height = 0f;
  protected float rotation = 0f;
  protected PVector scale = new PVector(1, 1, 1);
  protected Set<Transform> children = new HashSet<>();

  protected PMatrix2D scaleMatrix = new PMatrix2D();
  protected PMatrix2D rotationMatrix = new PMatrix2D();
  protected PMatrix2D translationMatrix = new PMatrix2D();
  protected PMatrix2D combinedMatrix = new PMatrix2D();

  private boolean transformChanged = false;

  public Transform(GameObject gameObject, Transform parent) {
    super(gameObject);
    this.parent = parent;
    if (parent != null)
      this.parent.addChild(this);
    this.recalculateMatrix();
  }

  public Transform(GameObject gameObject) {
    this(gameObject, IDENTITY);
  }

  public Transform() {
    this(null, IDENTITY);
  }

  @Override
  public void onDestroy() {
    this.parent.removeChild(this);
    for (Transform child : this.children) {
      child.setParent(null);
      child.gameObject().onDestroy();
    }
    this.position = null;
    this.scale = null;
    this.children.clear();
    this.children = null;
    this.parent = null;
    this.scaleMatrix = null;
    this.rotationMatrix = null;
    this.translationMatrix = null;
    this.combinedMatrix = null;
  }

  public Transform getParent() {
    return this.parent;
  }

  public Set<Transform> getChildren() {
    return Collections.unmodifiableSet(this.children);
  }

  public void addChild(Transform child) {
    this.children.add(child);
    child.setParent(this);
  }

  public void removeChild(Transform child) {
    this.children.remove(child);
  }

  private void updateChildren() {
    try {
      for (Transform child : this.children) {
        child.recalculateMatrix();
      }
    } catch (ConcurrentModificationException e) {
    }
  }

  public void addChangeObserver(Observer<TransformChangeEvent> observer) {
    this.changeEventManager.addObserver(observer);
  }

  public void removeChangeObserver(Observer<TransformChangeEvent> observer) {
    this.changeEventManager.removeObserver(observer);
  }

  @Override
  public GameObject gameObject() {
    return this.gameObject;
  }

  public PVector position() {
    if (this.parent == null)
      return this.position;
    return PVector.add(this.parent.position(), this.position);
  }

  public PVector screenPosition() {
    PVector copy = this.position.copy();
    return Transform.translateVector(copy).add(0, this.height());
  }

  public float rotation() {
    if (this.parent == null)
      return this.rotation;
    return this.parent.rotation() + this.rotation;
  }

  public float rotationInRadians() {
    return PApplet.radians(this.rotation()) + Float.MIN_VALUE;
  }

  public PVector scale() {
    if (this.parent == null)
      return this.scale;
    PVector parentScale = this.parent.scale();
    return new PVector(
        this.scale.x * parentScale.x,
        this.scale.y * parentScale.y,
        this.scale.z * parentScale.z);
  }

  public static Transform identity() {
    return IDENTITY;
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
    this.changeEventManager.notify(
        new TransformChangeEvent(this, ChangeType.HEIGHT, this.height, height));
    this.height = height;
    return this.height;
  }

  public void setParent(Transform parent) {
    this.changeEventManager.notify(
        new TransformChangeEvent(this, ChangeType.PARENT, this.parent, parent));
    this.parent.children.remove(this);
    this.parent = parent == null ? IDENTITY : parent;
    this.parent.children.add(this);
    this.transformChanged = true;
  }

  public void setPosition(PVector position) {
    this.changeEventManager.notify(
        new TransformChangeEvent(
            this,
            ChangeType.POSITION,
            this.position,
            position));
    this.position = position;
    this.transformChanged = true;
  }

  public void translate(PVector translation) {
    this.changeEventManager.notify(
        new TransformChangeEvent(
            this,
            ChangeType.POSITION,
            this.position,
            PVector.add(this.position, translation)));
    this.position.add(translation);
    this.transformChanged = true;
  }

  public void setRotation(float rotation) {
    this.changeEventManager.notify(
        new TransformChangeEvent(
            this,
            ChangeType.ROTATION,
            this.rotation,
            rotation));
    this.rotation = rotation;
    this.transformChanged = true;
  }

  public void rotate(float rotation) {
    this.changeEventManager.notify(
        new TransformChangeEvent(
            this,
            ChangeType.ROTATION,
            this.rotation,
            this.rotation + rotation));
    this.rotation += rotation;
    this.transformChanged = true;
  }

  public void setScale(PVector scale) {
    this.changeEventManager.notify(
        new TransformChangeEvent(this, ChangeType.SCALE, this.scale, scale));
    this.scale = scale;
    this.transformChanged = true;
  }

  public void setLocalPosition(PVector position) {
    this.setPosition(PVector.sub(position, this.parent.position()));
  }

  public void setLocalRotation(float rotation) {
    this.setRotation(rotation - this.parent.rotation());
  }

  public void setLocalScale(PVector scale) {
    PVector parentScale = this.parent.scale();
    PVector newScale = new PVector(
        Helper.safeDivide(scale.x, parentScale.x),
        Helper.safeDivide(scale.y, parentScale.y),
        Helper.safeDivide(scale.z, parentScale.z));
    this.setScale(newScale);
  }

  private void recalculateMatrix() {
    this.translationMatrix = new PMatrix2D();
    this.scaleMatrix = new PMatrix2D();
    this.rotationMatrix = new PMatrix2D();

    this.scaleMatrix.scale(this.scale.x, this.scale.z);
    this.translationMatrix.translate(this.position.x, this.position.y);
    this.rotationMatrix.rotate(this.rotation);

    this.combinedMatrix = new PMatrix2D();
    this.combinedMatrix.apply(this.scaleMatrix);
    this.combinedMatrix.apply(this.translationMatrix);

    if (this.parent != null) {
      this.combinedMatrix.preApply(this.parent.combinedMatrix);
    }

    this.updateChildren();
  }

  public PMatrix2D getCombinedMatrix() {
    if (this.transformChanged) {
      this.recalculateMatrix();
      this.transformChanged = false;
    }
    return this.combinedMatrix.get();
  }

  public PMatrix2D getInverseMatrix() {
    PMatrix2D inverseTranslationMatrix = new PMatrix2D();
    PMatrix2D inverseScaleMatrix = new PMatrix2D();

    // Invert each transformation
    inverseScaleMatrix.scale(1 / this.scale.x, 1 / this.scale.z);
    inverseTranslationMatrix.translate(-this.position.x, -this.position.y);

    PMatrix2D inverseCombinedMatrix = new PMatrix2D();

    // Apply the inverted transformations in reverse order
    inverseCombinedMatrix.apply(inverseTranslationMatrix);
    inverseCombinedMatrix.apply(inverseScaleMatrix);

    if (this.parent != null) {
      PMatrix2D parentInverseMatrix = this.parent.getInverseMatrix(); // Assuming the parent
      // also has this method
      inverseCombinedMatrix.preApply(parentInverseMatrix);
    }

    return inverseCombinedMatrix;
  }

  public PVector transformVertex(PVector vertex) {
    PVector transformedVertex = new PVector();
    PMatrix2D matrixCopy = this.combinedMatrix.get();
    matrixCopy.mult(vertex, transformedVertex);
    return transformedVertex;
  }

  public static PVector translateVector(PVector vector) {
    PMatrix2D cameraMatrix = Game
        .getInstance()
        .getCamera()
        .transform()
        .getCombinedMatrix();
    PVector translatedVector = new PVector();
    cameraMatrix.mult(vector, translatedVector);
    return translatedVector;
  }

  public static PVector inverseTranslateVector(PVector vector) {
    PMatrix2D cameraMatrix = Game
        .getInstance()
        .getCamera()
        .transform()
        .getCombinedMatrix();
    cameraMatrix.invert();
    PVector translatedVector = new PVector();
    cameraMatrix.mult(vector, translatedVector);
    return translatedVector;
  }
}
