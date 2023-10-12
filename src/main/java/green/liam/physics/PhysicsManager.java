package green.liam.physics;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import green.liam.physics.Rigidbody.RigidbodyType;
import green.liam.util.Pair;
import processing.core.PVector;

public class PhysicsManager {
    private final Set<Rigidbody> rigidbodies = new HashSet<>();
    private Set<Rigidbody>[][] spatialGrid;
    private Map<Rigidbody, CellIndex> rigidbodyCellIndices = new HashMap<>();
    private final int spatialGridSize;
    private final float cellSize;

    private CompletableFuture<Boolean> mapIsInitialised = new CompletableFuture<>();
    private static PhysicsManager instance;

    public static PhysicsManager instance() {
        if (instance == null)
            instance = new PhysicsManager(50, 250.0f); // example values, adjust as needed
        return instance;
    }

    private PhysicsManager(int spatialGridSize, float cellSize) {
        this.spatialGridSize = spatialGridSize;
        this.cellSize = cellSize;
        this.initialiseSpatialGrid();
    }

    public void clear() {
        this.rigidbodies.clear();
        this.rigidbodyCellIndices.clear();
        this.initialiseSpatialGrid();
    }

    @SuppressWarnings("unchecked")
    private void initialiseSpatialGrid() {
        this.spatialGrid = new HashSet[this.spatialGridSize][this.spatialGridSize];
        for (int i = 0; i < this.spatialGridSize; i++) {
            for (int j = 0; j < this.spatialGridSize; j++) {
                this.spatialGrid[i][j] = new HashSet<>();
            }
        }
        this.mapIsInitialised.complete(true);
    }

    public void addRigidbody(Rigidbody rigidbody) {
        this.rigidbodies.add(rigidbody);
        this.placeRigidbodyOntoGrid(rigidbody);
    }

    public void removeRigidbody(Rigidbody rigidbody) {
        if (!this.rigidbodies.contains(rigidbody))
            return;
        this.rigidbodies.remove(rigidbody);
        CellIndex index = this.rigidbodyCellIndices.remove(rigidbody);
        if (index != null) {
            this.spatialGrid[index.x][index.y].remove(rigidbody);
        }
    }

    public void resetSpatialGrid() {
        // Clear the spatial grid before rearranging rigidbodies
        for (int i = 0; i < this.spatialGridSize; i++) {
            for (int j = 0; j < this.spatialGridSize; j++) {
                this.spatialGrid[i][j].clear();
            }
        }
        for (Rigidbody rigidbody : this.rigidbodies) {
            this.placeRigidbodyOntoGrid(rigidbody);
        }
    }

    private CellIndex getCellIndex(PVector position) {
        int x = (int) (position.x / this.cellSize);
        int y = (int) (position.y / this.cellSize);
        // Ensure indices are within bounds
        x = Math.max(0, Math.min(x, this.spatialGridSize - 1));
        y = Math.max(0, Math.min(y, this.spatialGridSize - 1));
        return new CellIndex(x, y);
    }

    private void placeRigidbodyOntoGrid(Rigidbody rigidbody) {
        if (this.rigidbodyInCorrectCell(rigidbody))
            return;
        CellIndex cellIndex = this.getCellIndex(rigidbody.transform().position());
        this.spatialGrid[cellIndex.x][cellIndex.y].add(rigidbody);
        this.rigidbodyCellIndices.put(rigidbody, cellIndex);
    }

    private boolean rigidbodyInCorrectCell(Rigidbody rigidbody) {
        CellIndex cellIndex = this.getCellIndex(rigidbody.transform().position());
        return this.spatialGrid[cellIndex.x][cellIndex.y].contains(rigidbody);
    }

    private void fixRigidbodyPlacement(Rigidbody rigidbody) {
        if (!this.rigidbodies.contains(rigidbody))
            return;
        CellIndex cellIndex = this.rigidbodyCellIndices.get(rigidbody);
        this.spatialGrid[cellIndex.x][cellIndex.y].remove(rigidbody);
        this.placeRigidbodyOntoGrid(rigidbody);
    }

