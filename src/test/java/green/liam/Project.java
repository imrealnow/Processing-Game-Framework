package green.liam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.base.Time;
import green.liam.input.Directional2DBinding;
import green.liam.input.DiscreteKeyBinding;
import green.liam.input.InputManager;
import green.liam.physics.BoxCollider;
import green.liam.physics.Rigidbody;
import green.liam.physics.SphereCollider;
import green.liam.rendering.Camera;
import green.liam.rendering.camera.Isometric3DProjector;
import green.liam.shape.Box;
import green.liam.shape.InfiniteGround;
import green.liam.shape.ShapeFactory;
import green.liam.shape.Sphere;
import green.liam.util.Grid;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Project extends PApplet {
    private final BiFunction<Integer, Integer, GameObject> mappingFunction = (value, cellSize) -> {
        if (value == 2) {
            Box box = (Box) ShapeFactory.create(
                    Box.class,
                    new PVector(0, 0),
                    (float) cellSize,
                    (float) cellSize,
                    (float) cellSize);
            box.addComponent(new BoxCollider(box, box.bottomFace()));
            box.addComponent(new Rigidbody(box).setMass(200f));
            box.setSideTextures(this.crateTexture);
            box.setTopTexture(this.crateTexture);
            box.setStrokeColour(new float[] { 82, 75, 36, 255 });
            return box;
        } else if (value == 1) {
            Box wallBox = (Box) ShapeFactory.create(
                    Box.class,
                    new PVector(0, 0),
                    (float) cellSize,
                    (float) cellSize,
                    (float) cellSize);
            wallBox.addComponent(new BoxCollider(wallBox, wallBox.bottomFace()));
            wallBox.addComponent(new Rigidbody(wallBox).setType(Rigidbody.RigidbodyType.STATIC));
            wallBox.setStrokeColour(new float[] { 100, 100, 100, 255 });
            return wallBox;
        }
        return null;
    };

    Game game;
    Isometric3DProjector cameraProjector = new Isometric3DProjector();
    Camera camera;
    CameraController cameraController;
    Grid terrainGrid;
    InfiniteGround ground;
    Player player;
    PImage groundTexture;
    PImage crateTexture;
    PImage spriteSheet;

    public static void main(String[] args) {
        PApplet.main("green.liam.Project", args);
    }

    @Override
    public void settings() {
        this.size(800, 800, P2D);
    }

    @Override
    public void setup() {
        this.textureMode(NORMAL);
        this.textureWrap(REPEAT);
        this.game = new Game(this, this.cameraProjector);
        this.game.initialise(true);
        this.createInputBindings();
        this.camera = this.game.getCamera();
        this.crateTexture = this.loadImage("crate.png");
        this.groundTexture = this.loadImage("grass.png");
        this.spriteSheet = this.loadImage("animations/GirlSampleReadyIdle/GirlSample_ReadyIdle_Up.png");
        this.terrainGrid = new Grid(100, 20, 20, this.mappingFunction);
        this.cameraController = new CameraController(this.camera);
        this.camera.addComponent(this.cameraController);
        this.createLevelOne();
    }

    private void reset() {
        // clear any existing game objects
        this.game.removeAllGameObjects();
        this.player = new Player(0, 0, this.spriteSheet.get(0, 0, 256, 256), 256, 256, 32, 32);
        this.ground = new InfiniteGround(this.groundTexture, 32);
        this.ground.setFollowTarget(this.player.transform());
        this.game.addGameObject(this.ground);
        this.game.addGameObject(this.player);
        this.cameraController.setTarget(this.player.transform());
        this.camera.transform().setRotation(-45f);
    }

    private void createLevelOne() {
        this.reset();
        // procedural terrain
        this.generateMap();
        // Create sphere
        Sphere sphere = (Sphere) ShapeFactory.create(
                Sphere.class,
                new PVector(0, 0),
                25f);
        sphere.transform().translate(new PVector(100, 100));
        sphere.addComponent(new SphereCollider(sphere, sphere));
        sphere.addComponent(new Rigidbody(sphere).setMass(25f * 25f * 25f * 0.005f).setDrag(5f));
        this.game.addGameObject(sphere);
    }

    private void createLevelTwo() {
        this.reset();
        this.terrainGrid.clear();
        this.terrainGrid = new Grid(50, 10, 10, this.mappingFunction);
        this.terrainGrid.loadFromString(
                "2,2,2,2,2,2,2,2,2,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,0,0,0,0,0,0,0,0,2\n" +
                        "2,2,2,2,2,2,2,2,2,2\n");
        this.terrainGrid.renderGrid().forEach(go -> this.game.addGameObject(go));
        Sphere sphere = (Sphere) ShapeFactory.create(
                Sphere.class,
                new PVector(0, 0),
                25f);
        sphere.transform().translate(new PVector(100, 100));
        sphere.addComponent(new SphereCollider(sphere, sphere));
        sphere.addComponent(new Rigidbody(sphere).setMass(50).setDrag(3f));
        this.game.addGameObject(sphere);
    }

    private void generateMap() {
        this.terrainGrid.clear();
        this.terrainGrid = new Grid(100, 20, 20, this.mappingFunction);
        this.terrainGrid.randomFillMap(4);
        this.terrainGrid.renderGrid().forEach(gameObject -> this.game.addGameObject(gameObject));
    }

    private void createInputBindings() {
        Directional2DBinding movement = new Directional2DBinding('w', 's', 'a', 'd');
        DiscreteKeyBinding level1 = new DiscreteKeyBinding('r');
        level1.addCallback(() -> {
            this.createLevelOne();
        });
        DiscreteKeyBinding level2 = new DiscreteKeyBinding('t');
        level2.addCallback(() -> {
            this.createLevelTwo();
        });
        DiscreteKeyBinding rotateCameraCounterClockwise = new DiscreteKeyBinding('q');
        rotateCameraCounterClockwise.addCallback(() -> {
            this.game.getCamera().transform().rotate(-45f);
        });
        DiscreteKeyBinding rotateCameraClockwise = new DiscreteKeyBinding('e');
        rotateCameraClockwise.addCallback(() -> {
            this.game.getCamera().transform().rotate(45f);
        });
        InputManager.INSTANCE.addInputBinding("move", movement);
        InputManager.INSTANCE.addInputBinding("level1", level1);
        InputManager.INSTANCE.addInputBinding("level2", level2);
        InputManager.INSTANCE.addInputBinding("rotateCameraClockwise", rotateCameraClockwise);
        InputManager.INSTANCE.addInputBinding("rotateCameraCounterClockwise", rotateCameraCounterClockwise);
    }

    @Override
    public void handleKeyEvent(KeyEvent event) {
        this.game.handleKeyEvent(event);
    }

    @Override
    public void handleMouseEvent(MouseEvent event) {
        this.game.handleMouseEvent(event);
    }

    @Override
    public void draw() {
        this.game.draw();
        this.text("FPS: " + (1000f / Time.INSTANCE.averageDeltaTime()), 10, 10);
    }
}
