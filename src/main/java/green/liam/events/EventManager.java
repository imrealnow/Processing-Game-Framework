package green.liam.events;

import java.util.HashSet;
import java.util.Set;

public class EventManager<T> {
    private Set<Observer<T>> observers = new HashSet<>();

    public void addObserver(Observer<T> observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Observer<T> observer) {
        this.observers.remove(observer);
    }

    public void notify(T event) {
        if (event == null)
            throw new IllegalArgumentException("Event cannot be null");
        if (this.observers.size() == 0)
            return;

        Set<Observer<T>> observersCopy = new HashSet<>(this.observers);
        for (Observer<T> observer : observersCopy) {
            observer.onNotify(event);
        }
    }
}
