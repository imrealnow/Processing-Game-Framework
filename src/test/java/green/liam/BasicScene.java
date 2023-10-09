package green.liam;

import green.liam.base.Component;
import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.base.Time;
import green.liam.base.Transform;
import green.liam.input.Directional2DBinding;
import green.liam.input.DiscreteKeyBinding;
import green.liam.input.InputBinding;
import green.liam.input.InputManager;
import green.liam.physics.BoxCollider;
import green.liam.physics.Collider;
import green.liam.physics.Rigidbody;
import green.liam.physics.SphereCollider;
import green.liam.rendering.Camera;
import green.liam.rendering.camera.CameraProjector;
import green.liam.rendering.camera.Isometric3DProjector;
import green.liam.rendering.camera.Regular2DProjector;
import green.liam.shape.Box;
import green.liam.shape.InfiniteGround;
import green.liam.shape.ShapeFactory;
import green.liam.shape.Sphere;
import green.liam.shape.Sprite;
import green.liam.util.Grid;
import green.liam.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PShader;

public class BasicScene extends PApplet {

  CameraProjector isometric = new Isometric3DProjector();
  CameraProjector regular2D = new Regular2DProjector();
  boolean is3d = true;
  Game game = new Game(
      (PApplet) this,
      this.is3d ? this.isometric : this.regular2D);
  InfiniteGround ground;
  Player playerBox;
  Box longBox;
  PShader shader;
  List<GameObject> boxes = new ArrayList<>();

  public static void main(String[] args) {
    PApplet sketch = new BasicScene();
    PApplet.runSketch(new String[] { "green.liam.BasicScene" }, sketch);
  }

  @Override
  public void settings() {
    this.size(800, 800, P2D);
    this.smooth(16);
  }

  @Override
  public void setup() {
    this.textureMode(NORMAL);
    this.textureWrap(REPEAT);
    // setup game
    this.game.initialise(true);
    this.createMoveInputBinding();
    // setup shader
    this.shader = this.loadShader("halftone.glsl");
    this.shader.set("pixelsPerRow", 1000);
    // create player
    this.playerBox = new Player(0, 0, 20f, 20f, 70f);
    this.game.addGameObject(this.playerBox);
    this.playerBox.setTag("player");
    Rigidbody playerRigidbody = this.playerBox.getComponent(Rigidbody.class);
    playerRigidbody.setMass(20f * 20f * 70f * 0.005f);
    // create ground
    this.ground = new InfiniteGround(null, this.loadImage("grass.png"), 32f);
    this.ground.setFollowTarget(this.playerBox.transform());
    this.game.addGameObject(this.ground);
    // create boxes
    PImage crateTexture = this.loadImage("crate.png");
    PImage cobbleTexture = this.loadImage("cobblestone.png");
    Grid grid = new Grid(100, 10, 10, (value, cellSize) -> {
      if (value == 1) {
        Box box = (Box) ShapeFactory.create(
            Box.class,
            new PVector(0, 0),
            (float) cellSize,
            (float) cellSize,
            (float) cellSize);
        box.addComponent(new BoxCollider(box, box.bottomFace()));
        box.addComponent(new Rigidbody(box).setMass(cellSize * cellSize * cellSize * 0.005f));
        box.setSideTextures(crateTexture);
        box.setTopTexture(crateTexture);
        box.setStrokeColour(new float[] { 82, 75, 36, 255 });
        return box;
      } else if (value == 2) {
        Box wallBox = (Box) ShapeFactory.create(
            Box.class,
            new PVector(0, 0),
            (float) cellSize,
            (float) cellSize,
            (float) cellSize);
        wallBox.addComponent(new BoxCollider(wallBox, wallBox.bottomFace()));
        wallBox.addComponent(new Rigidbody(wallBox).setType(Rigidbody.RigidbodyType.STATIC));
        wallBox.setSideTextures(cobbleTexture);
        wallBox.setTopTexture(cobbleTexture);
        wallBox.setStrokeColour(new float[] { 80, 80, 80, 255 });
        return wallBox;
      }
      return null;
    });
    grid.loadFromString(
        "0,0,0,0,0,0,0,0,0,0\n" +
            "0,0,0,0,0,0,0,0,0,0\n" +
            "0,0,1,1,0,0,1,1,0,0\n" +
            "0,0,1,1,0,0,1,1,0,0\n" +
            "0,0,0,0,0,0,0,0,0,0\n" +
            "1,1,0,0,0,0,0,0,1,1\n" +
            "0,1,1,0,0,0,0,1,1,0\n" +
            "0,0,1,1,1,1,1,1,0,0\n" +
            "0,0,0,0,0,0,0,0,0,0\n" +
            "0,0,0,0,0,0,0,0,0,0\n");
    this.boxes.addAll(grid.renderGrid());
    this.boxes.forEach(box -> this.game.addGameObject(box));
    this.longBox = (Box) ShapeFactory.create(
        Box.class,
        new PVector(0, 0),
        100f,
        100f,
        100f);
    this.longBox.addComponent(new BoxCollider(this.longBox, this.longBox.bottomFace()));
    this.longBox.addComponent(new Rigidbody(this.longBox).setType(Rigidbody.RigidbodyType.STATIC));
    this.longBox.setSideTextures(crateTexture);
    this.longBox.setTopTexture(crateTexture);
    this.longBox.setStrokeColour(new float[] { 82, 75, 36, 255 });
    this.game.addGameObject(this.longBox);
    // Create sphere
    Sphere sphere = (Sphere) ShapeFactory.create(
        Sphere.class,
        new PVector(0, 0),
        25f);
    sphere.transform().translate(new PVector(100, 100));
    sphere.addComponent(new SphereCollider(sphere, sphere));
    sphere.addComponent(new Rigidbody(sphere).setMass(25f * 25f * 25f * 0.005f).setDrag(5f));
    this.game.addGameObject(sphere);
    // setup camera
    Camera camera = this.game
        .getCamera();
    camera.addComponent(new CameraController(camera, this.playerBox.transform()));
    this.game.getCamera().transform().setRotation(-45f);
  }

