package green.liam.rendering;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PShader;

public class Material {

  PShader shader;
  PImage texture;
  float[] color = new float[] { 1, 1, 1, 1 };

  public Material(PShader shader, PImage texture) {
    this.shader = shader;
    this.texture = texture;
  }

  public void apply(PApplet applet) {
    applet.shader(this.shader);
    this.shader.set("texture", this.texture);
    this.shader.set("color", this.color);
  }
}
