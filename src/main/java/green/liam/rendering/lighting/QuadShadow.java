package green.liam.rendering.lighting;

import green.liam.base.Transform;
import green.liam.shape.Quad;
import green.liam.shape.ShadowQuad;
import green.liam.shape.Vertex;
import processing.core.PApplet;
import processing.core.PVector;

public class QuadShadow {

  public static Quad cast(
    Vertex[] vertices,
    Transform transform,
    GlobalLight light
  ) {
    PVector lightDirection = light
      .getDirectionVector(PApplet.radians(15))
      .normalize();
    Vertex[] projectedVertices = new Vertex[vertices.length];

    for (int i = 0; i < vertices.length; i++) {
      Vertex vertex = vertices[i];
      PVector currentPosition = vertex.pseudo3DPosition();

      // If the vertex is on the ground, its projected position is the same
      if (currentPosition.y == 0) {
        projectedVertices[i] =
          new Vertex(vertex.transform(), vertex.localPosition(), 0);
      } else {
        // Calculate the projected position
        PVector projectedPosition = PVector.sub(
          vertex.localPosition(),
          PVector.mult(lightDirection, vertex.height() * lightDirection.y)
        );

        projectedVertices[i] =
          new Vertex(vertex.transform(), projectedPosition, 0);
      }
    }

    // Construct and return the new Quad
    return new ShadowQuad(projectedVertices); // Assuming your Quad constructor can take an array of vertices
  }
}
