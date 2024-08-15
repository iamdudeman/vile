package technology.sola.engine.game;

import technology.sola.engine.assets.BulkAssetLoader;
import technology.sola.engine.assets.audio.AudioClip;
import technology.sola.engine.assets.graphics.SolaImage;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.defaults.SolaWithDefaults;
import technology.sola.engine.game.audio.AudioPlayer;
import technology.sola.engine.game.render.LoadingScreen;
import technology.sola.engine.game.render.gui.MainMenuGuiBuilder;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;

public class GameSola extends SolaWithDefaults {
  private boolean isLoading = true;
  private LoadingScreen loadingScreen = new LoadingScreen();

  public GameSola() {
    super(new SolaConfiguration("Vile", 1080, 720, 20));
  }

  @Override
  protected void onInit(DefaultsConfigurator defaultsConfigurator) {
    defaultsConfigurator.useGraphics().useGui();

    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);
  }

  @Override
  protected void onAsyncInit(Runnable completeAsyncInit) {
    new BulkAssetLoader(assetLoaderProvider)
      .addAsset(SolaImage.class, AssetIds.Images.PLAYER, "assets/images/wizzarrrrdcat.png")
      .addAsset(SolaImage.class, AssetIds.Images.WARLOCAT, "assets/images/evilwarloccat.png")
      .addAsset(SolaImage.class, AssetIds.Images.DUCKY, "assets/images/alchemistducky.png")
      .addAsset(SolaImage.class, AssetIds.Images.DEF_NOT_A_CAT, "assets/images/defnotacat.png")
      .addAsset(SolaImage.class, AssetIds.Images.TITLE, "assets/images/viletitle.png")
      .addAsset(AudioClip.class, AssetIds.Audio.DRIPPING, "assets/audio/Dripping_MainMenu.wav")
      .addAsset(AudioClip.class, AssetIds.Audio.UNSTABLE, "assets/audio/Unstable_BattleMusic.wav")
      .loadAll()
      .onComplete(assets -> {
        guiDocument.setRootElement(
          new MainMenuGuiBuilder(guiDocument).build()
        );

        if (assets[5] instanceof AudioClip mainMenuAudioClip) {
          if (assets[6] instanceof AudioClip battleAudioClip) {
            AudioPlayer.initialize(mainMenuAudioClip, battleAudioClip);

            AudioPlayer.setVolume(0.5f);
            AudioPlayer.playMainMenu();
          }
        }

        // finish async load
        isLoading = false;
        loadingScreen = null;
        completeAsyncInit.run();
      });
  }

  @Override
  protected void onRender(Renderer renderer) {
    if (isLoading) {
      loadingScreen.drawLoading(renderer);
    } else {
      super.onRender(renderer);
    }
  }
}
