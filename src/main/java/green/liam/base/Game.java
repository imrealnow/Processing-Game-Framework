package green.liam.base;

import green.liam.input.InputManager;
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
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;

public class Game extends PApplet {

  private static Game instance = null;
  private final Camera camera;
  private List<GameObject> gameObjects = new ArrayList<>();

  private static final int frameRate = 120;
  private static final int updateRate = 60;

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
    this.addGameObject(this.camera);
  }

  /**
   * Creates a Game object with no parent PApplet.
   */
  public Game() {
    this(null, new Isometric3DProjector());
    this.parent = this;
  }

  public static void main(String[] args) {
    instance = new Game();
    PApplet.runSketch(new String[] { "green.liam.base.Game" }, instance);
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

  public PVector getScreenDimensions() {
    return new PVector(this.parent.width, this.parent.height);
  }

  public void initialise() {
    Time.INSTANCE.loop("update", Game.updateRate, this::update);
    this.parent.frameRate(Game.frameRate);
    this.parent.textureMode(NORMAL);
    ((PGraphicsOpenGL) this.parent.g).textureSampling(2);
  }

  public Camera getCamera() {
    return this.camera;
  }

  public void addGameObject(GameObject gameObject) {
    this.gameObjects.add(gameObject);
    gameObject.start();
  }

  public void removeGameObject(GameObject gameObject) {
    this.gameObjects.remove(gameObject);
    gameObject.onDestroy();
  }

  @Override
  public void handleKeyEvent(KeyEvent event) {
    // reroutes key events to InputManager
    InputManager.INSTANCE.handleKeyEvent(event);
  }

  @Override
  public void handleMouseEvent(MouseEvent event) {
    // reroutes mouse events to InputManager
    InputManager.INSTANCE.handleMouseEvent(event);
  }

  protected void update() {
    List<GameObject> listCopy = new ArrayList<>(this.gameObjects);
    listCopy.forEach(GameObject::update);
    Time.INSTANCE.update();
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
    }
  }

  private PriorityQueue<Renderable> createRenderQueue() {
    PriorityQueue<Renderable> renderQueue = new PriorityQueue<Renderable>();
    Set<Renderable> visited = new HashSet<Renderable>();
    renderQueue.addAll(this.renderQueueBuffer);
    this.renderQueueBuffer.clear();
    for (GameObject gameObject : this.gameObjects) {
      if (gameObject instanceof Renderable) {
        this.unpackRenderables((Renderable) gameObject, renderQueue, visited);
      }
    }
    return renderQueue;
  }

  @Override
  public void draw() {
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
