package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.VialsBoard;

public class AggressiveAi extends Ai {
  private int turnsDefending = 0;

  public AggressiveAi() {
    knowledge = new Knowledge();

    knowledge.addReroll();
    knowledge.addReroll();

    knowledge.addMaxHealth();
  }

  @Override
  public String getName() {
    // todo
    return "Aggressive AI";
  }

  @Override
  public String getGreeting(VialsBoard vialsBoard) {
    // todo
    return "I'm not aggressive, you're aggressive.";
  }

  @Override
  public String getStartTurnText(VialsBoard vialsBoard) {
    return "My turn!";
  }

  @Override
  public String nextAction(VialsBoard vialsBoard) {
    if (currentRoll == null) {
      currentRoll = vialsBoard.rollNextPh();

      return "I brewed " + currentRoll + ".";
    }

    if (currentRoll == 7) {
      vialsBoard.getOpponentVials()[random.nextInt(vialsBoard.getOpponentVials().length)]
        .addLiquidToTop(currentRoll);

      isDone = true;
      return "I poured my 7 on my board.";
    }

    for (var vial : vialsBoard.getPlayerVials()) {
      float value = vial.getCurrentAverage();

      if (value < 7 && currentRoll < 7) {
        vial.addLiquidToTop(currentRoll);

        turnsDefending = 0;
        isDone = true;
        return "I poured " + currentRoll + " on your acidic vial.";
      }

      if (value > 7 && currentRoll > 7) {
        vial.addLiquidToTop(currentRoll);

        turnsDefending = 0;
        isDone = true;
        return "I poured " + currentRoll + " on your basic vial.";
      }
    }

    for (var vial : vialsBoard.getPlayerVials()) {
      if (vial.isEmpty()) {
        vial.addLiquidToTop(currentRoll);

        isDone = true;
        turnsDefending = 0;
        return "I poured " + currentRoll + " in your empty vial.";
      }
    }

    if (knowledge.getCurrentRerolls() > 0) {
      knowledge.reroll();
      currentRoll = vialsBoard.rollNextPh();

      return "I can't attack well with this. I rebrewed " + currentRoll + ".";
    }

    if (turnsDefending < 3) {
      for (var vial : vialsBoard.getOpponentVials()) {
        float value = vial.getCurrentAverage();

        if (value > 7 && currentRoll < 7) {
          vial.addLiquidToTop(currentRoll);

          isDone = true;
          turnsDefending++;
          return "I poured " + currentRoll + " on my basic vial.";
        }

        if (value < 7 && currentRoll > 7) {
          vial.addLiquidToTop(currentRoll);

          isDone = true;
          turnsDefending++;
          return "I poured " + currentRoll + " on my acidic vial.";
        }
      }
    }


    vialsBoard.getPlayerVials()[random.nextInt(vialsBoard.getPlayerVials().length)]
      .addLiquidToTop(currentRoll);

    isDone = true;
    return "I'm tired of defending. I pour " + currentRoll + " on one of your vials.";
  }
}
