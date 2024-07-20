package technology.sola.engine.game.state;

public class Knowledge {
  private int extraLives;
  private int rerolls;
  private int neutralizeAgents;
  private int currentRerolls;
  private int currentNeutralizeAgents;

  public Knowledge() {
    extraLives = 0;
    rerolls = 10; // todo set to 1
    neutralizeAgents = 10; // todo set to 0

    reset();
  }

  public void reset() {
    currentRerolls = rerolls;
    currentNeutralizeAgents = neutralizeAgents;
  }

  public void addExtraLife() {
    extraLives++;
  }

  public void addReroll() {
    rerolls++;
  }

  public void reroll() {
    currentRerolls--;
  }

  public void neutralize() {
    currentNeutralizeAgents--;
  }

  public void addNeutralize() {
    neutralizeAgents++;
  }

  public int getExtraLives() {
    return extraLives;
  }

  public int getRerolls() {
    return rerolls;
  }

  public int getCurrentRerolls() {
    return currentRerolls;
  }

  public int getNeutralizeAgents() {
    return neutralizeAgents;
  }

  public int getCurrentNeutralizeAgents() {
    return currentNeutralizeAgents;
  }
}
