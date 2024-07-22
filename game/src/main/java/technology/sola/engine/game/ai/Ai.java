package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.VialsBoard;

import java.util.Random;

public abstract class Ai {
  protected final Random random = new Random();
  protected final Knowledge knowledge;
  protected Integer currentBrew = null;
  protected boolean isDone = false;

  protected Ai(Knowledge knowledge) {
    this.knowledge = knowledge;
  }

  public abstract String getName();

  public abstract String getGreeting(VialsBoard vialsBoard);

  public abstract String getStartTurnText(VialsBoard vialsBoard);

  public abstract String nextAction(VialsBoard vialsBoard);

  public Knowledge getKnowledge() {
    return knowledge;
  }

  public String startTurn(VialsBoard vialsBoard) {
    isDone = false;
    currentBrew = null;

    return getStartTurnText(vialsBoard);
  }

  public boolean isDone() {
    return isDone;
  }
}
