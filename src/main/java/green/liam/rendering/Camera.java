package green.liam.rendering;

import green.liam.base.GameObject;
import green.liam.events.Observer;
import green.liam.events.TransformChangeEvent;
import green.liam.rendering.camera.CameraProjector;
import processing.core.PMatrix2D;

public class Camera extends GameObject implements Observer<TransformChangeEvent>, AutoCloseable {
    private CameraProjector currentProjector;
    private PMatrix2D projectionMatrix;

    public Camera(CameraProjector initialProjector) {
        super();
        this.transform.addChangeObserver(this);
        this.currentProjector = initialProjector;
        this.updateMatrix();
    }

    public float getYScale() {
        return this.currentProjector.getYScale();
    }

    public float depthAlpha() {
        return this.currentProjector.depthAlpha();
    }

    private void updateMatrix() {
        this.projectionMatrix = this.currentProjector.getProjectionMatrix(this.transform);
    }

    public PMatrix2D getProjectionMatrix() {
        this.updateMatrix();
        return this.projectionMatrix;
    }

    public void switchProjector(CameraProjector newProjector) {
        this.currentProjector = newProjector;
        this.updateMatrix();
    }

    @Override
    public void onNotify(TransformChangeEvent event) {
        this.updateMatrix();
    }

    @Override
    public void close() throws Exception {
        this.transform.removeChangeObserver(this);
    }
}
