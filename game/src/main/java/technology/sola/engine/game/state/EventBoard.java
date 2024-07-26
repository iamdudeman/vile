package technology.sola.engine.game.state;

import technology.sola.engine.game.AssetIds;
import technology.sola.engine.game.ai.AggressiveAi;
import technology.sola.engine.game.ai.Ai;
import technology.sola.engine.game.ai.AiInfo;
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
  private static final List<AiInfo> randomAiInfoList = new ArrayList<>();
  private static final List<AiInfo> aggressiveAiInfoList = new ArrayList<>();

  static {
    // random options
    randomAiInfoList.add(new AiInfo(
      "Ran dum",
      AssetIds.Images.WARLOCAT,
      "The name's Ran. Most people just call me Dum though."
    ));
    randomAiInfoList.add(new AiInfo(
      "Isaac Mewton",
      AssetIds.Images.DEF_NOT_A_CAT,
      "I don't think you understand the gravity of your situation."
    ));

    // aggressive options
    aggressiveAiInfoList.add(new AiInfo(
      "Quack Dealer",
      AssetIds.Images.DUCKY,
      "You cannot stop me from my dream. I will create the perfect donut!"
    ));
    aggressiveAiInfoList.add(new AiInfo(
      "Osmeowdias",
      AssetIds.Images.DEF_NOT_A_CAT,
      "Ozy's the name and being a pharaoh's my game."
    ));
  }

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
      modificationBoardValue(random.nextInt(5, 9))
    );
    commonEvents.add(
      modificationBoardValue(random.nextInt(7 - round, 7 + round + 1))
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
    rareEvents.add(modificationBoardValue(random.nextInt(2, 12)));

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
    AiInfo aiInfo = randomAiInfoList.size() == 1
      ? randomAiInfoList.get(0)
      : randomAiInfoList.remove(random.nextInt(randomAiInfoList.size()));
    RandomAi randomAi = new RandomAi(aiInfo);
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

    return battle(randomAi, aiInfo.greeting());
  }

  private Event battleAggressiveAi() {
    AiInfo aiInfo = aggressiveAiInfoList.size() == 1
      ? aggressiveAiInfoList.get(0)
      : aggressiveAiInfoList.remove(random.nextInt(aggressiveAiInfoList.size()));
    AggressiveAi aggressiveAi = new AggressiveAi(aiInfo);
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

    return battle(aggressiveAi, aiInfo.greeting());
  }

  private Event battle(Ai ai, String description) {
    return new BattleEvent("Battle", description, () -> {
      VialsBoard vialsBoard = new VialsBoard(
        playerKnowledge,
        ai,
        INITIAL_VIAL_COUNT + vialCountModification,
        INITIAL_VIAL_DEPTH + vialDepthModification
      );

      if (round > 1) {
        for (int i = 0; i < playerKnowledge.getBattlesWon() - 1; i++) {
          playerModifications.add(
            random.nextInt(7 - (round - 1), 7 + (round - 1))
          );
        }
      }

      modifyBoard(vialsBoard);

      return vialsBoard;
    });
  }

  private Event modificationBoardValue(int value) {
    String description = String.format("Modify your opponent's next set of vials to start with a pH of %d poured in it.", value);

    return new ModificationEvent("Modification", description, () -> {
      opponentModifications.add(value);
    });
  }

  private Event modificationVialsBoardVialDepth() {
    return new ModificationEvent("Modification","Modify the next game of Vials to have deeper vials in play.", () -> {
      vialDepthModification++;
    });
  }

  private Event modificationVialsBoardVialCount() {
    return new ModificationEvent("Modification", "Modify the next game of Vials to have an additional vial in play.", () -> {
      vialCountModification++;
    });
  }

  private Event modificationEventBoard() {
    return new ModificationEvent("Modification", "See more events to choose from once between each battle.", () -> {
      playerKnowledge.incrementExtraEvents();
    });
  }

  private Event knowledgePlayerHealth() {
    return new ModificationEvent("Knowledge", "Modify your body to be able to handle poisonous brews more effectively.", () -> {
      playerKnowledge.addMaxHealth(0.5f);
    });
  }

  private Event knowledgeReBrew() {
    return new ModificationEvent("Knowledge", "Learn how to make an additional rebrew.", () -> {
      playerKnowledge.addReBrew();
    });
  }

  private Event knowledgeExtraLife() {
    return new ModificationEvent("Knowledge", "Learn how to cheat death another time.", () -> {
      playerKnowledge.addExtraLife();
    });
  }

  private Event knowledgeInstability() {
    return new ModificationEvent("Knowledge", "Learn how to make more unstable brews; increasing the likelihood of more acidic or basic brews.", () -> {
      playerKnowledge.incrementInstability();
    });
  }

  private Event knowledgeNeutralizingAgent() {
    String description = "Learn how to make an additional neutralizing agent.";

    if (playerKnowledge.getNeutralizeAgents() == 1) {
      description = "Learn how to make neutralizing agents. Neutralizing agents will neutralize the top of a vial in place of pouring a brew that round.";
    }

    return new ModificationEvent("Knowledge", description, () -> {
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

    String description();
  }

  public record BattleEvent(
    String title,
    String description,
    Supplier<VialsBoard> buildBoard
  ) implements Event {
  }

  public record ModificationEvent(
    String title,
    String description,
    Runnable apply
  ) implements Event {
  }
}
