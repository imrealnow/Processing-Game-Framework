package green.liam.shape;

import green.liam.base.Game;
import green.liam.rendering.Camera;
import green.liam.rendering.Renderable;
import green.liam.util.Helper;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Quad implements Renderable {
    final Edge[] edges = new Edge[4];
    boolean isVertical = false;
    PImage texture;
    Edge leadingEdge;
    PVector normal = new PVector(0, -1, 0); // default normal is up
    protected PVector uvOffset = new PVector(0, 0);
    protected PVector uvScale = new PVector(32, 32);
    Vertex min;
    Vertex max;
    PVector dimensions;
    PVector[] vertexUVs = new PVector[] {new PVector(0, 0), new PVector(1, 0), new PVector(1, 1),
            new PVector(0, 1)};

    public Quad(Edge[] edges) {
        this.edges[0] = edges[0];
        this.edges[1] = edges[1];
        this.edges[2] = edges[2];
        this.edges[3] = edges[3];
        this.min = this.edges[0].end();
        this.max = this.edges[2].end();
    }

    public Quad(Edge e1, Edge e2, Edge e3, Edge e4) {
        this.edges[0] = e1;
        this.edges[1] = e2;
        this.edges[2] = e3;
        this.edges[3] = e4;
        this.min = this.edges[0].end();
        this.max = this.edges[2].end();
    }

    public Quad(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        this.edges[0] = new Edge(v1, v2);
        this.edges[1] = new Edge(v2, v3);
        this.edges[2] = new Edge(v3, v4);
        this.edges[3] = new Edge(v4, v1);
        this.min = v2;
        this.max = v4;
    }

    public Edge[] edges() {
        return this.edges;
    }

    public Vertex[] vertices() {
        Vertex[] vertices = new Vertex[4];
        vertices[0] = this.edges[0].start();
        vertices[1] = this.edges[1].start();
        vertices[2] = this.edges[2].start();
        vertices[3] = this.edges[3].start();

        return vertices;
    }

    public PVector normal() {
        return this.normal;
    }

    public Edge leadingEdge() {
        if (!this.isVertical) {
            throw new RuntimeException("Quad is not vertical");
        }
        return this.leadingEdge;
    }

    public Quad setIsVertical(Edge leadingEdge) {
        this.isVertical = true;
        this.leadingEdge = leadingEdge;
        return this;
    }

    public Quad setTexture(PImage texture) {
        this.texture = texture;
        return this;
    }

    public Quad setUVScale(PVector uvScale) {
        this.uvScale = uvScale;
        return this;
    }

    public Quad setUVOffset(PVector uvOffset) {
        this.uvOffset = uvOffset;
        return this;
    }

    @Override
    public void render(PApplet game) {
        if (!this.cameraCanSee())
            return;
        game.beginShape();
        game.texture(this.texture);
        Vertex[] quadVertices = this.vertices();
        for (int i = 0; i < quadVertices.length; i++) {
            PVector pos = quadVertices[i].translatedPosition();
            // Calculate UVs based on position of vertex relative to quad's dimensions
            pos = Helper.roundPVector(pos, 2);
            if (this.texture != null) {
                PVector uv = this.vertexUVs[i].copy();
                uv.x *= this.uvScale.x;
                uv.y *= this.uvScale.y;
                uv.sub(this.uvOffset);
                game.vertex(pos.x, pos.y, uv.x, uv.y);
            } else
                game.vertex(pos.x, pos.y);

        }

        game.endShape(Game.CLOSE);
    }

    @Override
    public float getDepth(Camera camera) {
        float averageHeight = 0;
        float averagePosition = 0;

        for (Vertex v : this.vertices()) {
            averageHeight -= v.height();
            averagePosition += v.translatedPosition().sub(new PVector(0, v.height())).y;
        }
        averageHeight /= this.vertices().length;
        averagePosition /= this.vertices().length;

        float alpha = 0.5f; // adjust this value as per your requirements
        float depth = alpha * averageHeight + (1 - alpha) * averagePosition;

        return depth;
    }


    private boolean cameraCanSee() {
        if (!this.isVertical)
            return true;
        PVector cameraDirection = new PVector(0, -1, 0);
        PVector quadDirection = this.leadingEdge.normal();
        float dot = PVector.dot(cameraDirection, quadDirection);
        return dot < 0;
    }
}
