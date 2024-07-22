package technology.sola.engine.game.ai;

import technology.sola.engine.game.AssetIds;
import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.Vial;
import technology.sola.engine.game.state.VialsBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomAi extends Ai {
  public RandomAi() {
    super(new Knowledge());
  }

  @Override
  public String getName() {
    return "Ran Dum";
  }

  @Override
  public String getAssetId() {
    return AssetIds.Images.WARLOCAT;
  }

  @Override
  public String getGreeting(VialsBoard vialsBoard) {
    return "The name's Ran. Most people just call me Dum though.";
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

    if (knowledge.getCurrentRebrews() > 0 && random.nextInt(10) < 1) {
      knowledge.reBrew();
      currentBrew = vialsBoard.brewNextPh();

      return "I rebrewed " + currentBrew + ".";
    }

    List<Vial> validVials = new ArrayList<>(
      Arrays.asList(vialsBoard.getPlayerVials())
    );

    validVials.addAll(Arrays.asList(vialsBoard.getOpponentVials()));

    Vial chosenVial = validVials.get(random.nextInt(validVials.size()));

    chosenVial.addLiquidToTop(currentBrew);
    isDone = true;

    return "I poured " + currentBrew + " in a random vial.";
  }
}
