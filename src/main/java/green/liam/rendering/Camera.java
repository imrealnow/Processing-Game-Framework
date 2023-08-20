package green.liam.rendering;

import java.util.concurrent.CompletableFuture;
import green.liam.base.Game;
import green.liam.input.Directional2DBinding;
import green.liam.input.InputBinding;
import green.liam.input.InputManager;
import processing.core.PMatrix2D;
import processing.core.PVector;

public enum Camera {
    MAIN;

    private Camera() {
        this.recalculateMatrix();
        CompletableFuture<InputBinding<?>> moveBindingFuture =
                InputManager.INSTANCE.getInputBinding("move");
        moveBindingFuture.thenAccept(binding -> {
            this.moveBinding = (Directional2DBinding) binding;
        }).exceptionally((e) -> {
            System.out.println("Camera failed to get move binding: " + e.getMessage() + "");
            return null;
        });
    }

    private PVector position = new PVector(0, 0);
    private float yaw = 45f;
    private float pitch = 100f;
    private float speed = 10f;
    private float cameraHeight = 200f;
    Directional2DBinding moveBinding;

    PMatrix2D cameraMatrix = new PMatrix2D();

    public PVector position() {
        return this.position;
    }

    public PVector worldPosition() {
        PVector copy = this.position.copy();
        return copy.mult(-0.5f);
    }

    public float yaw() {
        return this.yaw;
    }

    public float pitch() {
        return this.pitch;
    }

    public float getYScale() {
        return 2f / (float) Math.sqrt(2);
    }

    public void setPosition(PVector position) {
        this.position = position;
        this.recalculateMatrix();
    }

    public void setYaw(float yaw) {
        // limit yaw to -180 to 180 with looping around at boundaries
        this.yaw = yaw;
        this.recalculateMatrix();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        this.recalculateMatrix();
    }

    public PMatrix2D getMatrix() {
        return this.cameraMatrix.get();
    }

    private void recalculateMatrix() {
        PMatrix2D updatedCameraMatrix = new PMatrix2D();
        PMatrix2D rotationMatrix = new PMatrix2D();
        PMatrix2D scaleMatrix = new PMatrix2D();
        float yawRadians = (float) Math.toRadians(this.yaw);
        PVector position = this.position.copy().rotate(yawRadians);
        float yScale = 2f / (float) Math.sqrt(2);
        PVector halfScreenDimensions = Game.getInstance().getScreenDimensions().mult(0.5f);

        scaleMatrix.scale(1f, yScale);
        rotationMatrix.rotate(-yawRadians);
        updatedCameraMatrix.apply(rotationMatrix);
        updatedCameraMatrix.translate(-halfScreenDimensions.x - position.x,
                -halfScreenDimensions.y - this.cameraHeight - position.y);
        updatedCameraMatrix.apply(scaleMatrix);
        this.cameraMatrix = updatedCameraMatrix;
    }

    private void move(PVector direction) {
        PVector movement = direction.copy();
        movement.mult(this.speed);
        movement.rotate((float) Math.toRadians(360 - this.yaw) * -1);
        movement.x *= -1;
        this.position.add(movement);
        this.recalculateMatrix();
    }

    public void update() {
        if (this.moveBinding != null) {
            PVector direction = this.moveBinding.getValue();
            if (direction.mag() > 0) {
                this.move(direction);
            }
        }
    }

}
