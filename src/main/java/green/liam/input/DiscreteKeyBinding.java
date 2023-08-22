package green.liam.input;

import green.liam.events.EventManager;
import green.liam.events.EventManagerFactory;
import green.liam.events.Observer;
import java.util.HashSet;
import java.util.Set;
import processing.event.KeyEvent;

public class DiscreteKeyBinding
  implements InputBinding<Boolean>, Observer<KeyEvent> {

  private char key;
  private InputManager inputManager;
  private Set<Runnable> callbacks = new HashSet<>();
  private EventManager<KeyEvent> keyEventManager;

  public DiscreteKeyBinding(char key) {
    this.key = key;
    this.inputManager = InputManager.INSTANCE;
    this.keyEventManager = EventManagerFactory.getEventManager(KeyEvent.class);
    this.keyEventManager.addObserver(this);
  }

  @Override
  public Boolean getValue() {
    return this.inputManager.isKeyDown(this.key);
  }

  public void addCallback(Runnable callback) {
    this.callbacks.add(callback);
  }

  public void removeCallback(Runnable callback) {
    this.callbacks.remove(callback);
  }

  @Override
  public void onNotify(KeyEvent event) {
    if (event.getKey() == this.key && event.getAction() == KeyEvent.PRESS) {
      for (Runnable callback : this.callbacks) {
        callback.run();
      }
    }
  }
}
