package green.liam.input;

import processing.core.PVector;

public class Directional2DBinding implements InputBinding<PVector> {
    private char upKey, downKey, leftKey, rightKey;
    private InputManager inputManager;

    public Directional2DBinding(char upKey, char downKey, char leftKey, char rightKey) {
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.inputManager = InputManager.INSTANCE;
    }

    @Override
    public PVector getValue() {
        float x = 0, y = 0;

        if (this.inputManager.isKeyDown(this.upKey))
            y--;
        if (this.inputManager.isKeyDown(this.downKey))
            y++;
        if (this.inputManager.isKeyDown(this.leftKey))
            x--;
        if (this.inputManager.isKeyDown(this.rightKey))
            x++;

        PVector direction = new PVector(x, y);
        if (direction.mag() > 0) {
            direction.normalize();
        }
        return direction;
    }

}
