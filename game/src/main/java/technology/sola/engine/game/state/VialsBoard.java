package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;
import technology.sola.engine.game.ai.Ai;

import java.util.Random;

public class VialsBoard {
  public final Knowledge playerKnowledge;
  public final Ai ai;
  private static final int NEUTRAL_PH = 7;
  private static final int MAX_PH = 14;
  private final Random random = new Random();
  private final Vial[] playerVials;
  private final Vial[] opponentVials;
  private boolean isPlayerTurn = true;
  private int lives;

  public VialsBoard(Knowledge playerKnowledge, Ai ai) {
    this.playerKnowledge = playerKnowledge;
    this.ai = ai;
    this.playerVials = new Vial[GameBalanceConfiguration.VIAL_COUNT];
    this.opponentVials = new Vial[GameBalanceConfiguration.VIAL_COUNT];

    lives = GameBalanceConfiguration.INITIAL_LIVES + playerKnowledge.getExtraLives();

    for (int i = 0; i < playerVials.length; i++) {
      playerVials[i] = new Vial();
    }
    for (int i = 0; i < opponentVials.length; i++) {
      opponentVials[i] = new Vial();
    }
  }

  public void reset() {
    for (int i = 0; i < playerVials.length; i++) {
      playerVials[i] = new Vial();
    }
    for (int i = 0; i < opponentVials.length; i++) {
      opponentVials[i] = new Vial();
    }

    playerKnowledge.reset();
    ai.getKnowledge().reset();
  }

  public int rollNextPh() {
    int value = (int) Math.round(random.nextGaussian(7, 2));

    if (value > MAX_PH) {
      return MAX_PH;
    } else if (value < 0) {
      return 0;
    }

    return value;
  }

  public boolean isPlayerTurn() {
    return isPlayerTurn;
  }

  public void endTurn() {
    isPlayerTurn = !isPlayerTurn;
  }

  public Vial[] getPlayerVials() {
    return playerVials;
  }

  public Vial[] getOpponentVials() {
    return opponentVials;
  }

  public float getPlayerScore() {
    return calculateScore(this.playerVials);
  }

  public float getOpponentScore() {
    return calculateScore(this.opponentVials);
  }

  public int getLives() {
    return lives;
  }

  public void reduceLives() {
    lives--;
  }

  public boolean isPlayerWin() {
    float playerNeutralDiff = Math.abs(NEUTRAL_PH - getPlayerScore());
    float opponentNeutralDiff = Math.abs(NEUTRAL_PH - getOpponentScore());

    return playerNeutralDiff <= opponentNeutralDiff;
  }

  public boolean isBoardFull() {
    for (var vial : getPlayerVials()) {
      if (!vial.isFull()) {
        return false;
      }
    }

    for (var vial : getOpponentVials()) {
      if (!vial.isFull()) {
        return false;
      }
    }

    return true;
  }

  private float calculateScore(Vial[] vials) {
    float score = 0;

    for (var vial : vials) {
      score += vial.getScore();
    }

    score /= vials.length;

    return score;
  }
}
