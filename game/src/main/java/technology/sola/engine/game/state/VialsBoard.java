package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;

public class VialsBoard {
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

  public Vial[] getPlayerVials() {
    return playerVials;
  }

  public Vial[] getOpponentVials() {
    return opponentVials;
  }
}
