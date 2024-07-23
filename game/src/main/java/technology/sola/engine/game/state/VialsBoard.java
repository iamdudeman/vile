package technology.sola.engine.game.state;

import technology.sola.engine.game.ai.Ai;

import java.util.Random;

public class VialsBoard {
  public final Knowledge playerKnowledge;
  public final Ai ai;
  private static final int INITIAL_LIVES = 2;
  private static final int MAX_PH = 14;
  private final Random random = new Random();
  private final Vial[] playerVials;
  private final Vial[] opponentVials;
  private final int vialDepth;
  private boolean isPlayerTurn = true;
  private int lives;

  public VialsBoard(Knowledge playerKnowledge, Ai ai) {
    this(playerKnowledge, ai, 3, 5);
  }

  public VialsBoard(Knowledge playerKnowledge, Ai ai, int vialCount, int vialDepth) {
    this.playerKnowledge = playerKnowledge;
    this.ai = ai;
    this.playerVials = new Vial[vialCount];
    this.opponentVials = new Vial[vialCount];
    this.vialDepth = vialDepth;

    lives = INITIAL_LIVES + playerKnowledge.getExtraLives();

    for (int i = 0; i < playerVials.length; i++) {
      playerVials[i] = new Vial(vialDepth);
    }
    for (int i = 0; i < opponentVials.length; i++) {
      opponentVials[i] = new Vial(vialDepth);
    }
  }

  public void reset() {
    for (int i = 0; i < playerVials.length; i++) {
      playerVials[i] = new Vial(vialDepth);
    }
    for (int i = 0; i < opponentVials.length; i++) {
      opponentVials[i] = new Vial(vialDepth);
    }

    playerKnowledge.reset();
    ai.getKnowledge().reset();
    isPlayerTurn = true;
  }

  public int brewNextPh() {
    Knowledge knowledge = isPlayerTurn ? playerKnowledge : ai.getKnowledge();

    float standardDeviation = switch (knowledge.getInstability()) {
      case 1 -> 2f;
      case 2 -> 3f;
      case 3 -> 3.5f;
      case 4 -> 4f;
      default -> 2;
    };
    int value = (int) Math.round(random.nextGaussian(7, standardDeviation));

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

  public int getLives() {
    return lives;
  }

  public void reduceLives() {
    lives--;
  }

  public boolean isGameDone() {
    return playerKnowledge.getCurrentHealth() <= 0 || ai.getKnowledge().getCurrentHealth() <= 0;
  }

  public boolean isPlayerWin() {
    return ai.getKnowledge().getCurrentHealth() <= 0;
  }
}
