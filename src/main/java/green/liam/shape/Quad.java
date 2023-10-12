package green.liam.shape;

import java.util.Set;

import green.liam.base.Game;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import green.liam.rendering.Renderable;
import green.liam.rendering.lighting.GlobalLight;
import green.liam.rendering.lighting.ShadowCaster;
import green.liam.util.Helper;
import green.liam.util.Pair;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Quad implements Renderable {
  private static PImage blankTexture = null;
  static {
    blankTexture = new PImage(1, 1, PApplet.RGB);
    blankTexture.pixels[0] = 0xffffffff;
  }

  Edge[] edges = new Edge[4];
  protected PVector uvOffset = new PVector(0, 0);
  protected PVector uvScale = new PVector(1, 1);
  boolean isLit = false;
  boolean isVertical = false;
  boolean castShadow = false;
  boolean drawStroke = true;
  boolean overrideDepth = false;
  boolean visibilityOverride = false;
  float depthOverride;
  Quad shadowQuad;
  float[] fillColour = new float[] { 150, 150, 150, 255 };
  float[] shadowColour = new float[] { 160, 160, 200, 255 };
  float[] strokeColour = new float[] { 50, 50, 50, 255 };
  PImage texture;
  Edge leadingEdge;
  PVector normal = new PVector(0, -1, 0); // default normal is up
  Vertex min;
  Vertex max;
  PVector dimensions;
  PVector[] vertexUVs = new PVector[] {
      new PVector(0, 0),
      new PVector(1, 0),
      new PVector(1, 1),
      new PVector(0, 1),
  };
  boolean textureSet = false;

  public Quad(Edge[] edges) {
    this(edges[0], edges[1], edges[2], edges[3]);
  }

  public Quad(Edge e1, Edge e2, Edge e3, Edge e4) {
    this.edges[0] = e1;
    this.edges[1] = e2;
    this.edges[2] = e3;
    this.edges[3] = e4;
    this.min = this.edges[0].end();
    this.max = this.edges[2].end();
    this.initialise();
  }

  public Quad(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
    this.edges[0] = new Edge(v1, v2);
    this.edges[1] = new Edge(v2, v3);
    this.edges[2] = new Edge(v3, v4);
    this.edges[3] = new Edge(v4, v1);
    this.min = v2;
    this.max = v4;
    this.initialise();
  }

  public Quad(Vertex[] vertices) {
    this(vertices[0], vertices[1], vertices[2], vertices[3]);
  }

  public Quad(Transform transform, float width, float length) {
    float halfWidth = width / 2;
    float halfLength = length / 2;
    Vertex[] vertices = new Vertex[4];
    vertices[0] = new Vertex(transform, new PVector(-halfWidth, -halfLength, 0), 0);
    vertices[1] = new Vertex(transform, new PVector(halfWidth, -halfLength, 0), 0);
    vertices[2] = new Vertex(transform, new PVector(halfWidth, halfLength, 0), 0);
    vertices[3] = new Vertex(transform, new PVector(-halfWidth, halfLength, 0), 0);
    this.edges[0] = new Edge(vertices[0], vertices[1]);
    this.edges[1] = new Edge(vertices[1], vertices[2]);
    this.edges[2] = new Edge(vertices[2], vertices[3]);
    this.edges[3] = new Edge(vertices[3], vertices[0]);
    this.min = vertices[0];
    this.max = vertices[2];
    this.initialise();
  }

  public void destroy() {
    Game.getInstance().forceCacheRemoval(this.texture);
    for (Edge edge : this.edges) {
      edge.destroy();
    }
    this.edges = null;
    this.min = null;
    this.max = null;
    this.normal = null;
    this.texture = null;
    this.leadingEdge = null;
    this.shadowQuad = null;
    this.vertexUVs = null;
    this.uvOffset = null;
    this.uvScale = null;
    this.dimensions = null;
  }

  private void initialise() {
    this.normal = this.calculateNormal();
    this.texture = blankTexture;
  }

  public Edge[] edges() {
    return this.edges;
  }

  public PVector centerPosition() {
    PVector center = new PVector();
    for (Vertex v : this.vertices()) {
      center.add(v.worldPosition());
    }
    center.div(this.vertices().length);
    return center;
  }

  public PVector translatedCenter() {
    PVector center = new PVector();
    for (Vertex v : this.vertices()) {
      center.add(v.translatedPosition());
    }
    center.div(this.vertices().length);
    return center;
  }

  /**
   * Returns true if the given point is inside the quad (using the assumption that
   * all vertices exist on a plane)
   * 
   * @param point
   * @return
   */
  public boolean isPointInside(PVector point) {
    PVector[] vertices = new PVector[4];
    for (int i = 0; i < vertices.length; i++) {
      vertices[i] = this.edges[i].start().worldPosition();
    }
    PVector v0 = vertices[3].copy().sub(vertices[0]);
    PVector v1 = vertices[1].copy().sub(vertices[0]);
    PVector v2 = point.copy().sub(vertices[0]);

    float dot00 = PVector.dot(v0, v0);
    float dot01 = PVector.dot(v0, v1);
    float dot02 = PVector.dot(v0, v2);
    float dot11 = PVector.dot(v1, v1);
    float dot12 = PVector.dot(v1, v2);

    float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    return (u >= 0) && (v >= 0) && (u + v < 1);
  }

  /**
   * Returns the closest point on one of the edges of the quad to the given point
   * 
   * @param point
   * @return
   */
  public PVector closestPointTo(PVector point) {
    PVector closestPoint = null;
    float closestDistance = Float.MAX_VALUE;
    for (Edge edge : this.edges) {
      PVector edgePoint = edge.closestPointTo(point);
      float distance = edgePoint.dist(point);
      if (distance < closestDistance) {
        closestDistance = distance;
        closestPoint = edgePoint;
      }
    }
    return closestPoint;
  }

  public Transform transform() {
    return this.vertices()[0].transform();
  }

  private PVector calculateNormal() {
    PVector edge1 = this.edges[0].toVector();
    PVector edge2 = this.edges[1].toVector();
    PVector normal = edge1.cross(edge2);
    normal.normalize();
    return normal;
  }

  public Vertex[] vertices() {
    Vertex[] vertices = new Vertex[4];
    vertices[0] = this.edges[0].start().copy();
    vertices[1] = this.edges[1].start().copy();
    vertices[2] = this.edges[2].start().copy();
    vertices[3] = this.edges[3].start().copy();

    return vertices;
  }

  public PVector normal() {
    return this.normal;
  }

  public Edge leadingEdge() {
    if (!this.isVertical) {
      throw new RuntimeException("Quad is not vertical");
    }
    return this.leadingEdge;
  }

  public Quad setIsVertical(Edge leadingEdge) {
    this.isVertical = true;
    this.leadingEdge = leadingEdge;
    return this;
  }

  public Quad setIsLit(boolean isLit) {
    this.isLit = isLit;
    return this;
  }

  public Quad setVisibilityOverride(boolean visibilityOverride) {
    this.visibilityOverride = visibilityOverride;
    return this;
  }

  public Quad setTexture(PImage texture) {
    this.texture = texture;
    this.textureSet = true;
    return this;
  }

  public void setCastShadow(boolean castShadow) {
    this.castShadow = castShadow;
  }

  public void setDrawStroke(boolean drawStroke) {
    this.drawStroke = drawStroke;
  }

  public void setDepthOverride(float depthOverride) {
    this.overrideDepth = true;
    this.depthOverride = depthOverride;
  }

  public void setFillColour(float[] fillColour) {
    this.fillColour = fillColour;
  }

  public void setShadowColour(float[] shadowColour) {
    this.shadowColour = shadowColour;
  }

  public void setStrokeColour(float[] strokeColour) {
    this.strokeColour = strokeColour;
  }

  public Quad setUVScale(PVector uvScale) {
    this.uvScale = uvScale;
    return this;
  }

  public Quad setUVOffset(PVector uvOffset) {
    this.uvOffset = uvOffset;
    return this;
  }

  private float lightLevel(GlobalLight light) {
    PVector lightDirection = light.getDirectionVector(PApplet.radians(15));
    float lightLevel = PApplet.constrain(
        PVector.dot(this.normal, lightDirection),
        0f,
        1);
    return lightLevel;
  }

  @Override
  public void render(PApplet game) {
    if (!this.cameraCanSee())
      return;
    if (this.drawStroke) {
      game.stroke(this.strokeColour[0], this.strokeColour[1], this.strokeColour[2], this.strokeColour[3]);
      game.strokeWeight(2);
      game.strokeCap(PApplet.PROJECT);
    } else {
      game.noStroke();
    }
    // calculate and apply quad lighting
    float lightLevel = this.lightLevel(GlobalLight.Dawn);
    if (this.isLit) {
      float[] colour = Helper.colourLerp(
          this.fillColour,
          this.shadowColour,
          lightLevel);
      game.tint(colour[0], colour[1], colour[2], colour[3]);
    } else {
      if (this.textureSet)
        game.tint(255, 255, 255, 255);
      else
        game.tint(this.fillColour[0], this.fillColour[1], this.fillColour[2], this.fillColour[3]);
    }
    game.beginShape();
    game.texture(this.texture);
    Vertex[] quadVertices = this.vertices();
    for (int i = 0; i < quadVertices.length; i++) {
      PVector pos = quadVertices[i].translatedPosition();
      // Calculate UVs based on position of vertex relative to quad's dimensions
      pos = Helper.roundPVector(pos, 2);
      if (this.texture != null) {
        PVector uv = this.vertexUVs[i].copy();
        uv.x *= this.uvScale.x;
        uv.y *= this.uvScale.y;
        uv.sub(this.uvOffset);
        game.vertex(pos.x, pos.y, uv.x, uv.y);
      } else
        game.vertex(pos.x, pos.y);
    }

    game.endShape(PApplet.CLOSE);
  }

  @Override
  public float getDepth(Camera camera) {
    if (this.overrideDepth)
      return this.depthOverride;
    float minHeight = Float.MAX_VALUE;
    float maxHeight = Float.MIN_VALUE;
    float minY = Float.MAX_VALUE;
    float heightSum = 0;
    for (Vertex v : this.vertices()) {
      float height = v.height();
      if (height < minHeight)
        minHeight = height;
      if (height > maxHeight)
        maxHeight = height;
      heightSum += height;
      float yPos = v.translatedPosition().y - v.height();
      if (yPos < minY)
        minY = yPos;
    }
    float averageHeight = heightSum / this.vertices().length;
    float alpha = camera.depthAlpha();
    float depth = alpha * -averageHeight + (1 - alpha) * minY;
    return depth;
  }

  @Override
  public Set<Renderable> getChildren() {
    if (this.castShadow) {
      this.shadowQuad = ShadowCaster.castQuad(this.vertices(), this.transform(), GlobalLight.Dawn);
      this.shadowQuad.setFillColour(new float[] { 100, 100, 100, 255 });
      this.shadowQuad.setIsLit(false);
      this.shadowQuad.setDrawStroke(false);
      return Set.of(this.shadowQuad);
    }
    return Set.of();
  }

  public Pair<PVector, PVector> getCenterLine() {
    float leftLength = this.edges[0].length();
    float topLength = this.edges[1].length();
    if (leftLength > topLength) {
      return new Pair<>(this.edges[1].translatedCenter(), this.edges[3].translatedCenter());
    } else {
      return new Pair<>(this.edges[0].translatedCenter(), this.edges[2].translatedCenter());
    }
  }

  public void drawCenterLine(PApplet game) {
    Pair<PVector, PVector> centerLine = this.getCenterLine();
    game.stroke(255, 0, 0);
    game.strokeWeight(2);
    game.line(
        centerLine.first().x,
        centerLine.first().y,
        centerLine.second().x,
        centerLine.second().y);
  }

  private boolean isWithinScreenBounds() {
    PVector screenDimensions = Game.getInstance().getScreenDimensions();
    Vertex[] vertices = this.vertices();
    for (Vertex vertex : vertices) {
      PVector position = vertex.translatedPosition();
      if (position.x > 0 && position.x < screenDimensions.x && position.y > 0 && position.y < screenDimensions.y) {
        return true;
      }
    }
    return false;
  }

  public boolean cameraCanSee() {
    if (this.visibilityOverride)
      return true;
    boolean isWithinScreenBounds = this.isWithinScreenBounds();
    if (this.isVertical) {
      PVector cameraDirection = new PVector(0, -1, 0);
      PVector quadDirection = this.leadingEdge.normal();
      float dot = PVector.dot(cameraDirection, quadDirection);
      return dot < 0 && isWithinScreenBounds;
    }
    return isWithinScreenBounds;
  }
}
