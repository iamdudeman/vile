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

import java.util.List;

public class VialsBoardGuiBuilder {
  private GuiTheme guiTheme = DefaultThemeBuilder.buildDarkTheme();
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
    return new SectionGuiElement();
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
