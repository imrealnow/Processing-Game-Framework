import green.liam.base.*;
import green.liam.input.*;
import green.liam.events.*;
import green.liam.shape.*;
import green.liam.util.Helper;
import green.liam.rendering.*;
import green.liam.rendering.camera.*;

Game game = new Game((PApplet)this, new Isometric3DProjector());
InfiniteGround ground;
Player playerBox;

void setup() {
    size(800, 600, P2D);
    // setup game
    game.settings();
    game.setup();
    game.getCamera().getTransform().setRotation(45f);
    createMoveInputBinding();
    // create ground
    this.ground = new InfiniteGround(null,
        loadImage("grass.png"), 50f);
    game.addGameObject(this.ground);
    // create player
    playerBox = new Player(0, 0, 50f, 50f, 50f);
    game.addGameObject(this.playerBox);
    // create boxes
    for (int i = 0; i < 20; i++) {
        PVector position = new PVector(this.random( -500, 500), this.random( -500, 500));
        Box box = (Box) ShapeFactory.create(Box.class, position, 50f, 50f,
            50f);
        game.addGameObject(box);
    }
}

void createMoveInputBinding() {
    Directional2DBinding moveBinding = new Directional2DBinding('w', 's', 'a', 'd');
    InputManager.INSTANCE.addInputBinding("move", moveBinding);
}

@Override
void handleKeyEvent(KeyEvent event) {
    game.handleKeyEvent(event);
}

@Override
void handleMouseEvent(MouseEvent event) {
    game.handleMouseEvent(event);
}

void draw() {
    fill(255);
    game.draw();
}
