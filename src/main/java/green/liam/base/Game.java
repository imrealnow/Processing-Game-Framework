package green.liam.base;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import green.liam.input.Directional2DBinding;
import green.liam.input.InputManager;
import green.liam.rendering.Camera;
import green.liam.rendering.CompositeRenderable;
import green.liam.rendering.Renderable;
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

    private final PApplet parent;
    private boolean hasParent = false;

    /**
     * Creates a Game object with a parent PApplet.
     */
    public Game(PApplet parent) {
        super();
        if (instance != null) {
            throw new RuntimeException("Game already instantiated");
        }
        instance = this;
        this.parent = parent;
        this.hasParent = parent != null;
        this.camera = new Camera(new Transform(null), new Isometric3DProjector());
    }

    /**
     * Creates a Game object with no parent PApplet.
     */
    public Game() {
        this(null);
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
        return new PVector(this.width, this.height);
    }

    @Override
    public void settings() {
        this.size((int) this.screenDimensions.x, (int) this.screenDimensions.y, P2D);
        Time.INSTANCE.loop("update", Game.updateRate, this::update);
    }

    @Override
    public void setup() {
        this.frameRate(Game.frameRate);
        this.textureMode(NORMAL);
        ((PGraphicsOpenGL) this.g).textureSampling(2);
    }

    protected void createInputBindings() {
        Directional2DBinding moveBinding = new Directional2DBinding('w', 's', 'a', 'd');
        InputManager.INSTANCE.addInputBinding("move", moveBinding);
    }

    public Camera getCamera() {
        return this.camera;
    }

    protected void addGameObject(GameObject gameObject) {
        this.gameObjects.add(gameObject);
        gameObject.start();
    }

    protected void removeGameObject(GameObject gameObject) {
        this.gameObjects.remove(gameObject);
        gameObject.onDestroy();
    }

    @Override
    protected void handleKeyEvent(KeyEvent event) {
        // reroutes key events to InputManager
        InputManager.INSTANCE.handleKeyEvent(event);
    }

    @Override
    protected void handleMouseEvent(MouseEvent event) {
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
        this.background(0);
        PriorityQueue<Renderable> renderQueue = this.createRenderQueue();
        while (!renderQueue.isEmpty()) {
            Renderable renderable = renderQueue.poll();
            renderable.render(this);
        }
    }
}
