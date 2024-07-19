package technology.sola.engine.game.render.gui;

import technology.sola.engine.game.state.Vial;
import technology.sola.engine.game.state.VialsBoard;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiDocument;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.elements.SectionGuiElement;
import technology.sola.engine.graphics.gui.elements.TextGuiElement;
import technology.sola.engine.graphics.gui.elements.input.ButtonGuiElement;
import technology.sola.engine.graphics.gui.style.BaseStyles;
import technology.sola.engine.graphics.gui.style.ConditionalStyle;
import technology.sola.engine.graphics.gui.style.property.Direction;
import technology.sola.engine.graphics.gui.style.theme.DefaultThemeBuilder;
import technology.sola.engine.graphics.gui.style.theme.GuiTheme;

import java.util.Arrays;
import java.util.List;

public class VialsBoardGuiBuilder {
  private final GuiTheme guiTheme = DefaultThemeBuilder.buildDarkTheme();
  private final GuiDocument guiDocument;

  public VialsBoardGuiBuilder(GuiDocument guiDocument) {
    this.guiDocument = guiDocument;
  }

  public GuiElement<?> buildVialsBoardGui(VialsBoard vialsBoard) {
    var section = new SectionGuiElement()
      .appendChildren(
        buildSidePanel(vialsBoard),
        buildVials(vialsBoard)
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

  private GuiElement<?> buildVials(VialsBoard vialsBoard) {
    var playerVials = new SectionGuiElement()
      .appendChildren(
        Arrays.stream(vialsBoard.getPlayerVials()).map(this::buildVial).toArray(GuiElement[]::new)
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
        Arrays.stream(vialsBoard.getOpponentVials()).map(this::buildVial).toArray(GuiElement[]::new)
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

  private GuiElement<?> buildVial(Vial vial) {
    return new ButtonGuiElement()
//      .appendChildren() // todo
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setPadding(4)
            .setBorderColor(Color.WHITE)
            .setWidth(30)
            .setHeight("100%")
            .build()
        )
      ));
  }

  private GuiElement<?> buildSidePanel(VialsBoard vialsBoard) {
    return new SectionGuiElement()
      .appendChildren(
        buildRollButton(vialsBoard),
        buildKnowledgeSection()
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

  private GuiElement<?> buildRollButton(VialsBoard vialsBoard) {
    return new SectionGuiElement()
      .appendChildren(
        new ButtonGuiElement()
          .setOnAction(() -> {
            var rollText = guiDocument.findElementById("roll", TextGuiElement.class);
            int nextPh = vialsBoard.rollNextPh();

            rollText.setText(String.valueOf(nextPh));
          })
          .appendChildren(
            new TextGuiElement()
              .setText("Roll")
          ),
        new TextGuiElement()
          .setText("--")
          .setId("roll")
      );
  }

  private GuiElement<?> buildKnowledgeSection() {
    return new SectionGuiElement()
      .appendChildren(
        new TextGuiElement().setText("Knowledge")
      );
  }
}
