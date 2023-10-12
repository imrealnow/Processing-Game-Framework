package green.liam.shape;

import processing.core.PApplet;
import processing.core.PVector;

public class Edge {

  private Vertex start;
  private Vertex end;

  public Edge(Vertex start, Vertex end) {
    this.start = start;
    this.end = end;
  }

  public void destroy() {
    if (this.start != null) {
      this.start.destroy();
    }
    if (this.end != null) {
      this.end.destroy();
    }
    this.start = null;
    this.end = null;
  }

  public Vertex start() {
    return this.start;
  }

  public Vertex end() {
    return this.end;
  }

  public float length() {
    return this.start.position().dist(this.end.position());
  }

  public PVector translatedCenter() {
    return this.start.translatedPosition()
        .copy()
        .add(this.end.translatedPosition())
        .mult(0.5f);
  }

  public PVector toVector() {
    return this.end.pseudo3DPosition()
        .copy()
        .sub(this.start.pseudo3DPosition());
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

  public PVector midpoint() {
    PVector start = this.start.translatedPosition();
    PVector end = this.end.translatedPosition();
    return new PVector((start.x + end.x) / 2, (start.y + end.y) / 2);
  }

  public void drawNormal(PApplet game) {
    PVector normal = this.normal();
    PVector midpoint = this.midpoint();
    PVector normalEnd = midpoint.copy().add(normal.copy().mult(10));
    game.line(midpoint.x, midpoint.y, normalEnd.x, normalEnd.y);
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
    if (intersection.x < Math.min(startPos.x, endPos.x) ||
        intersection.x > Math.max(startPos.x, endPos.x) ||
        intersection.y < Math.min(startPos.y, endPos.y) ||
        intersection.y > Math.max(startPos.y, endPos.y) ||
        intersection.x < Math.min(lineStart.x, lineEnd.x) ||
        intersection.x > Math.max(lineStart.x, lineEnd.x) ||
        intersection.y < Math.min(lineStart.y, lineEnd.y) ||
        intersection.y > Math.max(lineStart.y, lineEnd.y)) {
      return null;
    }

    return intersection;
  }

  public boolean edgeWithinCircle(PVector center, float radius) {
    PVector start = this.start.worldPosition();
    PVector end = this.end.worldPosition();
    PVector d = PVector.sub(end, start);
    PVector f = PVector.sub(start, center);

    float a = d.dot(d);
    float b = 2 * f.dot(d);
    float c = f.dot(f) - radius * radius;

    float discriminant = b * b - 4 * a * c;
    if (discriminant < 0) {
      // No intersection
      return false;
    } else {
      // Check if intersection points are within the edge
      discriminant = PApplet.sqrt(discriminant);

      float t1 = (-b - discriminant) / (2 * a);
      float t2 = (-b + discriminant) / (2 * a);

      if ((t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1)) {
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * Returns the point on this edge that has the smallest perpidicular distance to
   * the given point
   * 
   * @param point
   * @return
   */
  public PVector closestPointTo(PVector point) {
    PVector edgeVector = this.end.worldPosition().copy().sub(this.start.worldPosition());
    PVector pointVector = point.copy().sub(this.start.worldPosition());
    float t = pointVector.dot(edgeVector) / edgeVector.dot(edgeVector);
    if (t < 0) {
      return this.start.worldPosition();
    } else if (t > 1) {
      return this.end.worldPosition();
    } else {
      return this.start.worldPosition().copy().add(edgeVector.mult(t));
    }
  }
}
