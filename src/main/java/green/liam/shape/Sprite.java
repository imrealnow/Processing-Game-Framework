package green.liam.shape;

import green.liam.base.GameObject;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import green.liam.rendering.Renderable;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Sprite extends GameObject implements Renderable {

  protected PImage spriteImage;
  protected float width;
  protected float height;
  protected float contentHeight = 0f;
  protected float[] color = new float[] { 255f, 255f, 255f, 255f };
  protected Vertex baseVertex;
  protected PVector offset = new PVector();
  boolean drawCentered = true;

  public Sprite(PImage spriteImage, float width, float height) {
    super();
    this.spriteImage = spriteImage;
    this.width = width;
    this.height = height;
    this.baseVertex = new Vertex(this.transform, new PVector(0, 0, 0), 0f);
  }

  public Sprite(
      PImage spriteImage,
      float width,
      float height,
      Transform parent) {
    super(parent);
    this.spriteImage = spriteImage;
    this.width = width;
    this.height = height;
    this.contentHeight = height;
    this.baseVertex = new Vertex(this.transform, new PVector(0, 0, 0), 0f);
  }

  public void setDrawCentered(boolean drawCentered) {
    this.drawCentered = drawCentered;
  }

  public PImage spriteImage() {
    return this.spriteImage;
  }

  public void setOffset(PVector offset) {
    this.offset = offset;
  }

  public void setContentHeight(float contentHeight) {
    this.contentHeight = contentHeight;
  }

  public void setSpriteImage(PImage spriteImage) {
    this.spriteImage = spriteImage;
  }

  public float width() {
    return this.width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float height() {
    return this.height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  @Override
  public void render(PApplet applet) {
    PVector pos = this.baseVertex.translatedPosition();
    applet.tint(this.color[0], this.color[1], this.color[2], this.color[3]);
    if (this.drawCentered)
      applet.imageMode(PApplet.CENTER);
    else
      applet.imageMode(PApplet.CORNER);
    applet.image(this.spriteImage, pos.x + this.offset.x, pos.y + this.offset.y, this.width, this.height);
  }

  @Override
  public float getDepth(Camera camera) {
    float alpha = camera.depthAlpha();
    float yPos = this.baseVertex.translatedPosition().y - this.baseVertex.height();
    return alpha * this.contentHeight * 0.5f * camera.getYScale() + (1 - alpha) * yPos;
  }
}
