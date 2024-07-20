package technology.sola.engine.game.render.gui;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.VialsBoard;
import technology.sola.engine.graphics.gui.GuiDocument;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.elements.SectionGuiElement;

public class EventBoardGuiBuilder {
  private final GuiDocument guiDocument;

  public EventBoardGuiBuilder(GuiDocument guiDocument) {
    this.guiDocument = guiDocument;
  }

  public GuiElement<?> build(Knowledge playerKnowledge) {

    // todo
    return new SectionGuiElement();
  }
}
