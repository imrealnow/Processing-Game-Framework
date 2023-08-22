import java.util.concurrent.CompletableFuture;

class Player extends Box{
    float speed = 10f;
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
        PVector currentPosition = transform.position();
        movement.mult(this.speed);
        transform.setPosition(PVector.add(currentPosition, movement));
    }
    
    @Override
    public void update() {
        if (this.moveBinding != null) {
            PVector direction = this.moveBinding.getValue();
            if (direction.mag() > 0) {
                this.move(direction);
            }
        }
    }
}