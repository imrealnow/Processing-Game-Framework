package green.liam.base;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import green.liam.input.InputManager;
import green.liam.rendering.Camera;
import green.liam.rendering.CompositeRenderable;
import green.liam.rendering.Renderable;
import green.liam.rendering.camera.CameraProjector;
import green.liam.rendering.camera.Isometric3DProjector;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;

public class Game extends PApplet {
    private static Game instance = null;
    private final Camera camera;
    private List<GameObject> gameObjects = new ArrayList<>();
    private PVector screenDimensions = new PVector(800, 600);

    private static final int frameRate = 60;
    private static final int updateRate = 60;

    private PApplet parent;

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
        this.camera = new Camera(new Transform(), cameraProjector);
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
        PApplet.runSketch(new String[] {"green.liam.base.Game"}, instance);
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

    @Override
    public void settings() {
        Time.INSTANCE.loop("update", Game.updateRate, this::update);
    }

    @Override
    public void setup() {
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
        listCopy.forEach((gameObject) -> gameObject.update());
    }

    private PriorityQueue<Renderable> createRenderQueue() {
        PriorityQueue<Renderable> renderQueue = new PriorityQueue<Renderable>();
        this.gameObjects.forEach((gameObject) -> {
            if (gameObject instanceof CompositeRenderable) {
                CompositeRenderable composite = (CompositeRenderable) gameObject;
                renderQueue.addAll(composite.getRenderables());
            } else if (gameObject instanceof Renderable) {
                renderQueue.add((Renderable) gameObject);
            }
        });
        return renderQueue;
    }

    @Override
    public void draw() {
        this.parent.background(0);
        PriorityQueue<Renderable> renderQueue = this.createRenderQueue();
        while (!renderQueue.isEmpty()) {
            Renderable renderable = renderQueue.poll();
            renderable.render(this.parent);
        }
    }
}
