package green.liam.shape;

import green.liam.base.Transform;
import green.liam.rendering.Camera;
import processing.core.PApplet;
import processing.core.PVector;

public class ShadowEllipse extends Sphere {
    PVector offset;
    Vertex offsetVertex;
    float[] fillColour = new float[] { 0, 0, 0, 100 };

    public ShadowEllipse(Transform transform, float radius, PVector offset, float[] fillColour) {
        super(transform, radius);
        this.offset = offset;
        this.offsetVertex = new Vertex(this.transform, offset, 0);
        this.fillColour = fillColour;
    }

    @Override
    public void render(PApplet game) {
        game.noStroke();
        game.fill(this.fillColour[0], this.fillColour[1], this.fillColour[2], this.fillColour[3]);
        PVector pos = this.offsetVertex.translatedPosition();
        game.ellipse(pos.x, pos.y, this.radius * 2, this.radius * 2);
    }

    @Override
    public int getRenderLayer() {
        return 0;
    }

    @Override
    public float getDepth(Camera camera) {
        return Float.NEGATIVE_INFINITY;
    }
}
