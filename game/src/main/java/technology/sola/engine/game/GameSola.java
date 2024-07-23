package technology.sola.engine.game;

import technology.sola.engine.assets.BulkAssetLoader;
import technology.sola.engine.assets.graphics.SolaImage;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.defaults.SolaWithDefaults;
import technology.sola.engine.game.render.LoadingScreen;
import technology.sola.engine.game.render.gui.MainMenuGuiBuilder;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;

public class GameSola extends SolaWithDefaults {
  private boolean isLoading = true;
  private LoadingScreen loadingScreen = new LoadingScreen();

  public GameSola() {
    super(SolaConfiguration.build("Vile", 1000, 800).withTargetUpdatesPerSecond(20));
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
      .loadAll()
      .onComplete(assets -> {
        guiDocument.setRootElement(
          new MainMenuGuiBuilder(guiDocument).build()
        );

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
