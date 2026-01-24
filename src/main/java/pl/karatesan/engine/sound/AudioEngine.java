package pl.karatesan.engine.sound;

import org.joml.Vector2f;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;

public class AudioEngine {

  private long device;
  private long context;
  private Map<String, Integer> soundBuffers = new HashMap<>();
  private Map<String, Integer> sources = new HashMap<>(); // For testing, simple 1-to-1 source
  private List<Integer> sourcePool = new ArrayList<>();
  private int maxSources = 32; // Standard limit

  public void init() {
    // 1. Open the default device
    String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
    device = alcOpenDevice(defaultDeviceName);

    // 2. Create attributes (optional, usually null is fine)
    int[] attributes = {0};
    // 3. Create the context
    context = alcCreateContext(device, attributes);
    alcMakeContextCurrent(context);

    // 4. Create capabilities (Essential for LWJGL to see OpenAL functions)
    ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
    ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
    initSources();
  }

  private void initSources() {
    for (int i = 0; i < maxSources; i++) {
      int sourceId = alGenSources();
      if (alGetError() == AL_NO_ERROR) {
        sourcePool.add(sourceId);
      }
    }
  }

  private ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
    ByteBuffer buffer;

    // Try to locate the resource
    try (InputStream source = AudioEngine.class.getResourceAsStream(resource)) {
      if (source == null) {
        throw new IOException("Resource not found: " + resource);
      }

      try (ReadableByteChannel rbc = Channels.newChannel(source)) {
        buffer = MemoryUtil.memAlloc(bufferSize);

        while (true) {
          int bytes = rbc.read(buffer);
          if (bytes == -1) break;
          if (buffer.remaining() == 0) {
            buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 2);
          }
        }
      }
    }
    buffer.flip();
    return buffer;
  }

  public void loadSound(String name, String path) {
    int bufferId = alGenBuffers();

    try (MemoryStack stack = MemoryStack.stackPush()) {
      // 1. Load file to ByteBuffer
      ByteBuffer vorbisData;
      try {
        vorbisData = ioResourceToByteBuffer(path, 32 * 1024);
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }

      // 2. Decode from Memory (not filename)
      IntBuffer channelsBuffer = stack.mallocInt(1);
      IntBuffer sampleRateBuffer = stack.mallocInt(1);

      ShortBuffer rawAudioBuffer =
          stb_vorbis_decode_memory(vorbisData, channelsBuffer, sampleRateBuffer);

      if (rawAudioBuffer == null) {
        System.err.println("Could not load sound: " + path);
        return;
      }

      int channels = channelsBuffer.get(0);
      int sampleRate = sampleRateBuffer.get(0);

      // Determine format (Mono/Stereo)
      int format = -1;
      if (channels == 1) format = AL_FORMAT_MONO16;
      else if (channels == 2) format = AL_FORMAT_STEREO16;

      // Send data to OpenAL
      alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

      MemoryUtil.memFree(vorbisData);
      MemoryUtil.memFree(rawAudioBuffer); // Free STB data

    } catch (Exception e) {
      System.out.println(e);
    }

    soundBuffers.put(name, bufferId);
  }

  private int getFreeSource() {
    for (int sourceId : sourcePool) {
      int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
      // If the source is not playing and not paused, it's free to use.
      if (state != AL_PLAYING && state != AL_PAUSED) {
        return sourceId;
      }
    }
    // All sources are busy.
    // Ideally log a warning: "Sound pool exhausted!"
    return -1;
  }
//TODO for looping alSourcei(sourceId, AL_LOOPING, AL_TRUE)
  public void playSound(SoundEffect soundEffect, Vector2f position) {
    Integer bufferId = soundBuffers.get(soundEffect.getBufferName());

    // 1. Find free source (from your pool logic)
    int sourceId = getFreeSource();
    if (sourceId == -1) return;

    // 2. Reset Source

    alSourcei(sourceId, AL_BUFFER, bufferId);
    alSourcei(sourceId, AL_LOOPING, AL_FALSE);
    alSourcef(sourceId, AL_GAIN, soundEffect.getVolume()); // Master Volume for this sound

    // 3. Pitch Variation (The "Cheater's" Humanizer)

    alSourcef(sourceId, AL_PITCH, soundEffect.calculatePitchVariance());

    // 4. Positioning & Attenuation
    if (position != null) {
      // World Sound (3D)
      alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_FALSE);
      alSource3f(sourceId, AL_POSITION, position.x, position.y, 0);

      // ATTENUATION CONFIG (Crucial for 2D games)
      // Distance where sound is at Max Volume (e.g., 300 pixels)
      alSourcef(sourceId, AL_REFERENCE_DISTANCE, 300.0f);
      // Distance where sound stops getting quieter (or max hearing range)
      alSourcef(sourceId, AL_MAX_DISTANCE, 1500.0f);
      // Inverse Distance Clamp is usually best for 2D
      alSourcef(sourceId, AL_ROLLOFF_FACTOR, 1.0f);
    } else {
      // UI Sound (Head-Relative / 2D)
      // Plays "inside the player's head" (e.g., Menu clicks, Level Up)
      alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
      alSource3f(sourceId, AL_POSITION, 0, 0, 0);
    }

    alSourcePlay(sourceId);
  }

  public void setListenerData(float x, float y) {
    alListener3f(AL_POSITION, x, y, 0);
    alListener3f(AL_VELOCITY, 0, 0, 0);
  }

  public void cleanup() {
    // Delete all buffers
    for (Integer bufferId : soundBuffers.values()) {
      alDeleteBuffers(bufferId);
    }
    alcDestroyContext(context);
    alcCloseDevice(device);
  }

  private record SoundData(Integer bufferId, VariationType variation) {}
}
