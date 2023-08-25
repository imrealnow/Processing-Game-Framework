package green.liam.shape;

import green.liam.base.Game;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import green.liam.rendering.CompositeRenderable;
import green.liam.rendering.Renderable;
import green.liam.rendering.lighting.GlobalLight;
import green.liam.rendering.lighting.QuadShadow;
import green.liam.util.Helper;
import java.util.Collection;
import java.util.List;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Quad implements CompositeRenderable {

  final Edge[] edges = new Edge[4];
  protected PVector uvOffset = new PVector(0, 0);
  protected PVector uvScale = new PVector(32, 32);
  boolean isLit = true;
  boolean isVertical = false;
  boolean castShadow = true;
  boolean drawStroke = false;
  boolean overrideDepth = false;
  float depthOverride;
  Quad shadowQuad;
  float[] fillColour = new float[] { 150, 150, 150, 255 };
  float[] shadowColour = new float[] { 98, 105, 82, 255 };
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

  private void initialise() {
    this.normal = this.calculateNormal();
    PImage blankTexture = Game.getInstance().createImage(1, 1, PApplet.RGB);
    blankTexture.pixels[0] = Game.getInstance().color(255, 255, 255);
    this.texture = blankTexture;
  }

  public Edge[] edges() {
    return this.edges;
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

  public Quad setTexture(PImage texture) {
    this.texture = texture;
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
      1
    );
    return lightLevel;
  }

  @Override
  public void render(PApplet game) {
    if (!this.cameraCanSee()) return;
    if (this.drawStroke) {
      game.stroke(200);
    } else {
      game.noStroke();
    }
    // calculate and apply quad lighting
    float lightLevel = this.lightLevel(GlobalLight.Dawn);
    if (this.isLit) {
      float[] colour = Helper.colourLerp(
        this.fillColour,
        this.shadowColour,
        lightLevel
      );
      game.tint(colour[0], colour[1], colour[2], colour[3]);
    } else {
      game.tint(255, 255, 255, 255);
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
      } else game.vertex(pos.x, pos.y);
    }

    game.endShape(Game.CLOSE);
  }

  @Override
  public float getDepth(Camera camera) {
    if (this.overrideDepth) return this.depthOverride;
    float averageHeight = 0;
    float averagePosition = 0;

    for (Vertex v : this.vertices()) {
      averageHeight -= v.height();
      averagePosition +=
        v.translatedPosition().sub(new PVector(0, v.height())).y;
    }
    averageHeight /= this.vertices().length;
    averagePosition /= this.vertices().length;

    float alpha = camera.depthAlpha();
    float depth = alpha * averageHeight + (1 - alpha) * averagePosition;

    return depth;
  }

  private boolean cameraCanSee() {
    if (!this.isVertical) return true;
    PVector cameraDirection = new PVector(0, -1, 0);
    PVector quadDirection = this.leadingEdge.normal();
    float dot = PVector.dot(cameraDirection, quadDirection);
    return dot < 0;
  }

  @Override
  public Collection<Renderable> getRenderables() {
    if (this.castShadow) {
      this.shadowQuad =
        QuadShadow.cast(this.vertices(), this.transform(), GlobalLight.Dawn);
      this.shadowQuad.setDrawStroke(false);
      this.shadowQuad.setFillColour(this.shadowColour);
      return List.of(this, this.shadowQuad);
    }
    return List.of(this);
  }
}
