package green.liam.shape;

import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import green.liam.rendering.Renderable;
import green.liam.util.Helper;
import processing.core.PApplet;
import processing.core.PVector;

public abstract class Shape extends GameObject implements Renderable {

  protected Edge[] edges;
  protected Vertex[] vertices;

  public Shape() {
    super();
  }

  public Shape(Transform parent) {
    super(parent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (this.edges != null) {
      for (Edge edge : this.edges()) {
        edge.destroy();
      }
    }
    if (this.vertices != null) {
      for (Vertex vertex : this.vertices()) {
        vertex.destroy();
      }
    }
    this.edges = null;
    this.vertices = null;
  }

  public Edge[] edges() {
    return this.edges;
  }

  public Vertex[] vertices() {
    return this.vertices;
  }

  protected void setEdges(Edge[] edges) {
    this.edges = edges;
  }

  protected void setVertices(Vertex[] vertices) {
    this.vertices = vertices;
  }

  @Override
  public Transform transform() {
    return this.transform;
  }

  public void setTransform(Transform transform) {
    this.transform = transform;
  }

  @Override
  public void render(PApplet game) {
    game.beginShape();

    Vertex[] quadVertices = this.vertices();
    for (int i = 0; i < quadVertices.length; i++) {
      PVector pos = quadVertices[i].translatedPosition();
      pos = Helper.roundPVector(pos, 2);
      game.vertex(pos.x, pos.y);
    }
    game.endShape(PApplet.CLOSE);
  }

  @Override
  public float getDepth(Camera camera) {
    PVector pos = this.transform.position();
    return Transform.inverseTranslateVector(pos).y;
  }
}
