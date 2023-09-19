package green.liam.rendering;

import java.util.HashMap;
import java.util.Map;
import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PShader;

public class RenderingManager {

  private static RenderingManager instance;
  private static Map<String, Material> instancedMaterials = new HashMap<>();
  private static Material defaultMaterial;

  Material shadowMaterial;
  private PApplet parent;

  private PImage defaultTexture;
  private PShader defaultShader;

  private RenderingManager(PApplet p) {
    this.parent = p;
    this.initialiseDefaultMaterials();
  }

  public static RenderingManager getInstance(PApplet p) {
    if (instance == null) {
      instance = new RenderingManager(p);
    }
    return instance;
  }

  private void initialiseDefaultMaterials() {
    this.defaultShader =
    this.parent.loadShader("pathToFragShader.frag", "pathToVertShader.vert");

    this.defaultTexture = this.parent.createImage(1, 1, PApplet.RGB);
    this.defaultTexture.pixels[0] = this.parent.color(255, 255, 255);

    defaultMaterial = new Material(this.defaultShader, this.defaultTexture);
    instancedMaterials.put("default", defaultMaterial);
  }

  public Material getMaterial(String materialName) {
    return instancedMaterials.getOrDefault(materialName, defaultMaterial);
  }

  public void addMaterial(String name, Material material) {
    instancedMaterials.put(name, material);
  }

  public Material getDefaultMaterial() {
    return defaultMaterial;
  }
}
