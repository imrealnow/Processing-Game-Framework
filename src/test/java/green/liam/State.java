package green.liam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import green.liam.base.GameObject;
import green.liam.base.Time;

public class State {
    String name;
    List<StateTransition> transitions;
    State nextState;

    BiConsumer<State, GameObject> onEnter;
    BiConsumer<State, GameObject> onExit;

    public State(String name) {
        this.name = name;
        this.transitions = new ArrayList<StateTransition>();
    }

    public void destroy() {
        this.transitions.clear();
        this.transitions = null;
        this.nextState = null;
        this.onEnter = null;
        this.onExit = null;
    }

    public String getName() {
        return this.name;
    }

    public void start(GameObject parentObject) {
        if (this.onEnter != null) {
            this.onEnter.accept(this, parentObject);
        }
    }

    public void end(GameObject parentObject) {
        if (this.onExit != null) {
            this.onExit.accept(this, parentObject);
        }
    }

    public void setOnEnterCallback(BiConsumer<State, GameObject> onEnter) {
        this.onEnter = onEnter;
    }

    public void setOnExitCallback(BiConsumer<State, GameObject> onExit) {
        this.onExit = onExit;
    }

    public void addTransition(StateTransition transition) {
        this.transitions.add(transition);
    }

    public void setNextState(State to) {
        this.nextState = to;
    }

    public void changeAfterMs(State to, int ms) {
        // change to the next state after ms milliseconds
        State thisState = this;
        Time.INSTANCE.delayedInvoke(ms, () -> {
            thisState.setNextState(to);
        });
    }

    public final State updateState(GameObject parentObject) {
        if (this.nextState != null) {
            // tell the state machine to change state
            // and clear the nextState variable
            State temp = this.nextState;
            this.nextState = null;
            return temp;
        }
        for (StateTransition transition : this.transitions) {
            // check each transition to see if it should be taken
            if (transition.getPredicate().test(parentObject)) {
                return transition.getTo();
            }
        }
        // if no transition is taken, return this state
        return this.stateTick(parentObject);
    }

    protected State stateTick(GameObject parentObject) {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof State) {
            return ((State) obj).name.equals(this.name);
        }
        return false;
    }
}
