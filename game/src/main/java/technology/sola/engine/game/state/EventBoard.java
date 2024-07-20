package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;
import technology.sola.engine.game.ai.RandomAi;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventBoard {
  private final Knowledge playerKnowledge;
  private int round;

  public EventBoard(Knowledge playerKnowledge, int round) {
    this.playerKnowledge = playerKnowledge;
    this.round = round;
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
        return new VialsBoard(playerKnowledge, new RandomAi());
      }),
      new Event(EventType.BATTLE, "A common test battle", () -> {
        return new VialsBoard(playerKnowledge, new RandomAi());
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
