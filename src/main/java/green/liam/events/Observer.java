package green.liam.events;

/**
 * The Observer interface describes the capability to listen for events of type T.
 */
public interface Observer<T> {
  /**
   * Called when an event of type T is fired.
   *
   * @param event The event that was fired.
   */
  void onNotify(T event);
}
