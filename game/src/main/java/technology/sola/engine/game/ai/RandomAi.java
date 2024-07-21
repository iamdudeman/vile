package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.Vial;
import technology.sola.engine.game.state.VialsBoard;

import java.util.ArrayList;
import java.util.List;

public class RandomAi extends Ai {
  public RandomAi() {
    knowledge = new Knowledge();
  }

  @Override
  public String getName() {
    return "Ran Dumb";
  }

  @Override
  public String getGreeting(VialsBoard vialsBoard) {
    return "The name's Ran. Most people just call me Dumb though.";
  }

  @Override
  public String getStartTurnText(VialsBoard vialsBoard) {
    return "My turn to go.";
  }

  @Override
  public String nextAction(VialsBoard vialsBoard) {
    if (currentRoll == null) {
      currentRoll = vialsBoard.rollNextPh();

      return "I rolled " + currentRoll + ".";
    }

    if (knowledge.getCurrentRerolls() > 0 && random.nextInt(10) < 1) {
      knowledge.reroll();
      currentRoll = vialsBoard.rollNextPh();

      return "I rerolled " + currentRoll + ".";
    }

    List<Vial> validVials = new ArrayList<>();

    for (var vial : vialsBoard.getPlayerVials()) {
      if (!vial.isFull()) {
        validVials.add(vial);
      }
    }

    for (var vial : vialsBoard.getOpponentVials()) {
      if (!vial.isFull()) {
        validVials.add(vial);
      }
    }

    Vial chosenVial = validVials.get(random.nextInt(validVials.size()));

    chosenVial.addLiquidToTop(currentRoll);
    isDone = true;

    return "I poured " + currentRoll + " on a random vial.";
  }
}
