package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;

import java.util.Arrays;

public class Vial {
  private final Integer[] contents;
  private int firstEmptyIndex;

  public Vial() {
    this.contents = new Integer[GameBalanceConfiguration.VIAL_DEPTH];
    this.firstEmptyIndex = this.contents.length - 1;

    Arrays.fill(this.contents, null);
  }

  public String getFormattedScore() {
    return String.format("%.2f", getScore());
  }

  public Float getScore() {
    if (firstEmptyIndex >= 0) {
      return null;
    }

    float score = 0;

    for (Integer content : contents) {
      score += content;
    }

    score /= contents.length;

    return score;
  }

  public boolean isFull() {
    return firstEmptyIndex < 0;
  }

  public Integer[] getContents() {
    return contents;
  }

  public void addLiquidToTop(int liquid) {
    if (this.firstEmptyIndex >= 0) {
      this.contents[this.firstEmptyIndex] = liquid;
      this.firstEmptyIndex--;
    }
  }

  public void neutralizeTop() {
    if (firstEmptyIndex == this.contents.length - 1) {
      addLiquidToTop(7);
    } else {
      int value = this.contents[firstEmptyIndex + 1];

      addLiquidToTop(14 - value);
    }
  }

  public void removeTopLiquid() {
    for (int i = 0; i < this.contents.length; i++) {
      if (this.contents[i] != null) {
        this.firstEmptyIndex = i;
        this.contents[i] = null;
        break;
      }
    }
  }
}
