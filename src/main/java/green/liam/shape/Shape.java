package green.liam.shape;

import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.base.Transform;
import green.liam.rendering.Camera;
import green.liam.rendering.Renderable;
import processing.core.PApplet;
import processing.core.PVector;

public abstract class Shape extends GameObject implements Renderable {
    protected Edge[] edges;
    protected Vertex[] vertices;

    public Shape() {
        super();
    }

    public Shape(Transform parent) {
        super(parent);
    }

    public Edge[] edges() {
        return this.edges;
    }

    public Vertex[] vertices() {
        return this.vertices;
    }

    protected void setEdges(Edge[] edges) {
        this.edges = edges;
    }

    protected void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    @Override
    public Transform transform() {
        return this.transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    @Override
    public void render(PApplet game) {
        game.beginShape();

        Vertex[] quadVertices = this.vertices();
        for (int i = 0; i < quadVertices.length; i++) {
            PVector pos = quadVertices[i].translatedPosition();
            game.vertex(pos.x, pos.y);
        }
        game.endShape(Game.CLOSE);
    }

    @Override
    public float getDepth(Camera camera) {
        PVector pos = this.transform.position();
        return Transform.inverseTranslateVector(camera, pos).y;
    }
}
