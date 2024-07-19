package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;

import java.util.Random;

public class VialsBoard {
  private static final int NEUTRAL_PH = 7;
  private static final int MAX_PH = 14;
  private final Random random = new Random();
  private final Vial[] playerVials;
  private final Vial[] opponentVials;

  public VialsBoard() {
    this.playerVials = new Vial[GameBalanceConfiguration.VIAL_COUNT];
    this.opponentVials = new Vial[GameBalanceConfiguration.VIAL_COUNT];

    for (int i = 0; i < playerVials.length; i++) {
      playerVials[i] = new Vial();
    }
    for (int i = 0; i < opponentVials.length; i++) {
      opponentVials[i] = new Vial();
    }
  }

  public int rollNextPh() {
    return random.nextInt(MAX_PH + 1);
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
