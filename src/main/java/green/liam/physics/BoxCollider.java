package green.liam.physics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import green.liam.base.GameObject;
import green.liam.shape.Edge;
import green.liam.shape.Quad;
import green.liam.shape.Vertex;
import green.liam.util.Pair;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * A class representing a box collider for handling 2D rectangular collisions.
 */
public class BoxCollider extends Collider {

  private final Quad box;

  public BoxCollider(GameObject gameObject, Quad box) {
    super(gameObject);
    this.box = box;
  }

  @Override
  public PVector center() {
    return this.box.centerPosition();
  }

  public void drawNormals(PApplet game) {
    for (Edge edge : this.box.edges()) {
      edge.drawNormal(game);
    }
  }

  public static Pair<Float, Float> projectVertices(List<PVector> vertices, PVector edgeNormal) {
    float minProjection = Float.MAX_VALUE;
    float maxProjection = Float.MIN_VALUE;
    for (PVector vertex : vertices) {
      float projection = PVector.dot(vertex, edgeNormal);
      if (projection < minProjection) {
        minProjection = projection;
      }
      if (projection > maxProjection) {
        maxProjection = projection;
      }
    }
    return new Pair<>(minProjection, maxProjection);
  }

  @Override
  public boolean isPointInside(PVector point) {
    return this.box.isPointInside(point);
  }

  @Override
  public PVector closestPointTo(PVector point) {
    return this.box.closestPointTo(point);
  }

  @Override
  public CollisionData collidesWith(Collider other) {
    if (other instanceof BoxCollider box) {
      return this.collidesWithBox(box);
    } else if (other instanceof SphereCollider sphere) {
      return this.collidesWithSphere(sphere);
    } else {
      throw new RuntimeException("Unsupported collider type");
    }
  }

  public static PVector getAveragedEdgeNormal(PVector point, Edge[] edges, float radius) {
    PVector normal = new PVector(0, 0, 0);
    int count = 0;
    for (Edge edge : edges) {
      if (edge.edgeWithinCircle(point, radius)) {
        normal.add(edge.normal());
        count++;
      }
    }
    if (count > 0) {
      normal.div(count);
      normal.normalize();
    }
    return normal;
  }

  public CollisionData collidesWithBox(BoxCollider other) {
    List<PVector> verticesA = Arrays.asList(this.box.vertices()).stream().map(Vertex::worldPosition)
        .toList();
    List<PVector> verticesB = Arrays.asList(other.box.vertices()).stream().map(Vertex::worldPosition)
        .toList();

    float minOverlap = Float.MAX_VALUE;
    PVector collisionNormal = null;
    PVector collisionPoint = null;

    for (int i = 0; i < verticesA.size(); i++) {
      PVector vertexA = verticesA.get(i);
      PVector vertexB = verticesA.get((i + 1) % verticesA.size());

      PVector edge = PVector.sub(vertexA, vertexB);
      PVector axis = new PVector(-edge.y, edge.x, 0);
      axis.normalize();

      Pair<Float, Float> projectionsA = projectVertices(verticesA, axis);
      Pair<Float, Float> projectionsB = projectVertices(verticesB, axis);
      float minA = projectionsA.first();
      float maxA = projectionsA.second();
      float minB = projectionsB.first();
      float maxB = projectionsB.second();

      if (minA > maxB || minB > maxA) {
        return null;
      }

      float axisDepth = Math.min(maxB - minA, maxA - minB);

      if (axisDepth < minOverlap) {
        minOverlap = axisDepth;
        collisionNormal = axis;
      }
    }

    for (int i = 0; i < verticesB.size(); i++) {
      PVector vertexA = verticesB.get(i);
      PVector vertexB = verticesB.get((i + 1) % verticesB.size());

      PVector edge = PVector.sub(vertexA, vertexB);
      PVector axis = new PVector(-edge.y, edge.x, 0);
      axis.normalize();

      Pair<Float, Float> projectionsA = projectVertices(verticesA, axis);
      Pair<Float, Float> projectionsB = projectVertices(verticesB, axis);
      float minA = projectionsA.first();
      float maxA = projectionsA.second();
      float minB = projectionsB.first();
      float maxB = projectionsB.second();

      if (minA >= maxB || minB >= maxA) {
        return null;
      }

      float axisDepth = Math.min(maxB - minA, maxA - minB);

      if (axisDepth < minOverlap) {
        minOverlap = axisDepth;
        collisionNormal = axis;
      }
    }

    collisionPoint = PVector.mult(collisionNormal, minOverlap).add(this.center());

    PVector thisCenter = this.center();
    PVector otherCenter = other.center();
    PVector directionToOther = PVector.sub(otherCenter, thisCenter);
    if (directionToOther.dot(collisionNormal) < 0) {
      collisionNormal.mult(-1);
    }

    PVector relativeVelocity = this.getRelativeVelocity(other);

    return new CollisionData(other, collisionNormal, collisionPoint, minOverlap, relativeVelocity);
  }

  public CollisionData collidesWithSphere(SphereCollider other) {
    PVector closestPoint = this.closestPointTo(other.center());
    if (other.isPointInside(closestPoint)) {
      PVector thisClosestPoint = other.closestPointTo(this.center());
      PVector normal = PVector.sub(closestPoint, this.center());
      normal.normalize();
      float distance = thisClosestPoint.dist(closestPoint);
      return new CollisionData(other, normal, closestPoint, distance,
          this.getRelativeVelocity(other));
    } else {
      return null;
    }
  }
}
