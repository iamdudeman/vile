package technology.sola.engine.game.render.gui;

import technology.sola.engine.game.AssetIds;
import technology.sola.engine.game.GameSettings;
import technology.sola.engine.game.ai.TutorialAi;
import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.VialsBoard;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiDocument;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.elements.ImageGuiElement;
import technology.sola.engine.graphics.gui.elements.SectionGuiElement;
import technology.sola.engine.graphics.gui.elements.TextGuiElement;
import technology.sola.engine.graphics.gui.elements.TextStyles;
import technology.sola.engine.graphics.gui.elements.input.ButtonGuiElement;
import technology.sola.engine.graphics.gui.style.BaseStyles;
import technology.sola.engine.graphics.gui.style.ConditionalStyle;
import technology.sola.engine.graphics.gui.style.property.CrossAxisChildren;
import technology.sola.engine.graphics.gui.style.property.Direction;
import technology.sola.engine.graphics.gui.style.property.MainAxisChildren;
import technology.sola.engine.graphics.gui.style.theme.DefaultThemeBuilder;
import technology.sola.engine.graphics.gui.style.theme.GuiTheme;

import java.util.List;

public class MainMenuGuiBuilder {
  private final GuiDocument guiDocument;
  private final GuiTheme guiTheme = DefaultThemeBuilder.buildDarkTheme()
    .addStyle(TextGuiElement.class, List.of(ConditionalStyle.always(
      TextStyles.create()
        .setFontAssetId(AssetIds.Font.MONO_18)
        .build()
    )))
    .addStyle(ButtonGuiElement.class, List.of(ConditionalStyle.always(
      BaseStyles.create()
        .setPadding(8)
        .setCrossAxisChildren(CrossAxisChildren.CENTER)
        .setWidth(100)
        .build()
    )));
  private final ConditionalStyle<BaseStyles> highlightedStyle = ConditionalStyle.always(
    BaseStyles.create()
      .setBackgroundColor(new Color(195, 177, 104))
      .build()
  );

  public MainMenuGuiBuilder(GuiDocument guiDocument) {
    this.guiDocument = guiDocument;
  }

  public GuiElement<?, ?> build() {
    var titleImage = new ImageGuiElement();

    titleImage.setAssetId(AssetIds.Images.TITLE);

    titleImage.addStyle(ConditionalStyle.always(
      BaseStyles.create()
        .setWidth(575)
        .setHeight(575)
        .build()
    ));

    var rootSection = new SectionGuiElement()
      .appendChildren(
        titleImage,
        new ButtonGuiElement()
          .setOnAction(() -> {
            guiDocument.setRootElement(
              buildLoreIntro()
            );
          })
          .appendChildren(
            new TextGuiElement()
              .setText("Start")
          ),
        new ButtonGuiElement()
          .setOnAction(() -> {
            guiDocument.setRootElement(
              buildOptions()
            );
          })
          .appendChildren(
            new TextGuiElement()
              .setText("Options")
          )
      )
      .addStyle(ConditionalStyle.always(
        BaseStyles.create()
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setDirection(Direction.COLUMN)
          .setWidth("100%")
          .setHeight("100%")
          .setGap(10)
          .build()
      ));

    guiTheme.applyToTree(rootSection);

    return rootSection;
  }

  private GuiElement<?, ?> buildLoreIntro() {
    TextGuiElement textGuiElement = new TextGuiElement();

    textGuiElement.setText(
      """
        The underground world of alchemy is a lucrative well of knowledge. But in this world of equivalent exchanges, such an incredible opportunity for knowledge doesn't come cheap.

      The game of Vials is our scale.

        Alchemists from all over the world compete in the game of Vials with their lives as wager. Win and your journey to discover wonderful secrets, such as transmuting lead into gold, continues. Lose and you'll come face-to-face with the ultimacy of your mortality.

        Your journey is just beginning so take this time to learn the basics of how a game of Vials is played. Between each round you will have chances to gain additional "Knowledge" to help you obtain victory over opposing alchemists. You may even attempt to make "Modifications" to gain an advantage in your next game. The longer you prepare however, the longer your opponent also has to prepare.

        Now get out there and don't get yourself killed!

      Press any key or click to begin tutorial.
      """
    );

    DefaultThemeBuilder.buildDarkTheme().applyToElement(textGuiElement);

    textGuiElement.addStyle(ConditionalStyle.always(
      TextStyles.create()
        .setPadding(16)
        .setWidth("60%")
        .setHeight("80%")
        .setFontAssetId(AssetIds.Font.MONO_18)
        .setBorderColor(Color.WHITE)
        .build()
    ));

    Runnable onAction = () -> {
      guiDocument.setRootElement(
        new VialsBoardGuiBuilder(guiDocument)
          .build(new VialsBoard(
            new Knowledge(),
            new TutorialAi(),
            2,
            3
          ))
      );
    };


    ButtonGuiElement buttonGuiElement = new ButtonGuiElement()
      .setOnAction(onAction)
      .appendChildren(
        textGuiElement
      )
      .addStyle(ConditionalStyle.always(
        BaseStyles.create()
          .setWidth("100%")
          .setHeight("100%")
          .setMainAxisChildren(MainAxisChildren.CENTER)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .build()
      ));

    buttonGuiElement.events().keyPressed().on(event -> {
      onAction.run();
    });
    buttonGuiElement.events().mousePressed().on(event -> {
      onAction.run();
    });

    return buttonGuiElement;
  }

