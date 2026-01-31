package pl.karatesan.engine.sound;

import org.joml.Vector2f;
import pl.karatesan.engine.gameObjects.weapons.ArmorType;
import pl.karatesan.engine.gameObjects.weapons.WeaponType;

import java.util.Map;

public class SoundManager {

  private AudioEngine audioEngine;
  private Map<String, SoundEffect> soundEffects;

  public SoundManager(AudioEngine audioEngine, Map<String, SoundEffect> sounds) {
    this.audioEngine = audioEngine;
    this.soundEffects = sounds;
  }

  public void setListenerData(float x, float y) {
    audioEngine.setListenerData(x, y);
  }

  public SoundManager playShotSound(WeaponType weaponType, Vector2f position) {
    String soundId = weaponType.getWeaponString() + "Shot";
    audioEngine.playSound(soundEffects.get(soundId), position);
    audioEngine.playSound(soundEffects.get("caseFall"), position);
    return this;
  }

  public SoundManager playBulletHitSound(ArmorType armorType, Vector2f position) {
    String soundId = "";

    switch (armorType) {
      case ArmorType.FLESH -> soundId = "bulletHitFlesh";
      case ArmorType.METAL -> soundId = "bulletHitMetal";
    }
    audioEngine.playSound(soundEffects.get(soundId), position);
    return this;
  }

  public SoundManager playGruntAfterHitSound(Vector2f position) {
    audioEngine.playSound(soundEffects.get("hitGrunt"), position);
    return this;
  }

  public void playHealthPackSound(Vector2f position) {
    audioEngine.playSound(soundEffects.get("appleEat"), position);
  }

  // todo diffrent steps depending on armor?
  public SoundManager playFootstepSound(Vector2f position) {
    audioEngine.playSound(soundEffects.get("footstep"), position);
    return this;
  }

  // currently only theme
  public void playMusic(Vector2f position) {
    audioEngine.playSound(soundEffects.get("theme"), position);
  }
}
