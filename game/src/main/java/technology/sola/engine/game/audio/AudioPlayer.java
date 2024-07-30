package technology.sola.engine.game.audio;

import technology.sola.engine.assets.audio.AudioClip;

public class AudioPlayer {
  private static AudioClip mainMenu;
  private static AudioClip battle;

  public static void initialize(AudioClip mainMenu, AudioClip battle) {
    AudioPlayer.mainMenu = mainMenu;
    AudioPlayer.battle = battle;
  }

  public static void setVolume(float volume) {
    mainMenu.setVolume(volume);
    battle.setVolume(volume);
  }

  public static void playMainMenu() {
    battle.stop();
    mainMenu.loop();
  }

  public static void playBattle() {
    mainMenu.stop();
    battle.loop();
  }
}
