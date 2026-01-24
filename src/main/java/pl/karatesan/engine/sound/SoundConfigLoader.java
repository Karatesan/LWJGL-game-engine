package pl.karatesan.engine.sound;

import com.google.gson.Gson;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.karatesan.engine.utils.RandomService;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoundConfigLoader {

  // Helper class to map the JSON structure
  private record SoundConfigData(
      List<String> paths, float volume, String variation, boolean looping) {}

  public static Map<String, SoundEffect> load(AudioEngine audioEngine, RandomService rng) {
    Map<String, SoundEffect> result = new HashMap<>();
    Gson gson = new Gson();

    // 1. Read JSON file
    String resourcePath = "/sounds/sound_config.json";
    try (Reader reader =
        new InputStreamReader(SoundConfigLoader.class.getResourceAsStream(resourcePath))) {

      // Define the type: Map<String, SoundConfigData>
      Type type = new TypeToken<Map<String, SoundConfigData>>() {}.getType();
      Map<String, SoundConfigData> configMap = gson.fromJson(reader, type);

      // 2. Process each entry
      for (Map.Entry<String, SoundConfigData> entry : configMap.entrySet()) {
        String effectKey = entry.getKey(); // e.g., "hitGrunt"
        SoundConfigData data = entry.getValue();

        List<String> bufferNames = new ArrayList<>();

        // 3. Load each file in the list into AudioEngine
        for (int i = 0; i < data.paths.size(); i++) {
          String path = data.paths.get(i);
          // Generate a unique internal name for the buffer (e.g., "hitGrunt_0")
          String internalBufferName = effectKey + "_" + i;

          // Load into hardware
          audioEngine.loadSound(internalBufferName, path);
          bufferNames.add(internalBufferName);
        }

        // 4. Create the SoundEffect object
        VariationType varType = VariationType.valueOf(data.variation);
        SoundEffect effect = new SoundEffect(bufferNames, data.volume, varType, rng);
        // If you added looping support to SoundEffect, set it here:
        // effect.setLooping(data.looping);

        result.put(effectKey, effect);
      }

    } catch (Exception e) {
      throw new RuntimeException("Failed to load sound config", e);
    }

    return result;
  }
}
