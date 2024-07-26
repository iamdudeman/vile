package technology.sola.engine.game.state;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VialTest {
  @Nested
  class collapseTests {
    @Test
    void whenNeutralizingAboveSeven_shouldCollapse() {
      Vial vial = new Vial(5);

      vial.addLiquidToTop(10);
      vial.addLiquidToTop(7);
      vial.addLiquidToTop(8);

      assertTrue(vial.willCauseCollapse(6));
      vial.addLiquidToTop(6);

      assertNull(vial.getContents()[0]);
      assertNull(vial.getContents()[1]);
      assertNull(vial.getContents()[2]);
      assertNull(vial.getContents()[3]);
      assertEquals(10, vial.getContents()[4]);
    }

    @Test
    void whenNotNeutralizingAboveSeven_shouldNotCollapse() {
      Vial vial = new Vial(5);

      vial.addLiquidToTop(10);
      vial.addLiquidToTop(7);
      vial.addLiquidToTop(8);

      assertFalse(vial.willCauseCollapse(5));
      vial.addLiquidToTop(5);

      assertNull(vial.getContents()[0]);
      assertEquals(5, vial.getContents()[1]);
      assertEquals(8, vial.getContents()[2]);
      assertEquals(7, vial.getContents()[3]);
      assertEquals(10, vial.getContents()[4]);
    }

    @Test
    void whenNeutralizedAndAddingSeven_shouldCollapse() {
      Vial vial = new Vial(5);

      vial.addLiquidToTop(10);
      vial.addLiquidToTop(8);
      vial.addLiquidToTop(6);

      assertTrue(vial.willCauseCollapse(7));
      vial.addLiquidToTop(7);

      assertNull(vial.getContents()[0]);
      assertNull(vial.getContents()[1]);
      assertNull(vial.getContents()[2]);
      assertNull(vial.getContents()[3]);
      assertEquals(10, vial.getContents()[4]);
    }

    @Test
    void whenNotNeutralizedAndAddingSeven_shouldNotCollapse() {
      Vial vial = new Vial(5);

      vial.addLiquidToTop(10);
      vial.addLiquidToTop(8);
      vial.addLiquidToTop(5);

      assertFalse(vial.willCauseCollapse(7));
      vial.addLiquidToTop(7);

      assertNull(vial.getContents()[0]);
      assertEquals(7, vial.getContents()[1]);
      assertEquals(5, vial.getContents()[2]);
      assertEquals(8, vial.getContents()[3]);
      assertEquals(10, vial.getContents()[4]);
    }

    @Test
    void whenNeutralizing_shouldCollapseMultipleSevens() {
      Vial vial = new Vial(5);

      vial.addLiquidToTop(7);
      vial.addLiquidToTop(7);
      vial.addLiquidToTop(8);

      assertTrue(vial.willCauseCollapse(6));
      vial.addLiquidToTop(6);

      assertNull(vial.getContents()[0]);
      assertNull(vial.getContents()[1]);
      assertNull(vial.getContents()[2]);
      assertNull(vial.getContents()[3]);
      assertNull(vial.getContents()[4]);
    }

    @Test
    void shouldNotCollapseMultipleSevens() {
      Vial vial = new Vial(5);

      vial.addLiquidToTop(7);
      vial.addLiquidToTop(7);

      assertFalse(vial.willCauseCollapse(7));
      vial.addLiquidToTop(7);

      assertNull(vial.getContents()[0]);
      assertNull(vial.getContents()[1]);
      assertEquals(7, vial.getContents()[2]);
      assertEquals(7, vial.getContents()[3]);
      assertEquals(7, vial.getContents()[4]);
    }
  }
}
