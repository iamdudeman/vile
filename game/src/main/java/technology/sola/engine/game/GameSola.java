package technology.sola.engine.game;

import technology.sola.ecs.World;
import technology.sola.engine.assets.BulkAssetLoader;
import technology.sola.engine.assets.input.ControlsConfig;
import technology.sola.engine.core.SolaConfiguration;
import technology.sola.engine.defaults.SolaWithDefaults;
import technology.sola.engine.game.render.LoadingScreen;
import technology.sola.engine.graphics.gui.style.theme.DefaultThemeBuilder;
import technology.sola.engine.graphics.renderer.Renderer;
import technology.sola.engine.graphics.screen.AspectMode;

public class GameSola extends SolaWithDefaults {
  private boolean isLoading = true;
  private LoadingScreen loadingScreen = new LoadingScreen();

  public GameSola() {
    super(SolaConfiguration.build("Game", 800, 600).withTargetUpdatesPerSecond(30));
  }

  @Override
  protected void onInit(DefaultsConfigurator defaultsConfigurator) {
    defaultsConfigurator.useGraphics().useGui(DefaultThemeBuilder.buildLightTheme());

    platform.getViewport().setAspectMode(AspectMode.MAINTAIN);
  }

  @Override
  protected void onAsyncInit(Runnable completeAsyncInit) {
    new BulkAssetLoader(assetLoaderProvider)
      .addAsset(ControlsConfig.class, AssetIds.Controls.PLAYER, "assets/input/player.controls.json")
      .loadAll()
      .onComplete(assets -> {
        if (assets[0] instanceof ControlsConfig controlsConfig) {
          solaControls.addControls(controlsConfig);
        }

        // finish async load
        solaEcs.setWorld(buildWorld());
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

  private World buildWorld() {
    World world = new World(100);

    return world;
  }
}
