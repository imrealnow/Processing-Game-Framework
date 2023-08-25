package green.liam.shape;

import green.liam.rendering.Camera;

public class ShadowQuad extends Quad {

  public ShadowQuad(Vertex[] vertices) {
    super(vertices);
    this.castShadow = false;
  }

  @Override
  public float getDepth(Camera camera) {
    return Float.NEGATIVE_INFINITY;
  }

  @Override
  public int getRenderLayer() {
    return 0;
  }
}
