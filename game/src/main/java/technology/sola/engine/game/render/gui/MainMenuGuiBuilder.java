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

  public GuiElement<?> build() {
    var titleImage = new ImageGuiElement();

    titleImage.setAssetId(AssetIds.Images.TITLE);

    titleImage.setStyle(List.of(ConditionalStyle.always(
      BaseStyles.create()
        .setWidth(312)
        .setHeight(266)
        .build()
    )));

    var rootSection = new SectionGuiElement()
      .appendChildren(
        titleImage,
        new ButtonGuiElement()
          .setOnAction(() -> {
            guiDocument.setRootElement(
              new VialsBoardGuiBuilder(guiDocument)
                .build(new VialsBoard(
                  new Knowledge(),
                  new TutorialAi(),
                  2,
                  3
                ))
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
      .setStyle(List.of(ConditionalStyle.always(
        BaseStyles.create()
          .setMainAxisChildren(MainAxisChildren.CENTER)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setDirection(Direction.COLUMN)
          .setWidth("100%")
          .setHeight("100%")
          .setGap(10)
          .build()
      )));

    guiTheme.applyToTree(rootSection);

    return rootSection;
  }

  private GuiElement<?> buildOptions() {
    var rootSection = new SectionGuiElement()
      .appendChildren(
        new TextGuiElement()
          .setText("Options"),
        new TextGuiElement()
          .setText("AI Speed"),
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
      .setStyle(List.of(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.COLUMN)
          .setGap(16)
          .setMainAxisChildren(MainAxisChildren.CENTER)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setHeight("100%")
          .setWidth("100%")
          .build()
      )));

    guiTheme.applyToTree(rootSection);

    rootSection.findElementById("speed" + GameSettings.AI_SPEED, ButtonGuiElement.class)
      .styles()
      .addStyle(highlightedStyle);
    rootSection.findElementById("volume" + GameSettings.VOLUME, ButtonGuiElement.class)
      .styles()
      .addStyle(highlightedStyle);

    return rootSection;
  }

  private GuiElement<?> elementAiSpeed() {
    return new SectionGuiElement()
      .appendChildren(
        createAiSpeedButton("Slow", 1),
        createAiSpeedButton("Normal", 2),
        createAiSpeedButton("Fast", 3)
      )
      .setStyle(List.of(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.ROW)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setGap(12)
          .build()
      )));
  }

  private GuiElement<?> createAiSpeedButton(String label, int aiSpeed) {
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

  private GuiElement<?> elementVolume() {
    return new SectionGuiElement()
      .appendChildren(
        createVolumeButton("Lowest", 1),
        createVolumeButton("Low", 2),
        createVolumeButton("Normal", 3),
        createVolumeButton("High", 4),
        createVolumeButton("Highest", 5)
      )
      .setStyle(List.of(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.ROW)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setGap(12)
          .build()
      )));
  }

  private GuiElement<?> createVolumeButton(String label, int volume) {
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
