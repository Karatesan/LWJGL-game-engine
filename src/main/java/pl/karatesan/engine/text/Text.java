package pl.karatesan.engine.text;

import org.joml.Vector2f;

import java.util.List;

public class Text {

  private List<FontGlyph> glyphs;
  private boolean isUpdated;
  private String text;
  private float scale;
  private Vector2f position;
  private float height;
  private float width;
  private UIAnchor anchor;
  private boolean shouldRender;

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
    init(text, scale, atlas);
    this.position = hud.getPosition(anchor, xOffset, yOffset, width, height);
  }

  public Text(String text, Vector2f position, FontAtlas atlas, float scale) {
    init(text, scale, atlas);
    this.position = position;
  }

  private void init(String text, float scale, FontAtlas atlas) {
    this.text = text;
    this.height = atlas.getLineHeight() * scale;
    glyphs = atlas.getGlyphsForText(text);
    for (FontGlyph glyph : glyphs) {
      width += glyph.getWidth();
     // if (text.equals("GAME OVER")) System.out.println(glyph.getWidth());
    }
    width *= scale;
    this.scale = scale;
    isUpdated = true;
    this.shouldRender = true;
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

  public void setPosition(UIAnchor anchor, float xOffset, float yOffset, HUD hud) {
    this.position = hud.getPosition(anchor, xOffset, yOffset, width, height);
  }

  public void setPosition(Vector2f position, float xOffset, float yOffset) {
    this.position.set(position.x + xOffset, position.y + yOffset);
  }

  public void setPosition(float x, float y) {
    this.position.set(x, y);
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

  public boolean shouldRender() {
    return shouldRender;
  }

  public void setShouldRender(boolean shouldRender) {
    this.shouldRender = shouldRender;
  }
}
