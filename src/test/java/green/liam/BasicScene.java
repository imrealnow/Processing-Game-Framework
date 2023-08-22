package green.liam;

import java.util.concurrent.CompletableFuture;
import green.liam.base.Component;
import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.base.Time;
import green.liam.base.Transform;
import green.liam.input.Directional2DBinding;
import green.liam.input.DiscreteKeyBinding;
import green.liam.input.InputBinding;
import green.liam.input.InputManager;
import green.liam.rendering.Camera;
import green.liam.rendering.camera.Isometric3DProjector;
import green.liam.shape.Box;
import green.liam.shape.InfiniteGround;
import green.liam.shape.ShapeFactory;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class BasicScene extends PApplet {
    Game game = new Game((PApplet) this, new Isometric3DProjector());
    InfiniteGround ground;
    Player playerBox;

    public static void main(String[] args) {
        PApplet sketch = new BasicScene();
        PApplet.runSketch(new String[] {"green.liam.BasicScene"}, sketch);
    }

    @Override
    public void settings() {
        this.size(800, 800, P2D);
    }

    @Override
    public void setup() {
        this.textureMode(NORMAL);
        this.textureWrap(REPEAT);
        // setup game
        this.game.initialise();
        this.createMoveInputBinding();
        // create player
        this.playerBox = new Player(0, 0, 10f, 10f, 60f);
        this.game.addGameObject(this.playerBox);
        this.playerBox.setTag("player");
        // create ground
        this.ground = new InfiniteGround(null, this.loadImage("grass.png"), 32f);
        this.ground.setFollowTarget(this.playerBox.transform());
        this.game.addGameObject(this.ground);
        // create boxes
        for (int i = 0; i < 20; i++) {
            PVector position = new PVector(this.random(-500, 500), this.random(-500, 500));
            Box box = (Box) ShapeFactory.create(Box.class, position, 50f, 50f, 50f);
            this.game.addGameObject(box);
        }
        // setup camera
        Camera camera = this.game.getCamera();
        camera.addComponent(new CameraController(camera, this.playerBox.transform()));
        this.game.getCamera().transform().setRotation(-45f);
    }

    void createMoveInputBinding() {
        Directional2DBinding moveBinding = new Directional2DBinding('w', 's', 'a', 'd');
        DiscreteKeyBinding jumpBinding = new DiscreteKeyBinding(' ');
        jumpBinding.addCallback(() -> {
            this.playerBox.jump();
        });
        InputManager.INSTANCE.addInputBinding("jump", jumpBinding);
        InputManager.INSTANCE.addInputBinding("move", moveBinding);
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
    }

}


class Player extends Box {
    float speed = 5f;
    float yVelocity = 0f;
    Directional2DBinding moveBinding;

    Player(int x, int y, float width, float length, float height) {
        super(width, length, height);
        this.transform.setPosition(new PVector(x, y));
        CompletableFuture<InputBinding<?>> moveBindingFuture =
                InputManager.INSTANCE.getInputBinding("move");
        moveBindingFuture.thenAccept(binding -> {
            this.moveBinding = (Directional2DBinding) binding;
        }).exceptionally((e) -> {
            System.out.println("Failed to get move binding: " + e.getMessage() + "");
            return null;
        });
    }

    void move(PVector direction) {
        PVector movement = direction.copy();
        PVector currentPosition = this.transform.position();
        movement.mult(this.speed);
        this.transform.setPosition(PVector.add(currentPosition, movement));
    }

    @Override
    public void update() {
        if (this.moveBinding != null) {
            Camera camera = Game.getInstance().getCamera();
            float cameraRotation = camera.transform().rotationInRadians();
            PVector direction = this.moveBinding.getValue().rotate(-cameraRotation);
            this.transform.setRotation(-camera.transform().rotation());
            // apply y velocity
            float height = this.transform.height();
            float newHeight = PApplet.max(height + this.yVelocity * Time.INSTANCE.deltaTime(), 0);
            if (newHeight <= 0) {
                this.yVelocity = 0;
            }
            this.transform.setHeight(newHeight);
            // apply gravity if not on the ground
            if (height != 0)
                this.yVelocity -= 30f;
            // move if there is movement input
            if (direction.mag() > 0) {
                this.move(direction);
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
            throw new IllegalArgumentException("CameraController must be attached to a Camera");
        }
        this.target = target;
    }

    @Override
    public void update() {
        this.angle += 10f * Time.INSTANCE.deltaTime();
        PVector targetPosition = this.target.position();
        PVector cameraPosition = this.camera.transform().position();
        PVector targetDirection =
                PVector.sub(targetPosition, cameraPosition).mult(Time.INSTANCE.deltaTime() * 4f);
        PVector newPosition = PVector.add(cameraPosition, targetDirection);
        this.camera.transform().setPosition(newPosition);
        this.camera.transform().setRotation(this.angle);
    }
}
