package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.Vial;
import technology.sola.engine.game.state.VialsBoard;

import java.util.ArrayList;
import java.util.Arrays;
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

      return "I brewed " + currentRoll + ".";
    }

    if (knowledge.getCurrentRerolls() > 0 && random.nextInt(10) < 1) {
      knowledge.reroll();
      currentRoll = vialsBoard.rollNextPh();

      return "I rebrewed " + currentRoll + ".";
    }

    List<Vial> validVials = new ArrayList<>(
      Arrays.asList(vialsBoard.getPlayerVials())
    );

    validVials.addAll(Arrays.asList(vialsBoard.getOpponentVials()));

    Vial chosenVial = validVials.get(random.nextInt(validVials.size()));

    chosenVial.addLiquidToTop(currentRoll);
    isDone = true;

    return "I poured " + currentRoll + " on a random vial.";
  }
}
