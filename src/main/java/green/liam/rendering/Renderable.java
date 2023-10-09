package green.liam.rendering;

import java.util.HashSet;
import java.util.Set;

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

  default int getRenderLayer() {
    return 0;
  }

  @Override
  default int compareTo(Renderable other) {
    if (this.getRenderLayer() != other.getRenderLayer()) {
      return Integer.compare(this.getRenderLayer(), other.getRenderLayer());
    }
    Camera camera = Game.getInstance().getCamera();
    return Float.compare(this.getDepth(camera), other.getDepth(camera));
  }

  default Set<Renderable> getChildren() {
    return new HashSet<>();
  }
}