  private GuiElement<?, ?> buildOptions() {
    var rootSection = new SectionGuiElement()
      .appendChildren(
        new TextGuiElement()
          .setText("Options"),
        new TextGuiElement()
          .setText("AI decision speed"),
        elementAiSpeed(),
        new TextGuiElement()
          .setText("Volume"),
        elementVolume(),
        new ButtonGuiElement()
          .setOnAction(() -> {
            guiDocument.setRootElement(
              build()
            );
          })
          .appendChildren(
            new TextGuiElement()
              .setText("Done")
          )
      )
      .addStyle(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.COLUMN)
          .setGap(16)
          .setMainAxisChildren(MainAxisChildren.CENTER)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setHeight("100%")
          .setWidth("100%")
          .build()
      ));

    guiTheme.applyToTree(rootSection);

    rootSection.findElementById("speed" + GameSettings.AI_SPEED, ButtonGuiElement.class)
      .styles()
      .addStyle(highlightedStyle);
    rootSection.findElementById("volume" + GameSettings.VOLUME, ButtonGuiElement.class)
      .styles()
      .addStyle(highlightedStyle);

    return rootSection;
  }

  private GuiElement<?, ?> elementAiSpeed() {
    return new SectionGuiElement()
      .appendChildren(
        createAiSpeedButton("Slow", 1),
        createAiSpeedButton("Normal", 2),
        createAiSpeedButton("Fast", 3)
      )
      .addStyle(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.ROW)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setGap(12)
          .build()
      ));
  }

  private GuiElement<?, ?> createAiSpeedButton(String label, int aiSpeed) {
    ButtonGuiElement buttonGuiElement = new ButtonGuiElement()
      .setOnAction(() -> {
        int currentSpeed = GameSettings.AI_SPEED;

        GameSettings.AI_SPEED = aiSpeed;

        guiDocument.findElementById("speed" + currentSpeed, ButtonGuiElement.class)
          .styles()
          .removeStyle(highlightedStyle);
        guiDocument.findElementById("speed" + GameSettings.AI_SPEED, ButtonGuiElement.class)
          .styles()
          .addStyle(highlightedStyle);
      });

    buttonGuiElement.setId("speed" + aiSpeed)
      .appendChildren(
        new TextGuiElement()
          .setText(label)
      );

    return buttonGuiElement;
  }

  private GuiElement<?, ?> elementVolume() {
    return new SectionGuiElement()
      .appendChildren(
        createVolumeButton("Lowest", 1),
        createVolumeButton("Low", 2),
        createVolumeButton("Normal", 3),
        createVolumeButton("High", 4),
        createVolumeButton("Highest", 5)
      )
      .addStyle(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.ROW)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setGap(12)
          .build()
      ));
  }

  private GuiElement<?, ?> createVolumeButton(String label, int volume) {
    ButtonGuiElement buttonGuiElement = new ButtonGuiElement()
      .setOnAction(() -> {
        int currentVolume = GameSettings.VOLUME;

        GameSettings.VOLUME = volume;

        guiDocument.findElementById("volume" + currentVolume, ButtonGuiElement.class)
          .styles()
          .removeStyle(highlightedStyle);
        guiDocument.findElementById("volume" + GameSettings.VOLUME, ButtonGuiElement.class)
          .styles()
          .addStyle(highlightedStyle);
      });

    buttonGuiElement.setId("volume" + volume)
      .appendChildren(
        new TextGuiElement()
          .setText(label)
      );

    return buttonGuiElement;
  }
}
