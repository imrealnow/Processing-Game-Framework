package green.liam.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import green.liam.events.EventManager;
import green.liam.events.EventManagerFactory;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public enum InputManager {
    INSTANCE;

    private EventManager<KeyEvent> keyEventManager;
    private EventManager<MouseEvent> mouseEventManager;

    private Set<Character> keysDown = new HashSet<>();
    private Set<Integer> mouseButtonsDown = new HashSet<>();
    private PVector mousePosition = new PVector(0, 0);

    private Map<String, InputBinding<?>> inputBindings = new HashMap<>();
    private Map<String, CompletableFuture<InputBinding<?>>> futureBindings = new HashMap<>();

    private InputManager() {
        this.keyEventManager = EventManagerFactory.getEventManager(KeyEvent.class);
        this.mouseEventManager = EventManagerFactory.getEventManager(MouseEvent.class);
    }

    public void addInputBinding(String name, InputBinding<?> binding) {
        this.inputBindings.put(name, binding);
        // If a future is waiting for this binding, complete it.
        CompletableFuture<InputBinding<?>> future = this.futureBindings.get(name);
        if (future != null) {
            future.complete(binding);
            this.futureBindings.remove(name);
        }
    }

    public CompletableFuture<InputBinding<?>> getInputBinding(String name) {
        InputBinding<?> binding = this.inputBindings.get(name);
        if (binding != null) {
            // If the binding exists, return a completed future.
            return CompletableFuture.completedFuture(binding);
        } else {
            // If the binding doesn't exist yet, return a future that might be completed later.
            CompletableFuture<InputBinding<?>> future = new CompletableFuture<>();
            this.futureBindings.put(name, future);
            return future.orTimeout(1000, TimeUnit.MILLISECONDS);
        }
    }

    public void removeInputBinding(String name) {
        this.inputBindings.remove(name);
    }

    public void handleKeyEvent(KeyEvent event) {
        this.keyEventManager.notify(event);
        char key = event.getKey();
        if (event.getAction() == KeyEvent.PRESS) {
            this.keysDown.add(key);
        } else if (event.getAction() == KeyEvent.RELEASE && !event.isAutoRepeat()) {
            this.keysDown.remove(key);
        }
    }

    public void handleMouseEvent(MouseEvent event) {
        this.mouseEventManager.notify(event);
        int button = event.getButton();
        if (event.getAction() == MouseEvent.PRESS) {
            this.mouseButtonsDown.add(button);
        } else if (event.getAction() == MouseEvent.RELEASE) {
            this.mouseButtonsDown.remove(button);
        }
        this.mousePosition = new PVector(event.getX(), event.getY());
    }

    public boolean isKeyDown(char key) {
        return this.keysDown.contains(key);
    }

    public boolean isMouseButtonDown(int button) {
        return this.mouseButtonsDown.contains(button);
    }

    public PVector mousePosition() {
        return this.mousePosition;
    }
}
