package green.liam.rendering;

import processing.core.PApplet;

public interface Renderable extends Comparable<Renderable> {
    void render(PApplet applet);

    /**
     * Returns the transformed center y position of the object on the ground.
     * 
     * @return transformed center y position of the object on the ground.
     */
    float getDepth();

    @Override
    default int compareTo(Renderable other) {
        return Float.compare(this.getDepth(), other.getDepth());
    }
}
