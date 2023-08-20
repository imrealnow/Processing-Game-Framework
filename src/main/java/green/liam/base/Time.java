package green.liam.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public enum Time {
    INSTANCE;

    final Map<String, LoopRecord> loops = Collections.synchronizedMap(new HashMap<>());
    final long startTime = System.currentTimeMillis();

    long lastTime = System.currentTimeMillis();
    float timeScale = 1f;

    private Time() {}

    public float deltaTime() {
        return (System.currentTimeMillis() - this.lastTime) / 1000f * this.timeScale;
    }

    public float time() {
        return (System.currentTimeMillis() - this.startTime) / 1000f;
    }

    public void update() {
        this.lastTime = System.currentTimeMillis();
    }

    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
    }

    /**
     * Starts a loop that calls the given function a specified amount of times per second.
     *
     * @param name The name of the loop to be used as a key.
     * @param timesPerSecond how many times per second to call the function.
     * @param callback The function to call.
     * @return The timer object that is running the loop.
     */
    public Timer loop(String name, int timesPerSecond, Runnable callback) {
        if (this.loops.containsKey(name)) {
            this.loops.get(name).timer().cancel();
        }
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000 / timesPerSecond);
        this.loops.put(name, new LoopRecord(name, timer, task, timesPerSecond));
        return timer;
    }

    /**
     * Cancels a loop with the given name.
     *
     * @param name The name of the loop to cancel.
     */
    public void cancelLoop(String name) {
        if (this.loops.containsKey(name)) {
            this.loops.get(name).cancel();
            this.loops.remove(name);
        }
    }

    /**
     * Runs the callback after the specified number of milliseconds.
     *
     * @param milliseconds the delay in milliseconds
     * @param callback the callback to run
     */
    public void delayedInvoke(int milliseconds, Runnable callback) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        };
        timer.schedule(task, milliseconds);
    }

    /**
     * Record for saving info about a loop
     */
    record LoopRecord(String key, Timer timer, TimerTask task, int timesPerSecond) {
        /**
         * Gets the amount of times per second the loop is running.
         * 
         * @return loop calls per second.
         */
        public int timesPerSecond() {
            return this.timesPerSecond;
        }

        /**
         * Returns the loop's time interval.
         * 
         * @return the interval in milliseconds between each call of the loop.
         */
        public float interval() {
            return 1000f / this.timesPerSecond;
        }

        public void cancel() {
            Time.INSTANCE.cancelLoop(this.key);
        }
    }
}
