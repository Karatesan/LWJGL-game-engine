package pl.karatesan.engine.texture;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
  private Map<String, Texture> textures;

  public TextureManager() {

      this.textures = new HashMap<>();
      String path = "/Blood.png";
      Texture texture = new Texture(path, Texture.CLAMP);
      textures.put(path,texture);
  }

  public Texture load(String path) {
    if (textures.containsKey(path)) return textures.get(path);
    Texture texture = new Texture(path, Texture.CLAMP);
    textures.put(path, texture);
    return texture;
  }

  public Texture loadGround(String path) {
    if (textures.containsKey(path)) return textures.get(path);
    Texture texture = new Texture(path, Texture.REPEAT);
    textures.put(path, texture);
    return texture;
  }

  public void cleanup() {
    for (Texture tex : textures.values()) {
      tex.cleanup();
    }
    textures.clear();
  }
}
