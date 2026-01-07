package pl.karatesan.engine.text;

import pl.karatesan.engine.texture.Texture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class FontAtlas {

  private int lineHeight;
  private int base;
  private int scaleW;
  private int scaleH;
  private final Map<Character, FontGlyph> atlas;
  private Texture fontTexture;

  public FontAtlas() {
    this.atlas = new HashMap<>();
    fontTexture = new Texture("/fonts/Font.png", GL_CLAMP_TO_EDGE);
  }

  public void init() {
    try (BufferedReader bf =
        new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(this.getClass().getResourceAsStream("/fonts/Font.txt"))))) {
      String s = "";
      while (s != null) {
        s = bf.readLine();
        if (s.startsWith("char ")) {
          String[] split = s.split(" ");
          FontGlyph.Builder builder = FontGlyph.builder();
          for (String token : split) {
            String[] param = token.split("=");
            if (param.length > 1) {
              switch (param[0]) {
                case "id" -> builder.charId(Integer.parseInt(param[1]));
                case "x" -> builder.x(Integer.parseInt(param[1]));
                case "y" -> builder.y(Integer.parseInt(param[1]));
                case "width" -> builder.width(Integer.parseInt(param[1]));
                case "height" -> builder.height(Integer.parseInt(param[1]));
                case "xoffset" -> builder.xOffset(Integer.parseInt(param[1]));
                case "yoffset" -> builder.yOffset(Integer.parseInt(param[1]));
                case "xadvance" -> builder.xAdvance(Integer.parseInt(param[1]));
              }
            }
          }
          FontGlyph glyph = builder.build();
          atlas.put((char) glyph.getCharId(), glyph);
        }
        if (s.startsWith("common")) {
          String[] split = s.split(" ");
          for (String token : split) {
            String[] param = token.split("=");
            if (param.length > 1) {
              if (param[0].equals("lineHeight")) lineHeight = Integer.parseInt(param[1]);
              if (param[0].equals("base")) base = Integer.parseInt(param[1]);
              if (param[0].equals("scaleW")) scaleW = Integer.parseInt(param[1]);
              if (param[0].equals("scaleH")) scaleH = Integer.parseInt(param[1]);
            }
          }
        }
      }
    } catch (IOException ex) {
      System.out.println("IO");
    } catch (NullPointerException ex) {
      System.out.println("NULL");
    }
  }

  public List<FontGlyph> getGlyphsForText(String text) {
    List<FontGlyph> glyphs = new ArrayList<>(text.length());
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (!atlas.containsKey(text.charAt(i)))
        throw new IllegalArgumentException("Font atlas does not contain character " + c);
      glyphs.add(atlas.get(c));
    }
    return glyphs;
  }

  public void test() {
    for (Character ch : atlas.keySet()) {
      System.out.println(ch + " " + atlas.get(ch));
    }
  }

  public int getScaleW() {
    return scaleW;
  }

  public int getScaleH() {
    return scaleH;
  }

    public Texture getFontTexture() {
        return fontTexture;
    }

    public void setFontTexture(Texture fontTexture) {
        this.fontTexture = fontTexture;
    }

    public int getBase() {
        return base;
    }
}