    private void updateGrid() {
        Set<Rigidbody> rigidbodiesCopy = new HashSet<>(this.rigidbodies);
        for (Rigidbody rigidbody : rigidbodiesCopy) {
            if (!this.rigidbodyInCorrectCell(rigidbody)) {
                this.fixRigidbodyPlacement(rigidbody);
            }
        }
    }

    private Set<Rigidbody> getCombinedSetAround(int x, int y) {
        Set<Rigidbody> combinedSet = new HashSet<>();
        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i >= this.spatialGridSize)
                continue;
            for (int j = y - 1; j <= y + 1; j++) {
                if (j < 0 || j >= this.spatialGridSize)
                    continue;
                combinedSet.addAll(this.spatialGrid[i][j]);
            }
        }
        return combinedSet;
    }

    public void checkCollisions() {
        // only check each pair of rigidbodies once
        try {
            Set<Pair<Rigidbody, Rigidbody>> checkedPairs = new HashSet<>();
            for (Rigidbody rigidbody : this.rigidbodies) {
                if (rigidbody.type() == RigidbodyType.STATIC)
                    continue;
                if (rigidbody.velocity().mag() == 0)
                    continue;
                CellIndex index = this.rigidbodyCellIndices.get(rigidbody);
                Set<Rigidbody> nearbyBodies = this.getCombinedSetAround(index.x, index.y);
                for (Rigidbody other : nearbyBodies) {
                    if (rigidbody == other || checkedPairs.contains(new Pair<>(rigidbody, other)))
                        continue;
                    checkedPairs.add(new Pair<>(rigidbody, other));
                    this.checkCollision(rigidbody, other);
                }
            }
        } catch (ConcurrentModificationException e) {
            // ignore, this is a known issue with the HashSet class
        }
    }

    private void checkCollision(Rigidbody rigidbody, Rigidbody other) {
        Collider collider = rigidbody.gameObject().getComponent(Collider.class);
        Collider otherCollider = other.gameObject().getComponent(Collider.class);
        if (collider == null || otherCollider == null)
            return;
        CollisionData collision = collider.collidesWith(otherCollider);
        if (collision != null) {
            CollisionData reversedCollision = CollisionData.reverse(collision, collider);
            rigidbody.moveOutOfCollision(collision);
            rigidbody.queueCollision(collision);
            other.queueCollision(reversedCollision);
            other.moveOutOfCollision(reversedCollision);
        }
    }

    public Set<CollisionData> getCollisions(Rigidbody rigidbody, List<CollisionData> pendingCollisions) {
        // check for collisions with other rigidbodies
        Collider collider = rigidbody.gameObject().getComponent(Collider.class);
        if (collider == null)
            return Set.of();
        Set<CollisionData> collisions = new HashSet<>();
        CellIndex index = this.rigidbodyCellIndices.get(rigidbody);
        Set<Rigidbody> nearbyBodies = this.getCombinedSetAround(index.x, index.y);
        // skip rigidbodies that are already in the pending collisions list
        for (CollisionData pendingCollision : pendingCollisions) {
            Rigidbody otherRigidbody = pendingCollision.getOtherCollider().gameObject().getComponent(Rigidbody.class);
            if (otherRigidbody == null)
                continue;
            if (nearbyBodies.contains(otherRigidbody)) {
                nearbyBodies.remove(otherRigidbody);
            }
        }
        for (Rigidbody other : nearbyBodies) {
            if (rigidbody == other)
                continue;
            Collider otherCollider = other.gameObject().getComponent(Collider.class);
            if (otherCollider == null)
                continue;
            CollisionData collision = collider.collidesWith(otherCollider);
            if (collision != null) {
                collisions.add(collision);
            }
        }
        return collisions;
    }

    public void update() {
        if (!this.mapIsInitialised.isDone())
            return;
        this.updateGrid();
        this.checkCollisions();
        for (Rigidbody rigidbody : this.rigidbodies) {
            rigidbody.handlePendingCollisions();
        }
    }

    private record CellIndex(int x, int y) {
    }
}