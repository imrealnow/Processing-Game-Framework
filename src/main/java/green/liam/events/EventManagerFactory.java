package green.liam.events;

import java.util.HashMap;
import java.util.Map;

public class EventManagerFactory {

  private static final Map<Class<?>, EventManager<?>> eventManagers = new HashMap<>();

  /**
   * Retrieves the {@code EventManager} instance associated with the specified event type. If no
   * manager exists for the given event type, a new one is created, cached, and returned.
   *
   * @param <T> the type of event
   * @param eventType the class of the event type to retrieve the manager for
   * @return the {@code EventManager} associated with the given event type
   *
   * @throws NullPointerException if {@code eventType} is null
   */
  public static synchronized <T> EventManager<T> getEventManager(
    Class<T> eventType
  ) {
    if (!eventManagers.containsKey(eventType)) {
      eventManagers.put(eventType, new EventManager<>());
    }
    // This cast is safe because we're only putting EventManager<T> into the map with Class<T>
    // as the key
    @SuppressWarnings("unchecked")
    EventManager<T> manager = (EventManager<T>) eventManagers.get(eventType);
    return manager;
  }
}
