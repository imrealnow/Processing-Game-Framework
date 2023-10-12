package green.liam;

import java.util.function.Predicate;

import green.liam.base.GameObject;

public class StateTransition {
    private State to;
    private Predicate<GameObject> predicate;

    public StateTransition(State to, Predicate<GameObject> predicate) {
        this.to = to;
        this.predicate = predicate;
    }

    public State getTo() {
        return this.to;
    }

    public Predicate<GameObject> getPredicate() {
        return this.predicate;
    }
}
