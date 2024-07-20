package technology.sola.engine.game.state;

public class EventBoard {
  private final Knowledge playerKnowledge;

  public EventBoard(Knowledge playerKnowledge) {
    this.playerKnowledge = playerKnowledge;
  }

  public enum EventType {
    BATTLE,
    MODIFICATION,
    KNOWLEDGE
  }
}
