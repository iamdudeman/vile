package technology.sola.engine.game.render.gui;

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
import technology.sola.engine.graphics.gui.style.property.Visibility;
import technology.sola.engine.graphics.gui.style.theme.DefaultThemeBuilder;
import technology.sola.engine.graphics.gui.style.theme.GuiTheme;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EventBoardGuiBuilder {
  private static boolean hasSeenFlavorText = false;
  private boolean isEventsVisible = true;
  private final GuiDocument guiDocument;
  private final GuiTheme guiTheme = DefaultThemeBuilder.buildDarkTheme()
    .addStyle(ButtonGuiElement.class, List.of(ConditionalStyle.always(
      BaseStyles.create()
        .setPadding(8)
        .build()
    )));
  private final ConditionalStyle<TextStyles> visibilityNoneTextStyle = ConditionalStyle.always(
    TextStyles.create().setVisibility(Visibility.NONE).build()
  );
  private final ConditionalStyle<TextStyles> visibilityHiddenTextStyle = ConditionalStyle.always(
    TextStyles.create().setVisibility(Visibility.HIDDEN).build()
  );
  private final ConditionalStyle<BaseStyles> visibilityHiddenStyle = ConditionalStyle.always(
    BaseStyles.create().setVisibility(Visibility.NONE).build()
  );

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

    rootSection.appendChildren(
      elementEventsSection(eventBoard),
      elementEventText()
    );

    rootSection.addStyle(ConditionalStyle.always(
      BaseStyles.create()
        .setCrossAxisChildren(CrossAxisChildren.CENTER)
        .setMainAxisChildren(MainAxisChildren.CENTER)
        .setGap(12)
        .setDirection(Direction.COLUMN)
        .setHeight("100%")
        .setWidth("100%")
        .build()
    ));

    guiTheme.applyToTree(rootSection);

    return rootSection;
  }

  private GuiElement<?, ?> elementEventText() {
    return new TextGuiElement()
      .setText("")
      .setId("eventText")
      .addStyle(ConditionalStyle.always(
        TextStyles.create()
          .setBorderColor(Color.WHITE)
          .setPadding(8)
          .setWidth("70%")
          .setHeight(200)
          .build()
      ))
      .addStyle(visibilityNoneTextStyle);
  }

  private GuiElement<?, ?> elementEventsSection(EventBoard eventBoard) {
    SectionGuiElement eventsSection = new SectionGuiElement();

    eventsSection.appendChildren(
      Arrays.stream(eventBoard.getNextEvents())
        .map(event -> elementEvent(event, eventBoard))
        .toArray(GuiElement<?, ?>[]::new)
    );

    eventsSection.addStyle(ConditionalStyle.always(
      BaseStyles.create()
        .setCrossAxisChildren(CrossAxisChildren.CENTER)
        .setMainAxisChildren(MainAxisChildren.CENTER)
        .setGap(12)
        .setDirection(Direction.ROW)
        .build()
    ));

    var flavorText = new TextGuiElement()
      .setText("Acquire Knowledge or Modify the vials for your next round until it starts! The longer you prepare however, the longer your opponent also has to prepare.")
      .addStyle(
        ConditionalStyle.always(
          TextStyles.create()
            .setPaddingBottom(24)
            .setWidth("80%")
            .build()
        )
      );

    if (hasSeenFlavorText) {
      flavorText.addStyle(visibilityHiddenTextStyle);
    }

    return new SectionGuiElement()
      .setId("eventsSection")
      .appendChildren(
        flavorText,
        eventsSection
      )
      .addStyle(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.COLUMN)
          .setCrossAxisChildren(CrossAxisChildren.CENTER)
          .setGap(16)
          .build()
      ));
  }

  private GuiElement<?, ?> elementEvent(EventBoard.Event<?> event, EventBoard eventBoard) {
    return new ButtonGuiElement()
      .setOnAction(() -> {
        if (!isEventsVisible) {
          return;
        }

        hasSeenFlavorText = true;
        isEventsVisible = false;

        VialsBoard vialsBoard = null;
        String eventText = "";

        if (event instanceof EventBoard.BattleEvent battleEvent) {
          vialsBoard = battleEvent.payload().get();
          eventText = vialsBoard.ai.getGreeting(vialsBoard);
        } else if (event instanceof EventBoard.ModificationEvent modificationEvent) {
          eventText = modificationEvent.payload().get();
        }


        var eventsSection = guiDocument.findElementById("eventsSection", SectionGuiElement.class);
        var eventTextElement = guiDocument.findElementById("eventText", TextGuiElement.class);

        eventsSection
            .styles()
            .addStyle(visibilityHiddenStyle);

        eventTextElement
          .setText(eventText)
          .styles()
          .removeStyle(visibilityNoneTextStyle);

        Timer timer = new Timer();

        timer.schedule(buildTimerTask(timer, eventBoard, vialsBoard, eventTextElement), 4000);
      })
      .appendChildren(
        new TextGuiElement().setText(event.title())
      );
  }

  private TimerTask buildTimerTask(Timer timer, EventBoard eventBoard, VialsBoard vialsBoard, TextGuiElement eventTextElement) {
    return new TimerTask() {
      @Override
      public void run() {
        if (vialsBoard == null) {
          guiDocument.setRootElement(
            new EventBoardGuiBuilder(guiDocument)
              .build(new EventBoard(eventBoard))
          );

          eventTextElement.styles().addStyle(visibilityNoneTextStyle);
        } else {
          vialsBoard.playerKnowledge.reset();
          guiDocument.setRootElement(
            new VialsBoardGuiBuilder(guiDocument)
              .build(vialsBoard)
          );
        }

        cancel();
        timer.cancel();
      }
    };
  }
}
