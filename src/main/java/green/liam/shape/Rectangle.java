package green.liam.shape;

import green.liam.base.Transform;
import processing.core.PVector;

public class Rectangle extends Shape {

  protected float width = 1f;
  protected float height = 1f;

  public Rectangle(float width, float height) {
    super();
    this.width = width;
    this.height = height;
    this.createVerticesAndEdges();
  }

  public Rectangle(Transform parent, float width, float height) {
    super(parent);
    this.width = width;
    this.height = height;
    this.createVerticesAndEdges();
  }

  public float width() {
    return this.width;
  }

  public void setWidth(float width) {
    this.width = width;
    this.createVerticesAndEdges();
  }

  public float height() {
    return this.height;
  }

  public void setHeight(float height) {
    this.height = height;
    this.createVerticesAndEdges();
  }

  protected void createVerticesAndEdges() {
    this.vertices = new Vertex[4];
    this.edges = new Edge[4];
    PVector topLeft = new PVector(-this.width / 2, this.height / 2);
    PVector topRight = new PVector(this.width / 2, this.height / 2);
    PVector bottomRight = new PVector(this.width / 2, -this.height / 2);
    PVector bottomLeft = new PVector(-this.width / 2, -this.height / 2);
    this.vertices[0] = new Vertex(this.transform, topLeft, 0);
    this.vertices[1] = new Vertex(this.transform, topRight, 0);
    this.vertices[2] = new Vertex(this.transform, bottomRight, 0);
    this.vertices[3] = new Vertex(this.transform, bottomLeft, 0);
    this.edges[0] = new Edge(this.vertices[0], this.vertices[1]);
    this.edges[1] = new Edge(this.vertices[1], this.vertices[2]);
    this.edges[2] = new Edge(this.vertices[2], this.vertices[3]);
    this.edges[3] = new Edge(this.vertices[3], this.vertices[0]);
  }
}
