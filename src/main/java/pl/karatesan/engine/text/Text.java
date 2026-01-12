package pl.karatesan.engine.text;

import org.joml.Vector2f;

import java.util.List;
import java.util.Vector;

public class Text {

  private List<FontGlyph> glyphs;
  private boolean isUpdated;
  private String text;
  private FontAtlas atlas;
  private float scale;
  private Vector2f position;
  private float height;
  private float width;
  private UIAnchor anchor;

  public Text(
      String text, UIAnchor anchor, float xOffset, float yOffset, FontAtlas fontAtlas, HUD hud) {
    this(text, anchor, xOffset, yOffset, fontAtlas, 1, hud);
  }

  public Text(
      String text,
      UIAnchor anchor,
      float xOffset,
      float yOffset,
      FontAtlas atlas,
      float scale,
      HUD hud) {
    this.text = text;
    this.anchor = anchor;
    this.height = atlas.getLineHeight() * scale;
    glyphs = atlas.getGlyphsForText(text);
    for (FontGlyph glyph : glyphs) {
      width += glyph.getWidth();
    }
    width*=scale;
    this.position = hud.getPosition(anchor, xOffset, yOffset, width, height);
    this.scale = scale;
    isUpdated = true;
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

  public void flushUpdate() {
    isUpdated = false;
  }

  public void update(String newText, FontAtlas fontAtlas) {
    this.text = newText;
    glyphs = fontAtlas.getGlyphsForText(text);
    isUpdated = true;
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

  public Vector2f getPosition() {
    return position;
  }
}
