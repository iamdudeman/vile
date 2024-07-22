package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.VialsBoard;

public class AggressiveAi extends Ai {
  private int turnsDefending = 0;

  public AggressiveAi() {
    knowledge = new Knowledge();

    knowledge.addReBrew();
    knowledge.addReBrew();

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
    if (currentBrew == null) {
      currentBrew = vialsBoard.brewNextPh();

      return "I brewed " + currentBrew + ".";
    }

    if (currentBrew == 7) {
      vialsBoard.getOpponentVials()[random.nextInt(vialsBoard.getOpponentVials().length)]
        .addLiquidToTop(currentBrew);

      isDone = true;
      return "I poured my 7 on my board.";
    }

    for (var vial : vialsBoard.getPlayerVials()) {
      float value = vial.getCurrentAverage();

      if (value < 7 && currentBrew < 7) {
        vial.addLiquidToTop(currentBrew);

        turnsDefending = 0;
        isDone = true;
        return "I poured " + currentBrew + " in your acidic vial.";
      }

      if (value > 7 && currentBrew > 7) {
        vial.addLiquidToTop(currentBrew);

        turnsDefending = 0;
        isDone = true;
        return "I poured " + currentBrew + " in your basic vial.";
      }
    }

    for (var vial : vialsBoard.getPlayerVials()) {
      if (vial.isEmpty()) {
        vial.addLiquidToTop(currentBrew);

        isDone = true;
        turnsDefending = 0;
        return "I poured " + currentBrew + " in your empty vial.";
      }
    }

    if (knowledge.getCurrentRebrews() > 0) {
      knowledge.reBrew();
      currentBrew = vialsBoard.brewNextPh();

      return "I can't attack well with this. I rebrewed " + currentBrew + ".";
    }

    if (turnsDefending < 3) {
      for (var vial : vialsBoard.getOpponentVials()) {
        float value = vial.getCurrentAverage();

        if (value > 7 && currentBrew < 7) {
          vial.addLiquidToTop(currentBrew);

          isDone = true;
          turnsDefending++;
          return "I poured " + currentBrew + " in my basic vial.";
        }

        if (value < 7 && currentBrew > 7) {
          vial.addLiquidToTop(currentBrew);

          isDone = true;
          turnsDefending++;
          return "I poured " + currentBrew + " in my acidic vial.";
        }
      }
    }


    vialsBoard.getPlayerVials()[random.nextInt(vialsBoard.getPlayerVials().length)]
      .addLiquidToTop(currentBrew);

    isDone = true;
    return "I'm tired of defending. I pour " + currentBrew + " in one of your vials.";
  }
}
