package green.liam.rendering;

import green.liam.base.Transform;
import green.liam.events.Observer;
import green.liam.events.TransformChangeEvent;
import green.liam.rendering.camera.CameraProjector;
import processing.core.PMatrix2D;

public class Camera implements Observer<TransformChangeEvent>, AutoCloseable {
    private Transform transform;
    private CameraProjector currentProjector;
    private PMatrix2D matrix;

    public Camera(Transform transform, CameraProjector initialProjector) {
        this.transform = transform;
        this.transform.addChangeObserver(this);
        this.currentProjector = initialProjector;
    }

    private void updateMatrix() {
        this.matrix = this.currentProjector.getProjectionMatrix(this.transform);
    }

    public PMatrix2D getMatrix() {
        return this.matrix;
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
