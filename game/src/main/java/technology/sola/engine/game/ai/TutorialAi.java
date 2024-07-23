package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.Vial;
import technology.sola.engine.game.state.VialsBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TutorialAi extends Ai {
  public TutorialAi() {
    super(prepareKnowledge());
  }

  @Override
  public String getName() {
    return "Tutorial";
  }

  @Override
  public String getAssetId() {
    return null;
  }

  @Override
  public String getGreeting(VialsBoard vialsBoard) {
    return "Welcome to the game of Vials.";
  }

  @Override
  public String getStartTurnText(VialsBoard vialsBoard) {
    return "My turn to go.";
  }

  @Override
  public String nextAction(VialsBoard vialsBoard) {
    if (currentBrew == null) {
      currentBrew = vialsBoard.brewNextPh();

      return "I brewed " + currentBrew + ".";
    }

    Vial chosenVial = getRandomVial(vialsBoard);

    chosenVial.addLiquidToTop(currentBrew);
    isDone = true;

    return "I poured " + currentBrew + " in one of my vials.";
  }

  private Vial getRandomVial(VialsBoard vialsBoard) {
    List<Vial> validVials = new ArrayList<>(
      Arrays.asList(vialsBoard.getOpponentVials())
    );

    return validVials.get(random.nextInt(validVials.size()));
  }

  private static Knowledge prepareKnowledge() {
    Knowledge knowledge = new Knowledge();

    knowledge.takeDamage(1.5f);

    return knowledge;
  }
}
