package technology.sola.engine.game.state;

import java.util.Arrays;

public class Vial {
  private final Integer[] contents;
  private int firstEmptyIndex;

  public Vial(int depth) {
    this.contents = new Integer[depth];

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
      if (willCauseCollapse(liquid)) {
        this.contents[this.firstEmptyIndex + 1] = null;
        this.contents[this.firstEmptyIndex + 2] = null;
        this.firstEmptyIndex += 2;

        while (this.firstEmptyIndex + 1 < this.contents.length) {
          if (this.contents[this.firstEmptyIndex + 1] == 7) {
            this.contents[this.firstEmptyIndex + 1] = null;
            this.firstEmptyIndex++;
          } else {
            break;
          }
        }

      } else {
        this.contents[this.firstEmptyIndex] = liquid;
        this.firstEmptyIndex--;
      }
    }
  }

  public boolean willCauseCollapse(int liquid) {
    if (this.firstEmptyIndex >= 0) {
      int diff = contents.length - firstEmptyIndex;

      if (diff < 3) {
        return false;
      }

      int topValue = this.contents[this.firstEmptyIndex + 1];
      int belowValue = this.contents[this.firstEmptyIndex + 2];

      if (topValue == 7) {
        return false;
      }

      if (liquid == 7) {
        int minValue = Math.min(topValue, belowValue);
        int maxValue = Math.max(topValue, belowValue);

        return maxValue + minValue == 14;
      } else if (belowValue == 7) {
        int minValue = Math.min(topValue, liquid);
        int maxValue = Math.max(topValue, liquid);

        return maxValue + minValue == 14;
      }
    }

    return false;
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
