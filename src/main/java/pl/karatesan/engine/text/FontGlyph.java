package pl.karatesan.engine.text;

public final class FontGlyph {

  private final int charId;
  private final float x;
  private final float y;
  private final float width;
  private final float height;
  private final float xOffset;
  private final float yOffset;
  private final float xAdvance;

  private FontGlyph(Builder builder) {
    this.charId = builder.charId;
    this.x = builder.x;
    this.y = builder.y;
    this.width = builder.width;
    this.height = builder.height;
    this.xOffset = builder.xOffset;
    this.yOffset = builder.yOffset;
    this.xAdvance = builder.xAdvance;
  }

  @Override
  public String toString() {
    return "FontGlyph{"
        + "charId="
        + charId
        + ", x="
        + x
        + ", y="
        + y
        + ", width="
        + width
        + ", height="
        + height
        + ", xOffset="
        + xOffset
        + ", yOffset="
        + yOffset
        + ", xAdvance="
        + xAdvance
        + '}';
  }

  public int getCharId() {
    return charId;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }

  public float getXOffset() {
    return xOffset;
  }

  public float getYOffset() {
    return yOffset;
  }

  public float getXAdvance() {
    return xAdvance;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private Integer charId; // required; keep boxed so we can detect if it was set
    private float x;
    private float y;
    private float width;
    private float height;
    private float xOffset;
    private float yOffset;
    private float xAdvance;

    @Override
    public String toString() {
      return "Builder{"
          + "charId="
          + charId
          + ", x="
          + x
          + ", y="
          + y
          + ", width="
          + width
          + ", height="
          + height
          + ", xOffset="
          + xOffset
          + ", yOffset="
          + yOffset
          + ", xAdvance="
          + xAdvance
          + '}';
    }

    private Builder() {}

    public Builder charId(int value) {
      this.charId = value;
      return this;
    }

    public Builder x(float value) {
      this.x = value;
      return this;
    }

    public Builder y(float value) {
      this.y = value;
      return this;
    }

    public Builder width(float value) {
      this.width = value;
      return this;
    }

    public Builder height(float value) {
      this.height = value;
      return this;
    }

    public Builder xOffset(float value) {
      this.xOffset = value;
      return this;
    }

    public Builder yOffset(float value) {
      this.yOffset = value;
      return this;
    }

    public Builder xAdvance(float value) {
      this.xAdvance = value;
      return this;
    }

    public FontGlyph build() {
      if (charId == null) {
        throw new IllegalStateException("charId must be provided");
      }
      return new FontGlyph(this);
    }
  }
}
