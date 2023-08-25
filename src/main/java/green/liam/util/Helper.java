package green.liam.util;

import processing.core.PVector;

public class Helper {

  public static float safeDivide(float a, float b) {
    if (b == 0) return 0;
    return a / b;
  }

  public static Class<?> getPrimitiveClass(Class<?> wrapperType) {
    switch (wrapperType.getSimpleName()) {
      case "Float":
        return float.class;
      case "Integer":
        return int.class;
      case "Double":
        return double.class;
      case "Long":
        return long.class;
      case "Boolean":
        return boolean.class;
      case "Character":
        return char.class;
      case "Byte":
        return byte.class;
      case "Short":
        return short.class;
      case "Void":
        return void.class;
      default:
        return wrapperType; // returns the same class if not a recognized wrapper
    }
  }

  public static float smooveMoveTowards(
    float current,
    float target,
    float maxDelta,
    float smoothFactor
  ) {
    float difference = target - current;
    float direction = Math.signum(difference);
    if (Math.abs(current - target) <= 1f) {
      return target;
    }
    float sigmoid = 1 / (1 + (float) Math.exp(-smoothFactor * difference));
    return current + maxDelta * sigmoid * direction;
  }

  public static float smoothMoveTowardsAngle(
    float currentAngle,
    float targetAngle,
    float maxDelta,
    float smoothFactor
  ) {
    // Normalize angles to range [-180, 180)
    float normalizedCurrent = ((currentAngle + 180) % 360 + 360) % 360 - 180;
    float normalizedTarget = ((targetAngle + 180) % 360 + 360) % 360 - 180;

    // Calculate the shortest difference
    float difference;
    if (normalizedTarget >= normalizedCurrent) {
      difference =
        (normalizedTarget - normalizedCurrent <= 180)
          ? normalizedTarget - normalizedCurrent
          : normalizedTarget - normalizedCurrent - 360;
    } else {
      difference =
        (normalizedCurrent - normalizedTarget <= 180)
          ? normalizedTarget - normalizedCurrent
          : normalizedTarget - normalizedCurrent + 360;
    }

    // If the angles are close, just set to the target
    if (Math.abs(difference) < maxDelta) {
      return targetAngle;
    }

    // Apply sigmoid smoothing
    float sigmoid =
      1 / (1 + (float) Math.exp(-smoothFactor * Math.abs(difference)));
    float change = maxDelta * sigmoid * Math.signum(difference); // Ensuring change is applied
    // in the correct direction

    // Add the calculated change to the current angle and return
    return currentAngle + change;
  }

  public static PVector roundPVector(PVector vector, int decimalPlaces) {
    float multiplier = 10f;
    for (int i = 1; i < decimalPlaces; i++) {
      multiplier *= 10f;
    }
    return new PVector(
      Math.round(vector.x * multiplier) / multiplier,
      Math.round(vector.y * multiplier) / multiplier,
      Math.round(vector.z * multiplier) / multiplier
    );
  }

  public static float[] colourLerp(float[] start, float[] end, float t) {
    float[] result = new float[4];
    for (int i = 0; i < 4; i++) {
      result[i] = start[i] + (end[i] - start[i]) * t;
    }
    return result;
  }
}
