package green.liam.shape;

import green.liam.base.Game;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import green.liam.rendering.CompositeRenderable;
import green.liam.rendering.Renderable;
import java.util.Collection;
import java.util.Set;
import processing.core.PApplet;
import processing.core.PVector;

public class Box extends Shape implements CompositeRenderable {

  float width;
  float length;
  float height;
  Quad[] faces = new Quad[5]; // bottom quad can never be seen from fixed view pitch angle

  public Box(float width, float length, float height) {
    super();
    this.width = width;
    this.length = length;
    this.height = height;
    this.createVertices();
    this.createEdges();
    this.createFaces();
  }

  public Box(Transform parent, float width, float length, float height) {
    super(parent);
    this.width = width;
    this.length = length;
    this.height = height;
    this.createVertices();
    this.createEdges();
    this.createFaces();
  }

  public float width() {
    return this.width;
  }

  public void setWidth(float width) {
    this.width = width;
    this.createVertices();
    this.createEdges();
    this.createFaces();
  }

  public float length() {
    return this.length;
  }

  public void setLength(float length) {
    this.length = length;
    this.createVertices();
    this.createEdges();
    this.createFaces();
  }

  public float height() {
    return this.height;
  }

  public void setHeight(float height) {
    this.height = height;
    this.createVertices();
    this.createEdges();
    this.createFaces();
  }

  protected void createVertices() {
    this.vertices = new Vertex[8];
    PVector bottomFrontLeft = new PVector(-this.width / 2, -this.length / 2);
    PVector bottomFrontRight = new PVector(this.width / 2, -this.length / 2);
    PVector bottomBackRight = new PVector(this.width / 2, this.length / 2);
    PVector bottomBackLeft = new PVector(-this.width / 2, this.length / 2);
    this.vertices[0] = new Vertex(this.transform, bottomFrontLeft, 0);
    this.vertices[1] = new Vertex(this.transform, bottomFrontRight, 0);
    this.vertices[2] = new Vertex(this.transform, bottomBackRight, 0);
    this.vertices[3] = new Vertex(this.transform, bottomBackLeft, 0);
    // copy bottom 4 vertices, change height
    this.vertices[4] = this.vertices[0].copy().setHeight(this.height);
    this.vertices[5] = this.vertices[1].copy().setHeight(this.height);
    this.vertices[6] = this.vertices[2].copy().setHeight(this.height);
    this.vertices[7] = this.vertices[3].copy().setHeight(this.height);
  }

  protected void createEdges() {
    this.edges = new Edge[12];
    // bottom edges
    this.edges[0] = new Edge(this.vertices[0], this.vertices[1]); // front left
    this.edges[1] = new Edge(this.vertices[1], this.vertices[2]); // front right
    this.edges[2] = new Edge(this.vertices[2], this.vertices[3]); // back right
    this.edges[3] = new Edge(this.vertices[3], this.vertices[0]); // back left
    // top edges
    this.edges[4] = new Edge(this.vertices[4], this.vertices[5]); // front left
    this.edges[5] = new Edge(this.vertices[5], this.vertices[6]); // front right
    this.edges[6] = new Edge(this.vertices[6], this.vertices[7]); // back right
    this.edges[7] = new Edge(this.vertices[7], this.vertices[4]); // back left
    // vertical edges
    this.edges[8] = new Edge(this.vertices[0], this.vertices[4]); // front left
    this.edges[9] = new Edge(this.vertices[1], this.vertices[5]); // front right
    this.edges[10] = new Edge(this.vertices[2], this.vertices[6]); // back right
    this.edges[11] = new Edge(this.vertices[3], this.vertices[7]); // back left
  }

  protected void createFaces() {
    this.faces = new Quad[5];
    // top face
    this.faces[0] =
      new Quad(this.edges[4], this.edges[5], this.edges[6], this.edges[7]);
    // front face
    this.faces[1] =
      new Quad(this.edges[0], this.edges[9], this.edges[5], this.edges[4])
        .setIsVertical(this.edges[0]);
    // right face
    this.faces[2] =
      new Quad(this.edges[1], this.edges[10], this.edges[6], this.edges[5])
        .setIsVertical(this.edges[1]);
    // back face
    this.faces[3] =
      new Quad(this.edges[2], this.edges[11], this.edges[7], this.edges[6])
        .setIsVertical(this.edges[2]);
    // left face
    this.faces[4] =
      new Quad(this.edges[3], this.edges[8], this.edges[4], this.edges[7])
        .setIsVertical(this.edges[3]);
  }

  public Quad[] getFaces() {
    return this.faces;
  }

  @Override
  public void render(PApplet game) {
    this.faces[4].render(game);
    this.faces[3].render(game);
    this.faces[2].render(game);
    this.faces[1].render(game);
    this.faces[0].render(game);
  }

  @Override
  public Collection<Renderable> getRenderables() {
    return Set.of(this.faces);
  }

  public float getAverageDepth() {
    float depthSum = 0;
    Camera camera = Game.getInstance().getCamera();
    for (Quad face : this.faces) {
      depthSum += face.getDepth(camera);
    }
    return depthSum / this.faces.length;
  }
}
