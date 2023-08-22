package green.liam.base;

import processing.core.PVector;

/**
 * The root transform is a special transform that acts as the root of the transform hierarchy.
 */
public class RootTransform extends Transform {

  RootTransform() {}

  @Override
  public PVector position() {
    return new PVector();
  }

  @Override
  public float rotation() {
    return 0f;
  }

  @Override
  public PVector scale() {
    return new PVector(1, 1, 1);
  }

  @Override
  public float height() {
    return 0f;
  }

  @Override
  public float yScale() {
    return 1f;
  }

  @Override
  public GameObject gameObject() {
    throw new UnsupportedOperationException(
      "Root transform does not have an associated game object"
    );
  }

  @Override
  public void setParent(Transform parent) {
    throw new UnsupportedOperationException(
      "Root transform cannot have a parent"
    );
  }

  @Override
  public void setPosition(PVector position) {
    throw new UnsupportedOperationException(
      "Root transform cannot have a position"
    );
  }

  @Override
  public void setRotation(float rotation) {
    throw new UnsupportedOperationException(
      "Root transform cannot have a rotation"
    );
  }

  @Override
  public void setScale(PVector scale) {
    throw new UnsupportedOperationException(
      "Root transform cannot have a scale"
    );
  }
}
