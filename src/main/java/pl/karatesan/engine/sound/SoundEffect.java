package pl.karatesan.engine.sound;

import pl.karatesan.engine.utils.RandomService;

import java.util.List;

public class SoundEffect {
  private final List<String> bufferNames; // Key to the raw audio data
  private float volume; // 0.0 to 1.0
  private VariationType pitchVariance; // e.g., 0.1 for +/- 10%
  private final boolean looping;
  private final RandomService randomService;

  public SoundEffect(
      List<String> bufferNames,
      float volume,
      VariationType pitchVariance,
      RandomService randomService) {
    this.bufferNames = bufferNames;
    this.volume = volume;
    this.pitchVariance = pitchVariance;
    this.randomService = randomService;
    this.looping = false;
  }

  public String getBufferName() {
    return bufferNames.get(randomService.randIntInRange(0, bufferNames.size() - 1));
  }

  public float getVolume() {
    return volume;
  }

  public VariationType getPitchVariance() {
    return pitchVariance;
  }

  public float calculatePitchVariance() {
    float variance = pitchVariance.getValue();
    float randOffset = randomService.randFloatInRange(0, (1 - variance) * 2);
    return variance + randOffset;
  }

  public void setPitchVariance(VariationType pitchVariance) {
    this.pitchVariance = pitchVariance;
  }

  public void setVolume(float volume) {
    this.volume = volume;
  }
}
