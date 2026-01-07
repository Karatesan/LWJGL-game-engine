package pl.karatesan.engine.text;

import java.util.List;

public class Text {

  private List<FontGlyph> glyphs;
  private boolean isUpdated;
  private String text;
  private FontAtlas atlas;
  private float scale;

  public Text(String text, FontAtlas fontAtlas) {
    this.text = text;
    isUpdated = true;
    glyphs = fontAtlas.getGlyphsForText(text);
    scale = 1;
  }

  public List<FontGlyph> getGlyphs() {
    return glyphs;
  }

  public void setGlyphs(List<FontGlyph> glyphs) {
    this.glyphs = glyphs;
  }

  public boolean isUpdated() {
    return isUpdated;
  }

  public void setUpdated(boolean updated) {
    isUpdated = updated;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }
}
