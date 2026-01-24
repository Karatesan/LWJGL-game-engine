package pl.karatesan.engine.gameObjects.weapons;

public enum WeaponType {
  SHOTGUN,
  ASSAULT_RIFLE,
  PISTOL,
  RIFLE;

  public String getWeaponString() {
    return switch (this) {
      case SHOTGUN -> "shotgun";
      case ASSAULT_RIFLE -> "assaultRifle";
      case PISTOL -> "pistol";
      case RIFLE -> "rifle";
    };
  }
}
