import green.liam.base.*;
import green.liam.input.*;
import green.liam.events.*;
import green.liam.shape.*;
import green.liam.util.Helper;
import green.liam.rendering.*;
import green.liam.rendering.camera.*;

Game game = new Game((PApplet)this, new Regular2DProjector());

void setup() {
    size(800,600,P2D);
    game.settings();
    game.setup();
    Rectangle rect = ShapeFactory.create(Rectangle.class, 50f, 50f);
    game.addGameObject(rect);
    rect.transform().setPosition(new PVector(200,200));
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
