package green.liam.rendering;

import green.liam.base.Game;
import processing.core.PApplet;

public interface Renderable extends Comparable<Renderable> {
  void render(PApplet applet);

  /**
   * Returns the transformed center y position of the object on the ground.
   *
   * @return transformed center y position of the object on the ground.
   */
  float getDepth(Camera camera);

  @Override
  default int compareTo(Renderable other) {
    Camera camera = Game.getInstance().getCamera();
    return Float.compare(this.getDepth(camera), other.getDepth(camera));
  }
}
