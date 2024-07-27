package technology.sola.engine.game.render.gui;

import technology.sola.engine.game.AssetIds;
import technology.sola.engine.game.state.EventBoard;
import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.VialsBoard;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiDocument;
import technology.sola.engine.graphics.gui.GuiElement;
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

import java.util.Arrays;
import java.util.List;

public class EventBoardGuiBuilder {
  private final GuiDocument guiDocument;
  private final GuiTheme guiTheme = DefaultThemeBuilder.buildDarkTheme().addStyle(TextGuiElement.class, List.of(ConditionalStyle.always(
    TextStyles.create()
      .setFontAssetId(AssetIds.Font.MONO_18)
      .build()
  )));

  public EventBoardGuiBuilder(GuiDocument guiDocument) {
    this.guiDocument = guiDocument;
  }

  public GuiElement<?, ?> build(Knowledge playerKnowledge) {
    EventBoard eventBoard = new EventBoard(playerKnowledge);

    return build(eventBoard);
  }

  public GuiElement<?, ?> build(EventBoard eventBoard) {
    var rootSection = new SectionGuiElement();

    rootSection.setId("rootSection");

    SectionGuiElement eventsSection = new SectionGuiElement();

    eventsSection.appendChildren(
      Arrays.stream(eventBoard.getNextEvents())
        .map(event -> elementEvent(event, eventBoard))
        .toArray(GuiElement<?, ?>[]::new)
    );

    eventsSection.addStyle(ConditionalStyle.always(
      BaseStyles.create()
        .setGap(12)
        .setDirection(Direction.ROW)
        .build()
    ));

    rootSection.appendChildren(
      eventsSection
    );

    rootSection.addStyle(ConditionalStyle.always(
      BaseStyles.create()
        .setCrossAxisChildren(CrossAxisChildren.CENTER)
        .setMainAxisChildren(MainAxisChildren.CENTER)
        .setHeight("100%")
        .setWidth("100%")
        .build()
    ));

    guiTheme.applyToTree(rootSection);

    return rootSection;
  }

  private GuiElement<?, ?> elementEvent(EventBoard.Event event, EventBoard eventBoard) {
    String title = event.title();
    String fullDescription = event.description();

    return new ButtonGuiElement()
      .addStyle(ConditionalStyle.always(
        BaseStyles.create()
          .setWidth(260)
          .setHeight(320)
          .build()
      ))
      .appendChildren(
        new SectionGuiElement()
          .appendChildren(
            new TextGuiElement()
              .setText(title)
              .addStyle(ConditionalStyle.always(
                TextStyles.create()
                  .setBackgroundColor(new Color(96, 96, 96))
                  .setBorderColor(Color.WHITE)
                  .setWidth("100%")
                  .setTextAlignment(TextStyles.TextAlignment.CENTER)
                  .setPadding(8)
                  .build()
              )),
            new TextGuiElement()
              .setText(fullDescription)
              .addStyle(ConditionalStyle.always(
                TextStyles.create()
                  .setPadding(8)
                  .build()
              ))
          )
          .addStyle(ConditionalStyle.always(
            BaseStyles.create()
              .setDirection(Direction.COLUMN)
              .setGap(8)
              .build()
          ))
      )
      .setOnAction(() -> {
        VialsBoard vialsBoard = null;

        if (event instanceof EventBoard.BattleEvent battleEvent) {
          vialsBoard = battleEvent.buildBoard().get();
        } else if (event instanceof EventBoard.ModificationEvent modificationEvent) {
          modificationEvent.apply().run();
        }

        if (vialsBoard == null) {
          guiDocument.setRootElement(
            new EventBoardGuiBuilder(guiDocument)
              .build(new EventBoard(eventBoard))
          );
        } else {
          vialsBoard.playerKnowledge.reset();
          guiDocument.setRootElement(
            new VialsBoardGuiBuilder(guiDocument)
              .build(vialsBoard)
          );
        }
      });
  }
}
