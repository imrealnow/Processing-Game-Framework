package green.liam.shape;

import processing.core.PVector;

public class Edge {

  private Vertex start;
  private Vertex end;

  public Edge(Vertex start, Vertex end) {
    this.start = start;
    this.end = end;
  }

  public Vertex start() {
    return this.start;
  }

  public Vertex end() {
    return this.end;
  }

  public PVector normal() {
    PVector start = this.start.translatedPosition();
    PVector end = this.end.translatedPosition();
    return new PVector(end.y - start.y, start.x - end.x).normalize();
  }

  public void flipNormal() {
    // switch the start and end vertices
    Vertex temp = this.start;
    this.start = this.end;
    this.end = temp;
  }

  // Returns the intersection point of this edge with the given line segment
  public PVector findLineIntersection(PVector lineStart, PVector lineEnd) {
    PVector intersection = new PVector();
    PVector startPos = this.start.position();
    PVector endPos = this.end.position();
    float a1 = endPos.y - startPos.y;
    float b1 = startPos.x - endPos.x;
    float c1 = a1 * startPos.x + b1 * startPos.y;

    float a2 = lineEnd.y - lineStart.y;
    float b2 = lineStart.x - lineEnd.x;
    float c2 = a2 * lineStart.x + b2 * lineStart.y;

    float det = a1 * b2 - a2 * b1;

    if (det == 0) {
      // Lines are parallel
      return null;
    } else {
      intersection.x = (b2 * c1 - b1 * c2) / det;
      intersection.y = (a1 * c2 - a2 * c1) / det;
    }

    // Check that the intersection is within both line segments
    if (
      intersection.x < Math.min(startPos.x, endPos.x) ||
      intersection.x > Math.max(startPos.x, endPos.x) ||
      intersection.y < Math.min(startPos.y, endPos.y) ||
      intersection.y > Math.max(startPos.y, endPos.y) ||
      intersection.x < Math.min(lineStart.x, lineEnd.x) ||
      intersection.x > Math.max(lineStart.x, lineEnd.x) ||
      intersection.y < Math.min(lineStart.y, lineEnd.y) ||
      intersection.y > Math.max(lineStart.y, lineEnd.y)
    ) {
      return null;
    }

    return intersection;
  }
}
