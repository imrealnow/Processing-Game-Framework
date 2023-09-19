package green.liam.rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import green.liam.base.Time;
import green.liam.shape.Sprite;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * The SpriteAnimation class handles sprite-based animations using a sprite
 * sheet and animation
 * metadata.
 */
public class SpriteAnimation {

    public enum AnimationState {
        STOPPED, PLAYING, LOOPING, PAUSED
    }

    private final JSONObject animationData;
    private final PImage spriteSheet;
    private final Sprite spriteObject;

    private PImage[] frames;
    private final String name;
    private final int frameCount;
    private final int width;
    private final int height;
    private int frameDurationMs;

    private Map<Integer, Runnable> onFrameCallbacks = new HashMap<>();

    private int currentFrame = 0;
    private float currentFrameTime = 0.0f;
    private float animationSpeed = 0.7f;
    private AnimationState animationState = AnimationState.STOPPED;
    private AnimationState previousAnimationState = AnimationState.STOPPED;

    public SpriteAnimation(String name, JSONObject animationData, PImage spriteSheet,
            Sprite spriteObject) {
        this.name = name;
        this.animationData = animationData;
        this.spriteSheet = spriteSheet;
        this.spriteObject = spriteObject;

        JSONArray frameArray = animationData.getJSONArray("frames");
        this.frameCount = frameArray.size();
        this.frames = new PImage[this.frameCount];
        this.width = animationData.getJSONObject("meta").getJSONObject("size").getInt("w");
        this.height = animationData.getJSONObject("meta").getJSONObject("size").getInt("h");

        this.initializeFrames(frameArray);
    }

    /**
     * Initializes the frames from the sprite sheet based on the provided animation
     * data.
     *
     * @param frameArray
     *            JSON array containing frame data.
     */
    private void initializeFrames(JSONArray frameArray) {
        int frameDurationSum = 0;
        for (int i = 0; i < this.frameCount; i++) {
            JSONObject frameData = frameArray.getJSONObject(i);
            JSONObject frame = frameData.getJSONObject("frame");
            int x = frame.getInt("x");
            int y = frame.getInt("y");
            int w = frame.getInt("w");
            int h = frame.getInt("h");
            frameDurationSum += frameData.getInt("duration");
            this.frames[i] = this.spriteSheet.get(x, y, w, h);
        }
        this.frameDurationMs = (int) Math.round(frameDurationSum / (double) this.frameCount);
    }

    private void setState(AnimationState state) {
        this.previousAnimationState = this.animationState;
        this.animationState = state;
    }

    private void reset() {
        this.setFrame(0);
    }

    private void setFrame(int frame) {
        frame = frame % this.frameCount;
        if (this.onFrameCallbacks.containsKey(frame)) {
            this.onFrameCallbacks.get(frame).run();
        }
        this.currentFrame = frame;
        this.currentFrameTime = 0.0f;
        this.spriteObject.setSpriteImage(this.frames[this.currentFrame]);
    }

    /**
     * Adds a callback to be executed when a specific frame is reached.
     *
     * @param frameIndex
     *            Frame index.
     * @param callback
     *            Callback to be executed.
     */
    public void addOnFrameCallback(int frameIndex, Runnable callback) {
        this.onFrameCallbacks.put(frameIndex, callback);
    }

    /**
     * Sets the animation speed.
     *
     * @param speed
     *            Animation speed.
     * @return Current SpriteAnimation instance.
     */
    public SpriteAnimation setAnimationSpeed(float speed) {
        this.animationSpeed = speed;
        return this;
    }

    /**
     * Sets playing the animation in a loop.
     *
     * @return Current SpriteAnimation instance.
     */
    public SpriteAnimation loop() {
        this.setState(AnimationState.LOOPING);
        this.reset();
        return this;
    }

    /**
     * Starts or restarts the animation from the first frame.
     * 
     * @return Current SpriteAnimation instance.
     */
    public SpriteAnimation play() {
        this.setState(AnimationState.PLAYING);
        this.reset();
        return this;
    }

    /**
     * Stops the animation.
     * 
     * @return Current SpriteAnimation instance.
     */
    public SpriteAnimation stop() {
        this.setState(AnimationState.STOPPED);
        return this;
    }

    /**
     * Pauses the animation without resetting it.
     * 
     * @return Current SpriteAnimation instance.
     */
    public SpriteAnimation pause() {
        this.setState(AnimationState.PAUSED);
        return this;
    }

    /**
     * Resumes the animation from where it was paused.
     * 
     * @return Current SpriteAnimation instance.
     */
    public SpriteAnimation resume() {
        if (this.animationState == AnimationState.PAUSED) {
            this.setState(this.previousAnimationState);
        } else if (this.animationState == AnimationState.STOPPED) {
            this.play();
        }
        return this;
    }

    /**
     * Updates the animation state based on the elapsed time.
     */
    public void update() {
        if (this.animationSpeed == 0.0f
                || this.frameCount <= 1
                || this.animationState == AnimationState.STOPPED
                || this.animationState == AnimationState.PAUSED) {
            return;
        }

        if (this.animationState == AnimationState.PLAYING && this.currentFrame == this.frameCount - 1) {
            this.setState(AnimationState.STOPPED);
        }

        this.currentFrameTime += Time.INSTANCE.deltaTime();
        float frameDuration = this.frameDurationMs / 1000.0f;

        if (Math.abs(this.currentFrameTime) >= Math.abs(frameDuration / this.animationSpeed)) {
            int frameDelta = (int) Math.signum(this.animationSpeed);
            int nextFrame = this.currentFrame + frameDelta % this.frameCount;
            if (nextFrame < 0) {
                nextFrame = this.frameCount - 1;
            }
            this.setFrame(nextFrame);
        }
    }

}
