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
    return "Random";
  }

  @Override
  public String nextAction(VialsBoard vialsBoard) {
    if (currentRoll == null) {
      currentRoll = vialsBoard.rollNextPh();

      return "I rolled a " + currentRoll;
    }

    if (knowledge.getCurrentRerolls() > 0 && random.nextInt(10) < 1) {
      knowledge.reroll();
      currentRoll = vialsBoard.rollNextPh();

      return "I rerolled a " + currentRoll;
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

    return "I poured " + currentRoll + " on a random vial";
  }
}
