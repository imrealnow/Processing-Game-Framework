package green.liam.base;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import green.liam.input.Directional2DBinding;
import green.liam.input.InputManager;
import green.liam.rendering.Camera;
import green.liam.rendering.CompositeRenderable;
import green.liam.rendering.Renderable;
import green.liam.shape.Box;
import green.liam.shape.InfiniteGround;
import green.liam.shape.ShapeFactory;
import green.liam.shape.Vertex;
import green.liam.util.Helper;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;

public class Game extends PApplet {
    private Transform rootTransform = new Transform(null);
    private List<GameObject> gameObjects = new ArrayList<>();
    private static Game instance = null;
    private PVector screenDimensions = new PVector(800, 600);

    private static final int frameRate = 60;
    private static final int updateRate = 60;

    private InfiniteGround ground;
    private Box playerBox = new Box(this.rootTransform, 10f, 10f, 50f);
    private Box box = new Box(this.rootTransform, 50f, 50f, 30f);
    private float boxRotation = 0f;

    public Game() {
        super();
        if (instance != null) {
            throw new RuntimeException("Game already instantiated");
        }
        instance = this;
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

    public PVector getScreenDimensions() {
        return new PVector(this.width, this.height);
    }

    @Override
    public void settings() {
        this.size((int) this.screenDimensions.x, (int) this.screenDimensions.y, P2D);
        this.createInputBindings();
        Time.INSTANCE.loop("update", Game.updateRate, this::update);
        this.ground = new InfiniteGround(this.rootTransform,
                Helper.loadImageFromResource("grass.png"), 50f);
        this.addGameObject(this.playerBox);
        // create boxes
        for (int i = 0; i < 20; i++) {
            PVector position = new PVector(this.random(-500, 500), this.random(-500, 500));
            Box box = (Box) ShapeFactory.create(Box.class, position, this.rootTransform, 50f, 50f,
                    50f);
            this.addGameObject(box);
        }
        this.box.transform().setPosition(new PVector(0, 200, 0));
        this.addGameObject(this.box);
    }

    @Override
    public void setup() {
        this.frameRate(Game.frameRate);
        this.textureMode(NORMAL);
        this.textureWrap(REPEAT);
        ((PGraphicsOpenGL) this.g).textureSampling(2);
    }

    protected void createInputBindings() {
        Directional2DBinding moveBinding = new Directional2DBinding('w', 's', 'a', 'd');
        InputManager.INSTANCE.addInputBinding("move", moveBinding);
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
        Camera.MAIN.update();
        List<GameObject> listCopy = new ArrayList<>(this.gameObjects);
        listCopy.forEach((gameObject) -> gameObject.update());
        PVector difference = PVector.sub(InputManager.INSTANCE.mousePosition(),
                new PVector(this.width / 2, this.height / 2));
        difference.x *= (float) Math.tan(Camera.MAIN.pitch());
        float angle = (float) -Math.toDegrees(Math.atan2(difference.y, difference.x));
        Camera.MAIN.setYaw(Helper.smoothMoveTowardsAngle(Camera.MAIN.yaw(), angle, 5f, 5f));
        // this.playerBox.transform().setRotation(angle * 2f);
        this.playerBox.transform().setPosition(Camera.MAIN.worldPosition());
        this.boxRotation = (this.boxRotation + 1f) % 360f;
        this.box.transform().setRotation(this.boxRotation);
        System.out.println(this.playerBox.getFaces()[0].getDepth());

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
        this.stroke(255);
        this.fill(140);
        PriorityQueue<Renderable> renderQueue = this.createRenderQueue();
        this.ground.render(this);
        while (!renderQueue.isEmpty()) {
            Renderable renderable = renderQueue.poll();
            renderable.render(this);
        }
        Vertex[] player = this.playerBox.vertices();
        PVector playerPosition = new PVector(0, 0);
        for (Vertex vertex : player) {
            playerPosition.add(vertex.translatedPosition());
        }
        playerPosition.div(player.length);
        PVector mousePosition = InputManager.INSTANCE.mousePosition();
        this.line(playerPosition.x, playerPosition.y, mousePosition.x, mousePosition.y);
    }
}
