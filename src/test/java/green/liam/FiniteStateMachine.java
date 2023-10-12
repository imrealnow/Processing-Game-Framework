package green.liam;

import java.util.HashMap;
import java.util.Map;

import green.liam.base.Component;
import green.liam.base.GameObject;

public class FiniteStateMachine extends Component {
    Map<String, State> states;
    State currentState;
    GameObject parentObject;

    public FiniteStateMachine(GameObject parentObject) {
        super(parentObject);
        this.states = new HashMap<String, State>();
        this.parentObject = parentObject;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.states.values().forEach(State::destroy);
        this.states.clear();
        this.states = null;
        this.currentState = null;
        this.parentObject = null;
    }

    public State addState(String name) {
        State newState = new State(name);
        this.states.put(name, newState);
        return newState;
    }

    public void setState(State state) {
        if (this.currentState != null) {
            this.currentState.end(this.parentObject);
        }
        this.currentState = state;
        state.start(this.parentObject);
    }

    public void setState(String name) {
        this.setState(this.states.get(name));
    }

    public State getCurrentState() {
        return this.currentState;
    }

    public State getState(String name) {
        return this.states.get(name);
    }

    @Override
    public void update() {
        State nextState = this.currentState.updateState(this.parentObject);
        if (!nextState.equals(this.currentState)) {
            this.setState(nextState);
        }
    }
}
