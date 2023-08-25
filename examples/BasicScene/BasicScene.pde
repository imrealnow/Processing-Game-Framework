import green.liam.base.*;
import green.liam.input.*;
import green.liam.events.*;
import green.liam.shape.*;
import green.liam.util.Helper;
import green.liam.rendering.*;
import green.liam.rendering.camera.*;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

CameraProjector isometric = new Isometric3DProjector();
CameraProjector regular2D = new Regular2DProjector();
boolean is3d = true;
Game game = new Game((PApplet) this, is3d ? isometric : regular2D);
InfiniteGround ground;
Player playerBox;

@Override
public void settings() {
    // set window size, and use OpenGL 2D renderer
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
        PVector position = new PVector(this.random( -500, 500), this.random( -500, 500));
        Box box = (Box) ShapeFactory.create(Box.class, position, 50f, 50f, 50f);
        this.game.addGameObject(box);
    }
    // setup camera
    Camera camera = this.game.getCamera();
    camera.addComponent(new CameraController(camera, this.playerBox.transform()));
    this.game.getCamera().transform().setRotation( -45f);
}

void createMoveInputBinding() {
    // create WASD to PVector input binding
    Directional2DBinding moveBinding = new Directional2DBinding('w', 's', 'a', 'd');
    InputManager.INSTANCE.addInputBinding("move", moveBinding);
    // create discrete jump input binding that runs a callback when the key is pressed
    DiscreteKeyBinding jumpBinding = new DiscreteKeyBinding(' ');
    jumpBinding.addCallback(() -> {
        this.playerBox.jump();
    });
    InputManager.INSTANCE.addInputBinding("jump", jumpBinding);
    DiscreteKeyBinding toggle3dBinding = new DiscreteKeyBinding('r');
    toggle3dBinding.addCallback(() -> {
        this.is3d = !this.is3d;
        this.game.getCamera().switchProjector(this.is3d ? isometric : regular2D);
    });
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
