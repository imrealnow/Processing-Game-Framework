package green.liam;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.base.Time;
import green.liam.base.Transform;
import green.liam.input.Directional2DBinding;
import green.liam.input.InputBinding;
import green.liam.input.InputManager;
import green.liam.physics.BoxCollider;
import green.liam.physics.Rigidbody;
import green.liam.rendering.Camera;
import green.liam.rendering.Renderable;
import green.liam.rendering.lighting.GlobalLight;
import green.liam.rendering.lighting.ShadowCaster;
import green.liam.shape.Quad;
import green.liam.shape.ShadowEllipse;
import green.liam.shape.Sprite;
import green.liam.shape.Vertex;
import green.liam.util.CardinalDirection;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Player extends Sprite implements HasDirection {

    // parameters
    float speed = 6f;
    float yVelocity = 0f;
    float rampUpDuration = 0.3f;
    float inputDuration = 0;
    float smoothFactor = 0.9f;

    // references
    FiniteStateMachine animationController;
    Directional2DBinding moveBinding;
    Rigidbody rigidbody;
    BoxCollider collider;
    Quad collisionQuad;
    ShadowEllipse shadowEllipse;
    Vertex shadowVertex;

    // state
    CardinalDirection facing = CardinalDirection.North;

    Player(int x, int y, PImage defaultSprite, float width, float length, float collisionWidth,
            float collisionLength) {
        super(defaultSprite, width, length);
        this.setOffset(new PVector(0, -28f));
        this.setContentHeight(80f);
        this.transform.setPosition(new PVector(x, y));
        this.collisionQuad = new Quad(this.transform(), collisionWidth, collisionLength);
        this.shadowVertex = new Vertex(this.transform(), new PVector(0, 0), 0f);
        this.collider = new BoxCollider(this, this.collisionQuad);
        this.addComponent(this.collider);
        this.rigidbody = new Rigidbody(this);
        this.rigidbody.setMass(200f);
        this.addComponent(this.rigidbody);
        this.animationController = new FiniteStateMachine(this);
        this.loadAnimations();
        this.addComponent(this.animationController);
        this.animationController.update();
        CompletableFuture<InputBinding<?>> moveBindingFuture = InputManager.INSTANCE.getInputBinding(
                "move");
        moveBindingFuture
                .thenAccept(binding -> {
                    this.moveBinding = (Directional2DBinding) binding;
                })
                .exceptionally(e -> {
                    System.out.println(
                            "Failed to get move binding: " + e.getMessage() + "");
                    return null;
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.animationController = null;
        this.moveBinding = null;
        this.rigidbody = null;
        this.collider = null;
        this.collisionQuad = null;
        this.shadowEllipse = null;
        this.shadowVertex = null;
    }

    public FiniteStateMachine getAnimationController() {
        return this.animationController;
    }

    public boolean isMoving() {
        if (this.moveBinding != null)
            return this.moveBinding.getValue().mag() > 0;
        return false;
    }

    private void loadAnimations() {
        PApplet parentSketch = Game.getInstance().getParent();
        // animation states
        AnimationState idle = new AnimationState("Idle", true);
        idle.loadAllAnimations(parentSketch, idle, "animations/GirlSampleReadyIdle/GirlSample_ReadyIdle", "Idle", 4, 4,
                14, 100);
        AnimationState run = new AnimationState("Run", true);
        run.loadAllAnimations(parentSketch, run, "animations/GirlSampleRun/GirlSample_Run", "Run", 2, 4, 5, 100);
        run.setAnimationSpeed(0.5f);
        // transitions
        StateTransition toRun = new StateTransition(run, gameObject -> {
            Player player = (Player) gameObject;
            return player.isMoving();
        });
        idle.addTransition(toRun);
        StateTransition toIdle = new StateTransition(idle, gameObject -> {
            Player player = (Player) gameObject;
            return !player.isMoving();
        });
        run.addTransition(toIdle);
        // set initial state
        this.animationController.setState(idle);
        idle.start(this.facing);
        this.setSpriteImage(idle.getCurrentFrame());
    }

    void move(PVector direction, float inputDuration) {
        PVector movement = direction.copy();
        PVector currentVelocity = this.rigidbody.velocity();
        float rampScale = PApplet.min(inputDuration / this.rampUpDuration, 1 - this.smoothFactor);
        PVector targetVelocity = PVector.lerp(currentVelocity, movement.mult(this.speed), rampScale);
        this.rigidbody.addVelocity(targetVelocity.sub(currentVelocity));
    }

    @Override
    public void update() {
        super.update();
        this.animationController.update();
        if (this.moveBinding != null) {
            float deltaTime = Time.INSTANCE.deltaTime();
            Camera camera = Game.getInstance().getCamera();
            float cameraRotation = camera.transform().rotationInRadians();
            PVector inputVector = this.moveBinding.getValue();
            PVector direction = inputVector.copy().rotate(-cameraRotation);
            // move if there is movement input
            if (direction.mag() > 0) {
                this.inputDuration += deltaTime;
                this.move(direction, this.inputDuration);
                this.facing = CardinalDirection
                        .fromDegrees(
                                (float) Math.toDegrees(inputVector.heading()) - camera.transform().rotation() + 90f);
            } else {
                this.inputDuration = 0;
            }
        }
    }

    @Override
    public CardinalDirection direction() {
        return this.facing;
    }

    @Override
    public Set<Renderable> getChildren() {
        // draws a shadow
        this.shadowEllipse = ShadowCaster.castSphere(this.shadowVertex, 30f, this.transform,
                GlobalLight.Dawn);
        return Set.of(this.shadowEllipse);
    }
}