  void createMoveInputBinding() {
    Directional2DBinding moveBinding = new Directional2DBinding(
        'w',
        's',
        'a',
        'd');
    DiscreteKeyBinding jumpBinding = new DiscreteKeyBinding(' ');
    jumpBinding.addCallback(() -> {
      this.playerBox.jump();
    });
    DiscreteKeyBinding toggle3dBinding = new DiscreteKeyBinding('r');
    toggle3dBinding.addCallback(() -> {
      this.is3d = !this.is3d;
      this.game.getCamera()
          .switchProjector(this.is3d ? this.isometric : this.regular2D);
    });
    DiscreteKeyBinding rotateCameraCounterClockwise = new DiscreteKeyBinding('q');
    rotateCameraCounterClockwise.addCallback(() -> {
      this.game.getCamera().transform().rotate(-22.5f);
    });
    DiscreteKeyBinding rotateCameraClockwise = new DiscreteKeyBinding('e');
    rotateCameraClockwise.addCallback(() -> {
      this.game.getCamera().transform().rotate(22.5f);
    });
    InputManager.INSTANCE.addInputBinding("jump", jumpBinding);
    InputManager.INSTANCE.addInputBinding("move", moveBinding);
    InputManager.INSTANCE.addInputBinding("toggle3d", toggle3dBinding);
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

class Player extends Box {

  float speed = 4f;
  float yVelocity = 0f;
  float rampUpDuration = 0.3f;
  float inputDuration = 0;
  float smoothFactor = 0.5f;
  Directional2DBinding moveBinding;
  Rigidbody rigidbody;
  BoxCollider collider;

  Player(int x, int y, float width, float length, float height) {
    super(width, length, height);
    this.transform.setPosition(new PVector(x, y));
    this.collider = new BoxCollider(this, this.bottomFace());
    this.addComponent(this.collider);
    this.rigidbody = new Rigidbody(this);
    this.addComponent(this.rigidbody);
    CompletableFuture<InputBinding<?>> moveBindingFuture = InputManager.INSTANCE.getInputBinding(
        "move");
    moveBindingFuture
        .thenAccept(binding -> {
          this.moveBinding = (Directional2DBinding) binding;
        })
        .exceptionally(e -> {
          System.out.println(
              "Failed to get move binding: " + e.getMessage() + "");
          return null;
        });
  }

  void move(PVector direction, float inputDuration) {
    PVector movement = direction.copy();
    PVector currentVelocity = this.rigidbody.velocity();
    float rampScale = PApplet.min(inputDuration / this.rampUpDuration, 1 - this.smoothFactor);
    PVector targetVelocity = PVector.lerp(currentVelocity, movement.mult(this.speed), rampScale);
    this.rigidbody.addVelocity(targetVelocity.sub(currentVelocity));
  }

  @Override
  public void update() {
    super.update();
    if (this.moveBinding != null) {
      float deltaTime = Time.INSTANCE.deltaTime();
      Camera camera = Game.getInstance().getCamera();
      float cameraRotation = camera.transform().rotationInRadians();
      PVector direction = this.moveBinding.getValue().rotate(-cameraRotation);
      // apply y velocity
      float height = this.transform.height();
      float newHeight = PApplet.max(
          height + this.yVelocity * deltaTime,
          0);
      if (newHeight <= 0) {
        this.yVelocity = 0;
      }
      this.transform.setHeight(newHeight);
      // apply gravity if not on the ground
      if (height != 0)
        this.yVelocity -= 30f;
      // move if there is movement input
      if (direction.mag() > 0) {
        this.inputDuration += deltaTime;
        this.move(direction, this.inputDuration);
      } else {
        this.inputDuration = 0;
      }
    }
  }

  public void jump() {
    if (this.transform.height() == 0)
      this.yVelocity = 500f;
    System.out.println("Jump!");
  }
}

class CameraController extends Component {

  Camera camera;
  Transform target;
  float angle = 0f;

  public CameraController(GameObject gameObject, Transform target) {
    super(gameObject);
    if (gameObject instanceof Camera) {
      this.camera = (Camera) gameObject;
    } else {
      throw new IllegalArgumentException(
          "CameraController must be attached to a Camera");
    }
    this.target = target;
  }

  @Override
  public void update() {
    this.angle += 10f * Time.INSTANCE.deltaTime();
    PVector targetPosition = this.target.position();
    PVector cameraPosition = this.camera.transform().position();
    PVector targetDirection = PVector
        .sub(targetPosition, cameraPosition)
        .mult(Time.INSTANCE.deltaTime() * 4f);
    PVector newPosition = PVector.add(cameraPosition, targetDirection);
    this.camera.transform().setPosition(newPosition);
  }
}
