package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;
import technology.sola.engine.game.ai.AggressiveAi;
import technology.sola.engine.game.ai.RandomAi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class EventBoard {
  private final Random random = new Random();
  private final Knowledge playerKnowledge;
  private int round = 1;
  private List<Integer> playerModifications = new ArrayList<>();
  private List<Integer> opponentModifications = new ArrayList<>();

  public EventBoard(Knowledge playerKnowledge) {
    this.playerKnowledge = playerKnowledge;
  }

  public EventBoard(EventBoard eventBoard) {
    this.playerKnowledge = eventBoard.playerKnowledge;
    this.round = eventBoard.round + 1;
    this.playerModifications = eventBoard.playerModifications;
    this.opponentModifications = eventBoard.opponentModifications;
  }

  public Event[] getNextEvents() {
    int maxRounds = GameBalanceConfiguration.INITIAL_EVENT_ROUNDS;
    int eventsCount = GameBalanceConfiguration.INITIAL_EVENTS_COUNT;

    if (round >= maxRounds) {
      // todo return only battles
    }

    round++;
    // todo temp logic
    return new Event[] {
      new BattleEvent("Battle", () -> {
        VialsBoard vialsBoard = new VialsBoard(playerKnowledge, new RandomAi());

        modifyBoard(vialsBoard);

        return vialsBoard;
      }),
      new BattleEvent("Battle", () -> {
        VialsBoard vialsBoard = new VialsBoard(playerKnowledge, new AggressiveAi());

        modifyBoard(vialsBoard);

        return vialsBoard;
      }),
      new ModificationEvent("Modification", () -> {
        playerModifications.add(7);

        return "Modified the next battle so you have a starting pH of 7";
      }),
      new ModificationEvent("Knowledge", () -> {
        playerKnowledge.addReroll();

        return "You get a small test buff giving you a reroll.";
      }),
      new ModificationEvent("Knowledge", () -> {
        playerKnowledge.addNeutralize();

        return "You get a small test buff giving you a neutralizing agent.";
      }),
    };
  }

  public Knowledge getPlayerKnowledge() {
    return playerKnowledge;
  }

  public int getRound() {
    return round;
  }

  private void modifyBoard(VialsBoard board) {
    playerModifications.forEach(playerModification -> {
      int index = random.nextInt(board.getPlayerVials().length);

      board.getPlayerVials()[index].addLiquidToTop(playerModification);
    });

    opponentModifications.forEach(playerModification -> {
      int index = random.nextInt(board.getOpponentVials().length);

      board.getOpponentVials()[index].addLiquidToTop(playerModification);
    });
  }

  public interface Event<T> {
    String title();

    Supplier<T> payload();
  }

  public record BattleEvent(String title, Supplier<VialsBoard> payload) implements Event<VialsBoard> {
  }

  public record ModificationEvent(String title, Supplier<String> payload) implements Event<String> {
  }
}
