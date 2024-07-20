package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;

import java.util.function.Consumer;

public class EventBoard {
  private final Knowledge playerKnowledge;
  private int round = 1;

  public EventBoard(Knowledge playerKnowledge) {
    this.playerKnowledge = playerKnowledge;
  }

  public Event[] getNextEvents() {
    int maxRound = GameBalanceConfiguration.INITIAL_EVENT_ROUNDS;
    int events = GameBalanceConfiguration.INITIAL_EVENTS;

    if (round >= maxRound) {
      // todo return only battles
    }

    round++;
    return null;
  }

  public record Event(
    EventType type,
    String text,
    Consumer<VialsBoard> applyEvent
  ) {
  }

  public enum EventType {
    BATTLE,
    MODIFICATION,
    KNOWLEDGE
  }
}
