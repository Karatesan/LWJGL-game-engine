package pl.karatesan.engine.utils;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

// A service that owns one deterministic RNG for the whole game.
public final class RandomService {

  private final RandomGenerator rng;

  public RandomService(long seed) {
    this.rng = RandomGeneratorFactory.of("L64X128MixRandom").create(seed);
  }

  public int randInt(int bound) {
    return rng.nextInt(bound);
  }

  public int randIntInRange(int min, int max) {
    if (min > max) throw new IllegalArgumentException("Min cannot be higher than max");
    return randInt(max - min + 1) + min;
  }

  public float randFloat() {
    return rng.nextFloat(); // 0.0f–1.0f
  }

  public float randFloatInRange(float min, float max) {
    return randFloat() * (max - min) + min;
  }

  public double randDouble() {
    return rng.nextDouble(); // mean 0, stdev 1
  }

  public double randomDoubleInRange(double min, double max) {
    return randDouble() * (max - min) + min;
  }

  public double nextGaussian() {
    return rng.nextGaussian();
  }

  public RandomGenerator getGenerator() {
    return rng;
  }
}
