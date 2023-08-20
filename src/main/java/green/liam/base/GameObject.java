package green.liam.base;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    protected Transform transform;
    protected List<Component> components = new ArrayList<>();

    public GameObject() {
        this.transform = new Transform(this);
    }

    public GameObject(Transform parent) {
        this.transform = new Transform(this, parent);
    }

    public Transform transform() {
        return this.transform;
    }

    public <T extends Component> T getComponent(Class<T> type) {
        for (Component component : this.components) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        return null;
    }

    public <T extends Component> T addComponent(Class<T> type) {
        try {
            T component = type.getConstructor(GameObject.class).newInstance(this);
            this.components.add(component);
            return component;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasComponent(Class<? extends Component> type) {
        for (Component component : this.components) {
            if (type.isInstance(component)) {
                return true;
            }
        }
        return false;
    }

    public void removeComponent(Component component) {
        this.components.remove(component);
    }

    public void start() {
        for (Component component : this.components) {
            component.start();
        }
    }

    public void update() {
        for (Component component : this.components) {
            component.update();
        }
    }

    public void onDestroy() {
        for (Component component : this.components) {
            component.onDestroy();
        }
    }
}
