package technology.sola.engine.game.state;

import technology.sola.engine.game.GameBalanceConfiguration;

import java.util.Arrays;

public class Vial {
  private final Integer[] contents;
  private int firstEmptyIndex;

  public Vial() {
    this.contents = new Integer[GameBalanceConfiguration.VIAL_DEPTH];

    reset();
  }

  public void reset() {
    this.firstEmptyIndex = this.contents.length - 1;

    Arrays.fill(this.contents, null);
  }

  public float getDamage() {
    return Math.abs(getCurrentAverage() - 7);
  }

  public float getCurrentAverage() {
    float score = 0;
    int count = 0;

    for (Integer content : contents) {
      if (content != null) {
        score += content;
        count++;
      }
    }

    if (count == 0) {
      return 7;
    }

    score /= count;

    return score;
  }

  public boolean isEmpty() {
    return firstEmptyIndex == contents.length - 1;
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
}
