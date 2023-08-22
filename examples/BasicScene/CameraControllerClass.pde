class CameraController extends Component {
    Camera camera;
    Transform target;
    float angle = 0f;
    
    public CameraController(GameObject gameObject, Transform target) {
        super(gameObject);
        if (gameObject instanceof Camera) {
            this.camera = (Camera) gameObject;
        } else {
            throw new IllegalArgumentException("CameraController must be attached to a Camera");
        }
        this.target = target;
    }
    
    @Override
    public void update() {
        this.angle += 10f * Time.INSTANCE.deltaTime();
        PVector targetPosition = this.target.position();
        PVector cameraPosition = this.camera.transform().position();
        PVector targetDirection =
        PVector.sub(targetPosition, cameraPosition).mult(Time.INSTANCE.deltaTime() * 4f);
        PVector newPosition = PVector.add(cameraPosition, targetDirection);
        this.camera.transform().setPosition(newPosition);
        this.camera.transform().setRotation(this.angle);
    }
}
