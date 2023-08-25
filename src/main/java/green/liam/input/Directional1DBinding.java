package green.liam.input;

public class Directional1DBinding implements InputBinding<Float> {

  private char leftKey, rightKey;
  private InputManager inputManager;

  public Directional1DBinding(char leftKey, char rightKey) {
    this.leftKey = leftKey;
    this.rightKey = rightKey;
    this.inputManager = InputManager.INSTANCE;
  }

  @Override
  public Float getValue() {
    float x = 0;
    if (this.inputManager.isKeyDown(this.leftKey)) x--;
    if (this.inputManager.isKeyDown(this.rightKey)) x++;
    return x;
  }
}
