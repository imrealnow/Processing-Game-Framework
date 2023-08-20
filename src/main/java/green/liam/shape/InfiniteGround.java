package green.liam.shape;

import green.liam.base.Transform;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class InfiniteGround extends Shape {
    static final float RADIUS = 1000;

    PImage texture;
    float scale;
    Quad quad;

    public InfiniteGround(PImage texture, float scale) {
        super();
        this.texture = texture;
        this.scale = scale;
        this.createQuad();
        this.quad.setTexture(this.texture);
    }

    public InfiniteGround(Transform parent, PImage texture, float scale) {
        super(parent);
        this.texture = texture;
        this.scale = scale;
        this.createQuad();
        this.quad.setTexture(this.texture);
    }

    private void createQuad() {
        this.vertices = new Vertex[4];
        this.edges = new Edge[4];
        this.vertices[0] = new Vertex(this.transform, new PVector(-RADIUS, -RADIUS), 0);
        this.vertices[1] = new Vertex(this.transform, new PVector(RADIUS, -RADIUS), 0);
        this.vertices[2] = new Vertex(this.transform, new PVector(RADIUS, RADIUS), 0);
        this.vertices[3] = new Vertex(this.transform, new PVector(-RADIUS, RADIUS), 0);
        this.edges[0] = new Edge(this.vertices[0], this.vertices[1]);
        this.edges[1] = new Edge(this.vertices[1], this.vertices[2]);
        this.edges[2] = new Edge(this.vertices[2], this.vertices[3]);
        this.edges[3] = new Edge(this.vertices[3], this.vertices[0]);
        this.quad =
                new Quad(this.vertices[0], this.vertices[1], this.vertices[2], this.vertices[3]);
    }

    public void setTexture(PImage texture) {
        this.texture = texture;
        this.quad.setTexture(this.texture);
    }

    @Override
    public void render(PApplet game) {
        this.quad.render(game);
    }

    @Override
    public float getDepth() {
        return Float.MIN_VALUE;
    }
}
