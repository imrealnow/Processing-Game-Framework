package green.liam.rendering;

import java.util.Collection;

public interface CompositeRenderable extends Renderable {
  public Collection<Renderable> getRenderables();
}
