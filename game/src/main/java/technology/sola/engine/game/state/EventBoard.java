package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;
import technology.sola.engine.game.ai.RandomAi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
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
    int maxRound = GameBalanceConfiguration.INITIAL_EVENT_ROUNDS;
    int events = GameBalanceConfiguration.INITIAL_EVENTS;

    if (round >= maxRound) {
      // todo return only battles
    }

    round++;
    // todo temp logic
    return new Event[] {
      new Event(EventType.BATTLE, "Another common test battle", () -> {
        VialsBoard vialsBoard = new VialsBoard(playerKnowledge, new RandomAi());

        modifyBoard(vialsBoard);

        return vialsBoard;
      }),
      new Event(EventType.BATTLE, "A common test battle", () -> {
        VialsBoard vialsBoard = new VialsBoard(playerKnowledge, new RandomAi());

        modifyBoard(vialsBoard);

        return vialsBoard;
      }),
      new Event(EventType.MODIFICATION, "Modified the next battle", () -> {
        playerModifications.add(7);

        return null;
      }),
      new Event(EventType.KNOWLEDGE, "You get a small test buff", () -> {
        playerKnowledge.addReroll();

        return null;
      })
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

  public record Event(
    EventType type,
    String text,
    Supplier<VialsBoard> applyEvent
  ) {
  }

  public enum EventType {
    BATTLE("Battle"),
    MODIFICATION("Laboratory"),
    KNOWLEDGE("Library");

    public final String title;

    EventType(String title) {
      this.title = title;
    }
  }
}
