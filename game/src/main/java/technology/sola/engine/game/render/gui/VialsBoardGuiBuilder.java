package technology.sola.engine.game.render.gui;

import technology.sola.engine.game.state.Vial;
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
import technology.sola.engine.graphics.gui.style.property.Direction;
import technology.sola.engine.graphics.gui.style.theme.DefaultThemeBuilder;
import technology.sola.engine.graphics.gui.style.theme.GuiTheme;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class VialsBoardGuiBuilder {
  private final GuiTheme guiTheme = DefaultThemeBuilder.buildDarkTheme();
  private final GuiDocument guiDocument;
  private Integer currentRolledPH = null;

  public VialsBoardGuiBuilder(GuiDocument guiDocument) {
    this.guiDocument = guiDocument;
  }

  public GuiElement<?> build(VialsBoard vialsBoard) {
    currentRolledPH = null;

    var section = new SectionGuiElement()
      .appendChildren(
        elementSidePanel(vialsBoard),
        elementVials(vialsBoard)
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setDirection(Direction.ROW)
            .setGap(10)
            .setPadding(8)
            .setHeight("100%")
            .setWidth("100%")
            .build()
        )
      ));

    guiTheme.applyToTree(section);

    return section;
  }

  private GuiElement<?> elementVials(VialsBoard vialsBoard) {
    var playerVials = new SectionGuiElement()
      .appendChildren(
        Arrays.stream(vialsBoard.getPlayerVials()).map(this::elementVial).toArray(GuiElement[]::new)
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setDirection(Direction.ROW)
            .setHeight("100%")
            .build()
        )
      ));

    var opponentVials = new SectionGuiElement()
      .appendChildren(
        Arrays.stream(vialsBoard.getOpponentVials()).map(this::elementVial).toArray(GuiElement[]::new)
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setDirection(Direction.ROW)
            .setHeight("100%")
            .build()
        )
      ));

    return new SectionGuiElement()
      .appendChildren(
        playerVials,
        new SectionGuiElement()
          .setStyle(List.of(
            ConditionalStyle.always(
              BaseStyles
                .create()
                .setWidth(2)
                .setBackgroundColor(Color.WHITE)
                .setHeight("100%")
                .build()
            )
          )),
        opponentVials
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setDirection(Direction.ROW)
            .setHeight("100%")
            .build()
        )
      ));
  }

  private GuiElement<?> elementVial(Vial vial) {
    Supplier<GuiElement<?>[]> buildVialContents = () -> {
      var contentsLength = vial.getContents().length;
      var contentGuiItems = new GuiElement[contentsLength];

      for (int i = 0; i < contentsLength; i++) {
        Integer value = vial.getContents()[i];

        contentGuiItems[i] = new TextGuiElement()
          .setText(value == null ? "--" : String.valueOf(value))
          .setStyle(List.of(ConditionalStyle.always(
            TextStyles.create()
              .setPadding(4)
              .build()
          )));
      }

      return contentGuiItems;
    };

    var contentGuiItemsContainer = new SectionGuiElement()
      .appendChildren(buildVialContents.get())
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setPadding(4)
            .setBorderColor(Color.WHITE)
            .setWidth(30)
            .build()
        )
      ));

    return new SectionGuiElement()
      .appendChildren(
        new ButtonGuiElement()
          .setOnAction(() -> {



            // todo add liquid and update UI

          })
          .appendChildren(
            new TextGuiElement()
              .setText("Pour")
          ),
        contentGuiItemsContainer
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .build()
        )
      ))
    ;
  }

  private GuiElement<?> elementSidePanel(VialsBoard vialsBoard) {
    return new SectionGuiElement()
      .appendChildren(
        elementRollButton(vialsBoard),
        elementKnowledgeSection()
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setPadding(8)
            .setBorderColor(Color.WHITE)
            .build()
        )
      ));
  }

  private GuiElement<?> elementRollButton(VialsBoard vialsBoard) {
    return new SectionGuiElement()
      .appendChildren(
        new ButtonGuiElement()
          .setOnAction(() -> {
            int nextPh = vialsBoard.rollNextPh();

            setRolledPh(nextPh);
          })
          .setId("rollButton")
          .appendChildren(
            new TextGuiElement()
              .setText("Roll")
          ),
        new TextGuiElement()
          .setText("--")
          .setId("rollText")
      );
  }

  private GuiElement<?> elementKnowledgeSection() {
    return new SectionGuiElement()
      .appendChildren(
        new TextGuiElement().setText("Knowledge")
      );
  }

  private void setRolledPh(Integer newPh) {
    var rollButton = guiDocument.findElementById("rollButton", ButtonGuiElement.class);
    var rollText = guiDocument.findElementById("rollText", TextGuiElement.class);

    if (newPh == null) {
      rollText.setText("--");
      rollButton.setDisabled(false);
    } else {
      rollText.setText(String.valueOf(newPh));
      rollButton.setDisabled(true);
    }
    currentRolledPH = newPh;
  }
}
