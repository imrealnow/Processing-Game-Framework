package green.liam.shape;

import java.util.HashSet;
import java.util.Set;

import green.liam.base.Game;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import green.liam.rendering.Renderable;
import green.liam.rendering.lighting.GlobalLight;
import green.liam.rendering.lighting.ShadowCaster;
import processing.core.PApplet;
import processing.core.PVector;

public class Sphere extends Shape {
    protected float radius;
    protected Vertex center;
    protected float[] fillColour = new float[] { 100, 100, 100, 255 };
    protected float[] strokeColour = new float[] { 50, 50, 50, 255 };
    protected boolean castShadow = true;

    private ShadowEllipse shadowEllipse;

    public Sphere(float radius) {
        super();
        this.radius = radius;
        this.center = new Vertex(this.transform, new PVector(0, 0), radius);
    }

    public Sphere(Transform parent, float radius) {
        super(parent);
        this.radius = radius;
    }

    public Vertex centerVertex() {
        return this.center;
    }

    public float radius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        this.center = new Vertex(this.transform, new PVector(0, 0), radius);
    }

    @Override
    public void render(PApplet game) {
        game.stroke(this.strokeColour[0], this.strokeColour[1], this.strokeColour[2], this.strokeColour[3]);
        game.fill(this.fillColour[0], this.fillColour[1], this.fillColour[2], this.fillColour[3]);
        game.ellipse(this.center.translatedPosition().x, this.center.translatedPosition().y, this.radius * 2,
                this.radius * 2);
    }

    @Override
    public float getDepth(Camera camera) {
        float cameraAlpha = camera.depthAlpha();
        float yPos = this.center.translatedPosition().y - this.center.height();
        return cameraAlpha * -this.center.height() + (1 - cameraAlpha) * yPos;
    }

    @Override
    public Set<Renderable> getChildren() {
        if (this.castShadow) {
            this.shadowEllipse = ShadowCaster.castSphere(this.center, this.radius, this.transform, GlobalLight.Dawn);
            return Set.of(this.shadowEllipse);
        }
        return Set.of();
    }
}
