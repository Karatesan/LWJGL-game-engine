package pl.karatesan.engine.utils;

import org.joml.Vector2d;
import org.joml.Vector2f;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Utilities {
  private static Map<String, Integer> counters = new HashMap<>();

  private static DecimalFormat df = new DecimalFormat("#.####");

  public static void printVector2(Vector2f vec, String desc) {
    System.out.println(desc + df.format(vec.x) + " " + df.format(vec.y));
  }

  public static void printVector2(Vector2d vec, String desc) {
    System.out.println(desc + df.format(vec.x) + " " + df.format(vec.y));
  }

  public static void printDouble(double value){
    System.out.println(df.format(value));
  }

  public static void printVector2(float x, float y, String desc) {
    System.out.println(desc + df.format(x) + " " + df.format(y));
  }

  public static void printVector2(double x, double y, String desc) {
    System.out.println(desc + df.format(x) + " " + df.format(y));
  }

  public static void printVector2WithDelay(int delay, Vector2f vec, String desc) {
    counters.putIfAbsent(desc, 0);
    Integer counter = counters.get(desc);
    if (counter == 0) {
      counters.put(desc, counter + 1);
     // printVector2(vec, desc);
    } else {
      if (counter == delay) counters.put(desc, 0);
      else counters.put(desc, counter + 1);
    }
  }

    public static void printVector2WithDelay(int delay, Vector2d vec, String desc) {
        counters.putIfAbsent(desc, 0);
        Integer counter = counters.get(desc);
        if (counter == 0) {
            counters.put(desc, counter + 1);
           // printVector2(vec, desc);
        } else {
            if (counter == delay) counters.put(desc, 0);
            else counters.put(desc, counter + 1);
        }
    }

    public static String trunctate(float f){
      return df.format(f);
    }

    public static String trunctate(double f){
        return df.format(f);
    }
}
