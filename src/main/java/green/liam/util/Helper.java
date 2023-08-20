package green.liam.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import processing.core.PImage;

public class Helper {
    public static float safeDivide(float a, float b) {
        if (b == 0)
            return 0;
        return a / b;
    }

    public static PImage loadImageFromResource(String path) {
        try {
            BufferedImage bufferedImage =
                    ImageIO.read(Helper.class.getResourceAsStream("/" + path));

            // Convert BufferedImage to PImage
            PImage resultingImage = new PImage(bufferedImage);
            return resultingImage;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the image: " + path);
            return null;
        }
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

    public static float smooveMoveTowards(float current, float target, float maxDelta,
            float smoothFactor) {
        float difference = target - current;
        float direction = Math.signum(difference);
        if (Math.abs(current - target) <= 1f) {
            return target;
        }
        float sigmoid = 1 / (1 + (float) Math.exp(-smoothFactor * difference));
        return current + maxDelta * sigmoid * direction;
    }

    public static float smoothMoveTowardsAngle(float currentAngle, float targetAngle,
            float maxDelta, float smoothFactor) {
        // Normalize angles to range [-180, 180)
        float normalizedCurrent = ((currentAngle + 180) % 360 + 360) % 360 - 180;
        float normalizedTarget = ((targetAngle + 180) % 360 + 360) % 360 - 180;

        // Calculate the shortest difference
        float difference;
        if (normalizedTarget >= normalizedCurrent) {
            difference = (normalizedTarget - normalizedCurrent <= 180)
                    ? normalizedTarget - normalizedCurrent
                    : normalizedTarget - normalizedCurrent - 360;
        } else {
            difference = (normalizedCurrent - normalizedTarget <= 180)
                    ? normalizedTarget - normalizedCurrent
                    : normalizedTarget - normalizedCurrent + 360;
        }

        // If the angles are close, just set to the target
        if (Math.abs(difference) < maxDelta) {
            return targetAngle;
        }

        // Apply sigmoid smoothing
        float sigmoid = 1 / (1 + (float) Math.exp(-smoothFactor * Math.abs(difference)));
        float change = maxDelta * sigmoid * Math.signum(difference); // Ensuring change is applied
                                                                     // in the correct direction

        // Add the calculated change to the current angle and return
        return currentAngle + change;
    }

}
