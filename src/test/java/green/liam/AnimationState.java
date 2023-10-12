package green.liam;

import java.util.HashMap;
import java.util.Map;

import green.liam.base.Game;
import green.liam.base.GameObject;
import green.liam.rendering.SpriteAnimation;
import green.liam.shape.Sprite;
import green.liam.util.CardinalDirection;
import processing.core.PApplet;
import processing.core.PImage;

public class AnimationState extends State {
    private final Map<String, CardinalDirection> FILENAME_SUFFIXES_TO_DIRECTIONS = new HashMap<>() {
        {
            this.put("_Up", CardinalDirection.North);
            this.put("_UpRight", CardinalDirection.Northeast);
            this.put("_Right", CardinalDirection.East);
            this.put("_DownRight", CardinalDirection.Southeast);
            this.put("_Down", CardinalDirection.South);
            this.put("_DownLeft", CardinalDirection.Southwest);
            this.put("_Left", CardinalDirection.West);
            this.put("_UpLeft", CardinalDirection.Northwest);
        }
    };

    boolean shouldLoop = true;

    Map<CardinalDirection, SpriteAnimation> animations = new HashMap<>();
    SpriteAnimation currentAnimation = null;
    int currentFrameIndex = 0;

    public AnimationState(String name, boolean shouldLoop) {
        super(name);
        this.shouldLoop = shouldLoop;
    }

    @Override
    public void destroy() {
        super.destroy();
        this.animations.values().forEach(animation -> {
            Game game = Game.getInstance();
            for (PImage frame : animation.frames()) {
                game.forceCacheRemoval(frame);
            }
        });
    }

    public void loadAnimation(PApplet sketch, AnimationState animationState, String path, String name, int rows,
            int cols, int frames,
            int frameDurationMs,
            CardinalDirection direction) {
        PImage spriteSheet = sketch.loadImage(path);
        SpriteAnimation animation = new SpriteAnimation(name, spriteSheet, frames, spriteSheet.width,
                spriteSheet.height, rows, cols, frameDurationMs);
        animationState.animations.put(direction, animation);
    }

    public void loadAllAnimations(PApplet sketch, AnimationState animationState, String basePath, String animationName,
            int rows,
            int cols, int frames,
            int frameDurationMs) {
        for (Map.Entry<String, CardinalDirection> entry : this.FILENAME_SUFFIXES_TO_DIRECTIONS.entrySet()) {
            String suffix = entry.getKey();
            CardinalDirection direction = entry.getValue();
            String path = basePath + suffix + ".png";
            String name = animationName + suffix;
            this.loadAnimation(sketch, animationState, path, name, rows, cols, frames, frameDurationMs, direction);
        }
        this.start(CardinalDirection.North);
    }

    public void setAnimationSpeed(float speed) {
        this.animations.values().forEach(animation -> animation.setAnimationSpeed(speed));
    }

    @Override
    protected State stateTick(GameObject parentObject) {
        CardinalDirection direction = CardinalDirection.North;
        PImage nextSprite = this.currentAnimation.currentFrame();

        if (parentObject instanceof HasDirection hd) {
            direction = hd.cameraRelativeDirection();
        }
        SpriteAnimation animation = this.animations.get(direction);
        if (animation != null) {
            if (this.currentAnimation != animation) {
                this.startAnimation(animation);
            }
            nextSprite = this.currentAnimation.update();
            this.currentFrameIndex = this.currentAnimation.currentFrameIndex();
        }
        if (parentObject instanceof Sprite sprite) {
            if (nextSprite != null)
                sprite.setSpriteImage(nextSprite);
        }
        return this;
    }

    private void startAnimation(SpriteAnimation animation) {
        this.currentAnimation = animation;
        this.currentAnimation.setFrame(this.currentFrameIndex);
        if (this.shouldLoop) {
            this.currentAnimation.loop();
        } else {
            this.currentAnimation.play();
        }
    }

    public void start(CardinalDirection direction) {
        this.startAnimation(this.animations.get(direction));
    }

    public PImage getCurrentFrame() {
        return this.currentAnimation.currentFrame();
    }
}
