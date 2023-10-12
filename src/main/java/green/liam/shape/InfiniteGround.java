package green.liam.shape;

import green.liam.base.Transform;
import green.liam.rendering.Camera;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class InfiniteGround extends Shape {

  static final float RADIUS = 2000;

  Transform followTarget;
  PImage texture;
  float scale;
  Quad quad;

  public InfiniteGround(PImage texture, float scale) {
    super();
    this.texture = texture;
    this.scale = scale;
    this.createQuad();
    this.quad.setTexture(this.texture);
    this.quad.setUVScale(new PVector(this.scale, this.scale));
    this.quad.castShadow = false;
    this.quad.setVisibilityOverride(true);
  }

  public InfiniteGround(Transform parent, PImage texture, float scale) {
    this(texture, scale);
    this.transform.setParent(parent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    this.quad.destroy();
    this.quad = null;
    this.texture = null;
    this.followTarget = null;
  }

  private void createQuad() {
    this.vertices = new Vertex[4];
    this.edges = new Edge[4];
    this.vertices[0] = new Vertex(this.transform, new PVector(-RADIUS, -RADIUS), 0);
    this.vertices[1] = new Vertex(this.transform, new PVector(RADIUS, -RADIUS), 0);
    this.vertices[2] = new Vertex(this.transform, new PVector(RADIUS, RADIUS), 0);
    this.vertices[3] = new Vertex(this.transform, new PVector(-RADIUS, RADIUS), 0);
    this.edges[0] = new Edge(this.vertices[0], this.vertices[1]);
    this.edges[1] = new Edge(this.vertices[1], this.vertices[2]);
    this.edges[2] = new Edge(this.vertices[2], this.vertices[3]);
    this.edges[3] = new Edge(this.vertices[3], this.vertices[0]);
    Quad quad = new Quad(
        this.vertices[0],
        this.vertices[1],
        this.vertices[2],
        this.vertices[3]);
    quad.setIsLit(false);
    quad.setFillColour(new float[] { 255, 255, 255, 255 });
    this.quad = quad;
  }

  public void setFollowTarget(Transform followTarget) {
    this.followTarget = followTarget;
  }

  public void setTexture(PImage texture) {
    this.texture = texture;
    this.quad.setTexture(this.texture);
  }

  @Override
  public void render(PApplet game) {
    if (this.followTarget != null) {
      this.transform.setPosition(this.followTarget.position());
    }
    float scaleDeterminant = this.texture.width / (RADIUS * 2f) + Float.MIN_VALUE;
    this.quad.setUVOffset(this.followTarget.position().mult(-scaleDeterminant));
    this.quad.render(game);
  }

  @Override
  public float getDepth(Camera camera) {
    return Float.NEGATIVE_INFINITY;
  }

  @Override
  public int getRenderLayer() {
    return -2;
  }
}
