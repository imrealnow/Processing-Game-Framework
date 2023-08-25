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

  // Singleton method to get instance
  public static RenderingManager getInstance(PApplet p) {
    if (instance == null) {
      instance = new RenderingManager(p);
    }
    return instance;
  }

  private void initialiseDefaultMaterials() {
    // Initialize defaultShader
    // Example:
    this.defaultShader =
      this.parent.loadShader("pathToVertShader.vert", "pathToFragShader.frag");

    // Initialize defaultTexture
    // Example:
    this.defaultTexture = this.parent.loadImage("pathToDefaultTexture.png");

    // Create and set defaultMaterial using the default shader and texture
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
