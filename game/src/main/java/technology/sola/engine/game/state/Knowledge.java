package technology.sola.engine.game.state;

public class Knowledge {
  private int extraLives;
  private int maxHealth;
  private int rerolls;
  private int neutralizeAgents;
  private int battlesWon = 0;
  // in battle stats
  private float currentHealth;
  private int currentRerolls;
  private int currentNeutralizeAgents;

  public Knowledge() {
    maxHealth = 4;
    extraLives = 0;
    rerolls = 10; // todo set to 1
    neutralizeAgents = 10; // todo set to 0

    reset();
  }

  public void reset() {
    currentHealth = maxHealth;
    currentRerolls = rerolls;
    currentNeutralizeAgents = neutralizeAgents;
  }

  public void addMaxHealth() {
    maxHealth++;
  }

  public float getCurrentHealth() {
    return currentHealth;
  }

  public String getFormattedCurrentHealth() {
    return String.format("%.2f", currentHealth);
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

  public void takeDamage(float damage) {
    currentHealth -= damage;
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

  public int getBattlesWon() {
    return battlesWon;
  }

  public void incrementBattlesWon() {
    battlesWon++;
  }
}
