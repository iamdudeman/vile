package technology.sola.engine.game.state;

import technology.sola.engine.game.ai.AggressiveAi;
import technology.sola.engine.game.ai.RandomAi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class EventBoard {
  private static final int INITIAL_EVENTS_COUNT = 2;
  private static final int INITIAL_EVENT_ROUNDS = 3;
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

  public Event<?>[] getNextEvents() {
    int maxRounds = INITIAL_EVENT_ROUNDS;
    int eventsCount = INITIAL_EVENTS_COUNT + playerKnowledge.getExtraEvents();

    if (round >= maxRounds) {
      // todo return only battles
    }

    round++;
    // todo temp logic
    return new Event[] {
      battleRandomAi(),
      battleAggressiveAi(),
      modificationBoardValue(7, true),
      knowledgeInstability(),
      knowledgeReroll(),
      knowledgeNeutralizingAgent(),
    };
  }

  private Event<?> battleRandomAi() {
    return new BattleEvent("Battle", () -> {
      VialsBoard vialsBoard = new VialsBoard(playerKnowledge, new RandomAi());

      modifyBoard(vialsBoard);

      return vialsBoard;
    });
  }

  private Event<?> battleAggressiveAi() {
    return new BattleEvent("Battle", () -> {
      VialsBoard vialsBoard = new VialsBoard(playerKnowledge, new AggressiveAi());

      modifyBoard(vialsBoard);

      return vialsBoard;
    });
  }

  private Event<?> modificationBoardValue(int value, boolean isPlayer) {
    return new ModificationEvent("Modification", () -> {
      String whoPhrase = "";

      if (isPlayer) {
        playerModifications.add(value);
        whoPhrase = "you have";
      } else {
        opponentModifications.add(value);
        whoPhrase = "your opponent has";
      }

      return String.format("Modified the next battle so %s a starting pH of %d", whoPhrase, value);
    });
  }

  private Event<?> knowledgeReroll() {
    return new ModificationEvent("Knowledge", () -> {
      playerKnowledge.addReBrew();

      return "You get a small test buff giving you a rebrew.";
    });
  }

  private Event<?> knowledgeInstability() {
    return new ModificationEvent("Knowledge", () -> {
      playerKnowledge.incrementInstability();

      return "Your brewing has become more unstable, increasing the likelihood of more acidic or basic brews.";
    });
  }

  private Event<?> knowledgeNeutralizingAgent() {
    return new ModificationEvent("Knowledge", () -> {
      playerKnowledge.addNeutralize();

      return "You get a small test buff giving you a neutralizing agent.";
    });
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
