package green.liam.physics;

import green.liam.base.Component;
import green.liam.base.GameObject;
import processing.core.PVector;

public class Rigidbody extends Component {

  PVector velocity = new PVector(0, 0, 0);

  public Rigidbody(GameObject gameObject) {
    super(gameObject);
  }
}
