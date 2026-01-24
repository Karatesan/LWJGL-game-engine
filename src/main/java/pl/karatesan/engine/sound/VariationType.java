package pl.karatesan.engine.sound;

public enum VariationType {
  HIGH(0.5f),
  MEDIUM(0.75f),
  LOW(0.9f),
  NONE(1f);

  private final float value;

  VariationType(float v) {
    this.value = v;
  }

  public float getValue() {
    return value;
  }
}
