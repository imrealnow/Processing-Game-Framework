import java.util.concurrent.CompletableFuture;

class Player extends Box {
    float speed = 5f;
    float yVelocity = 0f;
    Directional2DBinding moveBinding;
    
    Player(int x, int y, float width, float length, float height) {
        super(width, length, height);
        this.transform.setPosition(new PVector(x, y));
        CompletableFuture<InputBinding<?> > moveBindingFuture =
        InputManager.INSTANCE.getInputBinding("move");
        moveBindingFuture.thenAccept(binding -> {
            this.moveBinding = (Directional2DBinding) binding;
        }).exceptionally((e) -> {
            System.out.println("Failed to get move binding: " + e.getMessage() + "");
            return null;
        });
    }
    
    void move(PVector direction) {
        PVector movement = direction.copy();
        PVector currentPosition = this.transform.position();
        movement.mult(this.speed);
        this.transform.setPosition(PVector.add(currentPosition, movement));
    }
    
    @Override
    public void update() {
        if (this.moveBinding != null) {
            Camera camera = Game.getInstance().getCamera();
            float cameraRotation = camera.transform().rotationInRadians();
            PVector direction = this.moveBinding.getValue().rotate( - cameraRotation);
            this.transform.setRotation( - camera.transform().rotation());
            // apply y velocity
            float height = this.transform.height();
            float newHeight = PApplet.max(height + this.yVelocity * Time.INSTANCE.deltaTime(), 0);
            if (newHeight <= 0) {
                this.yVelocity = 0;
            }
            this.transform.setHeight(newHeight);
            // apply gravity if not on the ground
            if (height != 0)
                this.yVelocity -= 30f;
            // move if there is movement input
            if (direction.mag() > 0) {
                this.move(direction);
            }
        }
    }
    
    public void jump() {
        if (this.transform.height() == 0)
            this.yVelocity = 500f;
        System.out.println("Jump!");
    }
}