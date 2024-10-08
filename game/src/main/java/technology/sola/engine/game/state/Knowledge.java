package technology.sola.engine.game.state;

public class Knowledge {
  private int extraLives;
  private int extraEvents;
  private int instability;
  private float maxHealth;
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
    extraEvents = 0;
    instability = 1;
    reBrews = 1;
    neutralizeAgents = 0;

    reset();
  }

  public void reset() {
    currentHealth = maxHealth;
    currentRebrews = reBrews;
    currentNeutralizeAgents = neutralizeAgents;
  }


  public void addMaxHealth(float additionalMaxHealth) {
    maxHealth += additionalMaxHealth;
    currentHealth = maxHealth;
  }

  public float getCurrentHealth() {
    return currentHealth;
  }

  public String getFormattedCurrentHealth() {
    if (currentHealth <= 0) {
      return "0.00";
    }

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

  public int getExtraEvents() {
    return extraEvents;
  }

  public void incrementExtraEvents() {
    extraEvents++;
  }
}
