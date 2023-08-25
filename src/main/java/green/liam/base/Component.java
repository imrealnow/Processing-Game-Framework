package green.liam.base;

public abstract class Component {

  protected GameObject gameObject;
  protected Transform transform;

  public Component(GameObject gameObject) {
    this.setGameObject(gameObject);
  }

  public void setGameObject(GameObject gameObject) {
    this.gameObject = gameObject;
    if (gameObject != null) this.transform = gameObject.transform();
  }

  public GameObject gameObject() {
    return this.gameObject;
  }

  public Transform transform() {
    return this.transform;
  }

  public void update() {}

  public void start() {}

  public void onDestroy() {
    this.gameObject = null;
    this.transform = null;
  }
}
