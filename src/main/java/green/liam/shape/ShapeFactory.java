package green.liam.shape;

import green.liam.base.Transform;
import green.liam.util.Helper;
import java.lang.reflect.Constructor;
import processing.core.PVector;

public class ShapeFactory {

  public static <T extends Shape> T create(Class<T> type, Object... args) {
    try {
      if (args == null || args.length == 0) {
        Constructor<T> constructor = type.getConstructor();
        return constructor.newInstance();
      } else {
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
          argTypes[i] = Helper.getPrimitiveClass(args[i].getClass());
        }
        Constructor<T> constructor = type.getConstructor(argTypes);
        return constructor.newInstance(args);
      }
    } catch (Exception e) {
      System.out.println("Error creating shape");
      e.printStackTrace();
      return null;
    }
  }

  public static <T extends Shape> T create(
    Class<T> type,
    PVector position,
    Object... args
  ) {
    Shape shape = create(type, args);
    shape.transform().setPosition(position);
    return type.cast(shape);
  }

  public static <T extends Shape> T create(
    Class<T> type,
    Transform parent,
    PVector localPosition,
    Object... args
  ) {
    Shape shape = create(type, args);
    shape.transform().setParent(parent);
    shape.transform().setLocalPosition(localPosition);
    return type.cast(shape);
  }

  public static <T extends Shape> T create(
    Class<T> type,
    PVector position,
    float rotation,
    PVector scale,
    Object... args
  ) {
    Shape shape = create(type, args);
    shape.transform().setPosition(position);
    shape.transform().setRotation(rotation);
    shape.transform().setScale(scale);
    return type.cast(shape);
  }
}
