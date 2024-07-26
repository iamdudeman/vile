package technology.sola.engine.game.state;

import technology.sola.engine.game.ai.AggressiveAi;
import technology.sola.engine.game.ai.Ai;
import technology.sola.engine.game.ai.RandomAi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class EventBoard {
  private static final int INITIAL_EVENTS_COUNT = 3;
  private static final int INITIAL_EVENT_ROUNDS = 5;
  private static final int INITIAL_VIAL_COUNT = 2;
  private static final int INITIAL_VIAL_DEPTH = 4;
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
    this.vialCountModification = eventBoard.vialCountModification;
    this.vialDepthModification = eventBoard.vialDepthModification;
  }

  public Event[] getNextEvents() {
    int eventsCount = round == 1 ? INITIAL_EVENTS_COUNT + playerKnowledge.getExtraEvents() : INITIAL_EVENTS_COUNT;
    Event[] events = new Event[eventsCount];
    List<Event> commonEvents = generateCommonEvents();
    List<Event> rareEvents = generateRareEvents();

    for (int i = 0; i < eventsCount; i++) {
      if (random.nextInt(5) < 1 && !rareEvents.isEmpty()) {
        events[i] = rareEvents.remove(random.nextInt(rareEvents.size()));
      }
      else {
        events[i] = commonEvents.remove(random.nextInt(commonEvents.size()));
      }
    }

    return events;
  }

  private List<Event> generateCommonEvents() {
    List<Event> commonEvents = new ArrayList<>();

    if (round >= INITIAL_EVENT_ROUNDS) {
      commonEvents.add(battleRandomAi());

      if (playerKnowledge.getBattlesWon() > 1) {
        commonEvents.add(battleRandomAi());
        commonEvents.add(battleAggressiveAi());
        commonEvents.add(battleAggressiveAi());
        commonEvents.add(battleAggressiveAi());
      } else {
        commonEvents.add(battleRandomAi());
        commonEvents.add(battleRandomAi());
        commonEvents.add(battleRandomAi());
        commonEvents.add(battleAggressiveAi());
      }

      return commonEvents;
    }

    // battle events
    if (round > 0 && round < 3) {
      commonEvents.add(battleRandomAi());
    }

    if (round >= 3 && random.nextBoolean()) {
      commonEvents.add(battleAggressiveAi());
    }

    // modification events
    commonEvents.add(
      modificationBoardValue(
        random.nextInt(5, 9),
        random.nextInt(playerKnowledge.getBattlesWon()) > 1
      )
    );
    commonEvents.add(
      modificationBoardValue(
        random.nextInt(7 - round, 7 + round + 1),
        random.nextInt(playerKnowledge.getBattlesWon()) > 2
      )
    );

    if (vialCountModification == 0) {
      commonEvents.add(modificationVialsBoardVialCount());
    }

    // knowledge events
    commonEvents.add(knowledgeReBrew());

    if (playerKnowledge.getNeutralizeAgents() == 0) {
      commonEvents.add(knowledgeNeutralizingAgent());
    } else if (playerKnowledge.getNeutralizeAgents() < 3 && random.nextBoolean()) {
      commonEvents.add(knowledgeNeutralizingAgent());
    } else if (playerKnowledge.getNeutralizeAgents() > 3 && random.nextInt(10) < 3) {
      commonEvents.add(knowledgeNeutralizingAgent());
    }

    if (playerKnowledge.getInstability() == 1 && random.nextInt(10) < 3) {
      commonEvents.add(knowledgeInstability());
    }

    if (playerKnowledge.getCurrentHealth() < 5) {
      commonEvents.add(knowledgePlayerHealth());
    }

    return commonEvents;
  }

  private List<Event> generateRareEvents() {
    List<Event> rareEvents = new ArrayList<>();

    if (round >= INITIAL_EVENT_ROUNDS) {
      rareEvents.add(battleAggressiveAi());

      return rareEvents;
    }

    // battle events
    rareEvents.add(battleAggressiveAi());

    // modification events
    rareEvents.add(
      modificationBoardValue(
        random.nextInt(2, 12),
        random.nextInt(playerKnowledge.getBattlesWon()) > 1
      )
    );

    if (vialDepthModification == 0) {
      rareEvents.add(modificationVialsBoardVialDepth());
    }

    if (playerKnowledge.getExtraEvents() == 0) {
      rareEvents.add(modificationEventBoard());
    }

    // knowledge events
    rareEvents.add(knowledgeInstability());

    if (playerKnowledge.getCurrentHealth() < 10) {
      rareEvents.add(knowledgePlayerHealth());
    }

    if (playerKnowledge.getExtraLives() == 0) {
      rareEvents.add(knowledgeExtraLife());
    }

    return rareEvents;
  }

  private Event battleRandomAi() {
    RandomAi randomAi = new RandomAi();
    Knowledge aiKnowledge = randomAi.getKnowledge();

    // battles won buffs
    if (playerKnowledge.getBattlesWon() > 1) {
      aiKnowledge.addNeutralize();
    }

    if (playerKnowledge.getBattlesWon() > 3) {
      aiKnowledge.addNeutralize();
    }

    for (int i = 0; i < playerKnowledge.getBattlesWon(); i++) {
      aiKnowledge.addReBrew();
    }

    aiKnowledge.addMaxHealth((playerKnowledge.getBattlesWon() - 1) / 2f);

    // round buffs
    for (int i = 1; i < round - 1; i++) {
      aiKnowledge.addMaxHealth(0.25f);
    }

    if (round >= INITIAL_EVENT_ROUNDS - 1) {
      aiKnowledge.addReBrew();
    }
    if (round >= INITIAL_EVENT_ROUNDS) {
      aiKnowledge.addReBrew();
      aiKnowledge.addNeutralize();
    }

    return battle(randomAi);
  }

  private Event battleAggressiveAi() {
    AggressiveAi aggressiveAi = new AggressiveAi();
    Knowledge aiKnowledge = aggressiveAi.getKnowledge();

    // battles won buffs
    if (playerKnowledge.getBattlesWon() > 1) {
      aiKnowledge.addReBrew();
    }

    aiKnowledge.addMaxHealth((playerKnowledge.getBattlesWon() - 1) / 2f);

    // round buffs
    for (int i = 1; i < round - 1; i++) {
      aiKnowledge.addMaxHealth(0.25f);
    }

    if (round >= INITIAL_EVENT_ROUNDS - 1) {
      aiKnowledge.addReBrew();
    }
    if (round >= INITIAL_EVENT_ROUNDS) {
      aiKnowledge.addReBrew();
    }

    return battle(aggressiveAi);
  }

  private Event battle(Ai ai) {
    return new BattleEvent("Battle", "", "", () -> {
      VialsBoard vialsBoard = new VialsBoard(
        playerKnowledge,
        ai,
        INITIAL_VIAL_COUNT + vialCountModification,
        INITIAL_VIAL_DEPTH + vialDepthModification
      );

      modifyBoard(vialsBoard);

      return vialsBoard;
    });
  }

  private Event modificationBoardValue(int value, boolean isPlayer) {
    String whoPhrase;

    if (isPlayer) {
      whoPhrase = "you have";
    } else {
      whoPhrase = "your opponent has";
    }

    String full = String.format("The next set of vials have been modified so %s a vile with a pH of %d poured in it.", whoPhrase, value);

    return new ModificationEvent("Modification", "", full, () -> {
      if (isPlayer) {
        playerModifications.add(value);
      } else {
        opponentModifications.add(value);
      }
    });
  }

  private Event modificationVialsBoardVialDepth() {
    return new ModificationEvent("Modification","", "Your next game of vials will have deeper vials in play.", () -> {
      vialDepthModification++;
    });
  }

  private Event modificationVialsBoardVialCount() {
    return new ModificationEvent("Modification", "", "Your next game of vials will have an additional vial in play.", () -> {
      vialCountModification++;
    });
  }

  private Event modificationEventBoard() {
    return new ModificationEvent("Modification", "", "Your craftiness has allowed you to choose from more events once in between battles.", () -> {
      playerKnowledge.incrementExtraEvents();
    });
  }

  private Event knowledgePlayerHealth() {
    return new ModificationEvent("Knowledge", "", "You have modified your body to be able to handle poisonous brews more effectively.", () -> {
      playerKnowledge.addMaxHealth(0.5f);
    });
  }

  private Event knowledgeReBrew() {
    return new ModificationEvent("Knowledge", "", "You learned how to make an additional rebrew.", () -> {
      playerKnowledge.addReBrew();
    });
  }

  private Event knowledgeExtraLife() {
    return new ModificationEvent("Knowledge", "", "You learned how to cheat death another time.", () -> {
      playerKnowledge.addExtraLife();
    });
  }

  private Event knowledgeInstability() {
    return new ModificationEvent("Knowledge", "", "Your brewing has become more unstable, increasing the likelihood of more acidic or basic brews.", () -> {
      playerKnowledge.incrementInstability();
    });
  }

  private Event knowledgeNeutralizingAgent() {
    String full = "You learned how to make an additional neutralizing agent.";

    if (playerKnowledge.getNeutralizeAgents() == 1) {
      full = "You learned how to make neutralizing agents. Neutralizing agents will neutralize the top of a vial in place of pouring a brew that round.";
    }

    return new ModificationEvent("Knowledge", "", full, () -> {
      playerKnowledge.addNeutralize();
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

  public interface Event {
    String title();

    String shortDescription();

    String fullDescription();
  }

  public record BattleEvent(
    String title,
    String shortDescription,
    String fullDescription,
    Supplier<VialsBoard> buildBoard
  ) implements Event {
  }

  public record ModificationEvent(
    String title,
    String shortDescription,
    String fullDescription,
    Runnable apply
  ) implements Event {
  }
}
