package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.VialsBoard;

import java.util.Random;

public abstract class Ai {
  protected Integer currentRoll = null;
  protected Random random = new Random();
  protected boolean isDone = false;
  protected Knowledge knowledge;

  public abstract String getName();

  public abstract String getStartTurnText(VialsBoard vialsBoard);

  public abstract String nextAction(VialsBoard vialsBoard);

  public Knowledge getKnowledge() {
    return knowledge;
  }

  public String startTurn(VialsBoard vialsBoard) {
    isDone = false;
    currentRoll = null;

    return getStartTurnText(vialsBoard);
  }

  public boolean isDone() {
    return isDone;
  }
}
