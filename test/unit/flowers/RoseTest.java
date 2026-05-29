package flowers;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Rose.
 */
class RoseTest {

    private Rose createRose() {
        return new Rose("Троянда Гран-Прі", 80, 60, "Червоний",
                LocalDate.of(2026, 5, 20), true, "Келих");
    }

    @Test
    void testConstructorAndGetters() {
        Rose rose = createRose();
        assertEquals("Троянда Гран-Прі", rose.getName());
        assertEquals(80, rose.getPrice());
        assertEquals(60, rose.getStemLength());
        assertEquals("Червоний", rose.getColor());
        assertEquals(LocalDate.of(2026, 5, 20), rose.getFreshnessDate());
        assertTrue(rose.isHasThorns());
        assertEquals("Келих", rose.getBudShape());
    }

    @Test
    void testGetTypeName() {
        assertEquals("Троянда", createRose().getTypeName());
    }

    @Test
    void testToStringWithThorns() {
        Rose rose = createRose();
        String str = rose.toString();
        assertTrue(str.contains("Троянда"));
        assertTrue(str.contains("з шипами"));
    }

    @Test
    void testToStringWithoutThorns() {
        Rose rose = new Rose("Аваланч", 90, 55, "Білий",
                LocalDate.now(), false, "Півонієвидний");
        String str = rose.toString();
        assertTrue(str.contains("Троянда"));
        assertFalse(str.contains("з шипами"));
    }

    @Test
    void testIdDefaultAndSetter() {
        Rose rose = createRose();
        assertEquals(-1, rose.getId());
        rose.setId(42);
        assertEquals(42, rose.getId());
    }
}
