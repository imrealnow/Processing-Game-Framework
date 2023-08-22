package green.liam.base;

public abstract class Component {

  protected GameObject gameObject;

  public Component(GameObject gameObject) {
    this.gameObject = gameObject;
  }

  public GameObject gameObject() {
    return this.gameObject;
  }

  public void update() {}

  public void start() {}

  public void onDestroy() {
    this.gameObject = null;
  }
}
