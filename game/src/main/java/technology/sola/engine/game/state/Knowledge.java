package technology.sola.engine.game.state;

public class Knowledge {
  // todo unstable (2 -> 3 -> 3.5 -> 4)
  // todo extra events

  private int extraLives;
  private int instability = 1;
  private int maxHealth;
  private int reBrews;
  private int neutralizeAgents;
  private int battlesWon = 0;
  // in battle stats
  private float currentHealth;
  private int currentRebrews;
  private int currentNeutralizeAgents;

  public Knowledge() {
    maxHealth = 4;
    extraLives = 0;
    reBrews = 1;
    neutralizeAgents = 0;

    reset();
  }

  public void reset() {
    currentHealth = maxHealth;
    currentRebrews = reBrews;
    currentNeutralizeAgents = neutralizeAgents;
  }


  public void addMaxHealth() {
    maxHealth++;
    currentHealth = maxHealth;
  }

  public float getCurrentHealth() {
    return currentHealth;
  }

  public String getFormattedCurrentHealth() {
    return String.format("%.2f", currentHealth);
  }

  public void takeDamage(float damage) {
    currentHealth -= damage;
  }


  public void addExtraLife() {
    extraLives++;
  }

  public int getExtraLives() {
    return extraLives;
  }


  public void addReBrew() {
    reBrews++;
    currentRebrews = reBrews;
  }

  public void reBrew() {
    currentRebrews--;
  }

  public int getReBrews() {
    return reBrews;
  }

  public int getCurrentRebrews() {
    return currentRebrews;
  }


  public void neutralize() {
    currentNeutralizeAgents--;
  }

  public void addNeutralize() {
    neutralizeAgents++;
    currentNeutralizeAgents = neutralizeAgents;
  }

  public int getNeutralizeAgents() {
    return neutralizeAgents;
  }

  public int getCurrentNeutralizeAgents() {
    return currentNeutralizeAgents;
  }


  public int getInstability() {
    return instability;
  }

  public void incrementInstability() {
    instability++;
  }

  public int getBattlesWon() {
    return battlesWon;
  }

  public void incrementBattlesWon() {
    battlesWon++;
  }
}
