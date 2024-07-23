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
  private static final int INITIAL_VIAL_COUNT = 3;
  private static final int INITIAL_VIAL_DEPTH = 5;
  private final Random random = new Random();
  private final Knowledge playerKnowledge;
  private int round = 1;
  private List<Integer> playerModifications = new ArrayList<>();
  private List<Integer> opponentModifications = new ArrayList<>();
  private int vialCountModification = 0;
  private int vialDepthModification = 0;

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
      knowledgeReBrew(),
      knowledgeNeutralizingAgent(),
    };
  }

  private Event<?> battleRandomAi() {
    return new BattleEvent("Battle", () -> {
      VialsBoard vialsBoard = new VialsBoard(
        playerKnowledge,
        new RandomAi(),
        INITIAL_VIAL_COUNT + vialCountModification,
        INITIAL_VIAL_DEPTH + vialDepthModification
      );

      modifyBoard(vialsBoard);

      return vialsBoard;
    });
  }

  private Event<?> battleAggressiveAi() {
    return new BattleEvent("Battle", () -> {
      VialsBoard vialsBoard = new VialsBoard(
        playerKnowledge,
        new AggressiveAi(),
        INITIAL_VIAL_COUNT + vialCountModification,
        INITIAL_VIAL_DEPTH + vialDepthModification
      );

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

      return String.format("The next set of vials have been modified so %s a vile with a pH of %d poured in it.", whoPhrase, value);
    });
  }

  private Event<?> modificationVialsBoardVialDepth() {
    return new ModificationEvent("Modification", () -> {
      vialDepthModification++;

      return "Your next game of vials will have deeper vials in play.";
    });
  }

  private Event<?> modificationVialsBoardVialCount() {
    return new ModificationEvent("Modification", () -> {
      vialCountModification++;

      return "Your next game of vials will have an additional vial in play.";
    });
  }

  private Event<?> modificationEventBoard() {
    return new ModificationEvent("Modification", () -> {
      playerKnowledge.incrementExtraEvents();

      return "Your craftiness has allowed you to see more events each round.";
    });
  }

  private Event<?> knowledgePlayerHealth() {
    return new ModificationEvent("Knowledge", () -> {
      playerKnowledge.addMaxHealth(0.5f);

      return "You have modified your body to be able to handle poisonous brews more effectively.";
    });
  }

  private Event<?> knowledgeReBrew() {
    return new ModificationEvent("Knowledge", () -> {
      playerKnowledge.addReBrew();

      return "You learned how to make an additional rebrews.";
    });
  }

  private Event<?> knowledgeExtraLife() {
    return new ModificationEvent("Knowledge", () -> {
      playerKnowledge.addExtraLife();

      return "You learned how to cheat death another time.";
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

      if (playerKnowledge.getNeutralizeAgents() == 1) {
        return "You learned how to make neutralizing agents. Neutralizing agents will neutralize the top of a vial in place of pouring a brew that round.";
      }

      return "You learned how to make an additional neutralizing agent.";
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
