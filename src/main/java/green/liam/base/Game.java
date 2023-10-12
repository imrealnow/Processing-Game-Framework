package green.liam.base;

import green.liam.input.InputManager;
import green.liam.physics.PhysicsManager;
import green.liam.physics.Rigidbody;
import green.liam.rendering.Camera;
import green.liam.rendering.CompositeRenderable;
import green.liam.rendering.Renderable;
import green.liam.rendering.camera.CameraProjector;
import green.liam.rendering.camera.Isometric3DProjector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.Texture;

public class Game {

  private static Game instance = null;
  private final Camera camera;
  private List<GameObject> gameObjects = new ArrayList<>();

  private static final int frameRate = 120;
  private long frameCount = 0;
  private PApplet parent;
  private Set<Renderable> renderQueueBuffer = new HashSet<>();

  /**
   * Creates a Game object with a parent PApplet.
   */
  public Game(PApplet parent, CameraProjector cameraProjector) {
    super();
    if (instance != null) {
      throw new RuntimeException("Game already instantiated");
    }
    instance = this;
    this.parent = parent;
    this.camera = new Camera(cameraProjector);
    this.gameObjects.add(this.camera);
  }

  /**
   * Creates a Game object with no parent PApplet.
   */
  public Game() {
    this(null, new Isometric3DProjector());
  }

  public static Game getInstance() {
    if (instance == null) {
      throw new RuntimeException("Game not instantiated");
    }
    return instance;
  }

  public PApplet getParent() {
    return this.parent;
  }

  public long getFrameCount() {
    return this.frameCount;
  }

  public void forceCacheRemoval(PImage img) {
    PGraphics pg = this.parent.g;
    Object cache = pg.getCache(img);

    if (cache instanceof Texture)
      ((Texture) cache).disposeSourceBuffer();

    pg.removeCache(img);
  }

  public PVector getScreenDimensions() {
    return new PVector(this.parent.width, this.parent.height);
  }

  public void initialise(boolean useNearestNeighbour) {
    this.parent.frameRate(Game.frameRate);
    this.parent.textureMode(PApplet.NORMAL);
    if (useNearestNeighbour)
      ((PGraphicsOpenGL) this.parent.g).textureSampling(2);
  }

  public Camera getCamera() {
    return this.camera;
  }

  public void addGameObject(GameObject gameObject) {
    this.gameObjects.add(gameObject);
    if (gameObject.hasComponent(Rigidbody.class))
      PhysicsManager.instance().addRigidbody(gameObject.getComponent(Rigidbody.class));
    gameObject.start();
  }

  public void removeGameObject(GameObject gameObject) {
    this.gameObjects.remove(gameObject);
    if (gameObject.hasComponent(Rigidbody.class))
      PhysicsManager.instance().removeRigidbody(gameObject.getComponent(Rigidbody.class));
    gameObject.onDestroy();
  }

  public void removeAllGameObjects() {
    List<GameObject> listCopy = new ArrayList<>(this.gameObjects);
    for (GameObject gameObject : listCopy) {
      if (gameObject == this.camera)
        continue;
      this.removeGameObject(gameObject);
    }
    this.gameObjects.clear();
    PhysicsManager.instance().clear();
    this.gameObjects.add(this.camera);
    System.gc();
  }

  public void handleKeyEvent(KeyEvent event) {
    // reroutes key events to InputManager
    InputManager.INSTANCE.handleKeyEvent(event);
  }

  public void handleMouseEvent(MouseEvent event) {
    // reroutes mouse events to InputManager
    InputManager.INSTANCE.handleMouseEvent(event);
  }

  protected void update() {
    List<GameObject> listCopy = new ArrayList<>(this.gameObjects);
    listCopy.forEach(GameObject::update);
    Time.INSTANCE.update();
    PhysicsManager.instance().update();
  }

  public void addToRenderQueue(Renderable renderable) {
    this.renderQueueBuffer.add(renderable);
  }

  private void unpackRenderables(
      Renderable renderable,
      PriorityQueue<Renderable> queue,
      Set<Renderable> visited) {
    if (visited.contains(renderable)) {
      queue.add(renderable);
      return; // If we've already visited this renderable, skip it
    }

    visited.add(renderable); // Mark this renderable as visited

    // If the renderable is a composite, unpack its renderables
    if (renderable instanceof CompositeRenderable) {
      CompositeRenderable composite = (CompositeRenderable) renderable;
      for (Renderable r : composite.getRenderables()) {
        this.unpackRenderables(r, queue, visited);
      }
    } else {
      // Otherwise, add the renderable to the queue
      queue.add(renderable);
      queue.addAll(renderable.getChildren());
    }
  }

  private PriorityQueue<Renderable> createRenderQueue() {
    PriorityQueue<Renderable> renderQueue = new PriorityQueue<Renderable>();
    Set<Renderable> visited = new HashSet<Renderable>();
    renderQueue.addAll(this.renderQueueBuffer);
    this.renderQueueBuffer.clear();
    List<GameObject> listCopy = new ArrayList<>(this.gameObjects);
    for (GameObject gameObject : listCopy) {
      if (gameObject instanceof Renderable) {
        this.unpackRenderables((Renderable) gameObject, renderQueue, visited);
      }
    }
    return renderQueue;
  }

  public void draw() {
    this.frameCount++;
    this.update();
    this.parent.background(0);
    this.parent.fill(120);
    this.parent.stroke(200);
    PriorityQueue<Renderable> renderQueue = this.createRenderQueue();
    while (!renderQueue.isEmpty()) {
      Renderable renderable = renderQueue.poll();
      renderable.render(this.parent);
    }
  }
}
