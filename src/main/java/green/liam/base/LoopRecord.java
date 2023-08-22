package green.liam.base;

import java.util.Timer;
import java.util.TimerTask;

public record LoopRecord(
  String key,
  Timer timer,
  TimerTask task,
  int timesPerSecond
) {
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
