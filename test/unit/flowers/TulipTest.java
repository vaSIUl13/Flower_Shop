package flowers;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Tulip.
 */
class TulipTest {

    @Test
    void testConstructorAndGetters() {
        Tulip tulip = new Tulip("Тюльпан Ред", 45, 40, "Червоний",
                LocalDate.of(2026, 5, 20), "Овальна", false);
        assertEquals("Тюльпан Ред", tulip.getName());
        assertEquals(45, tulip.getPrice());
        assertEquals(40, tulip.getStemLength());
        assertEquals("Червоний", tulip.getColor());
        assertEquals("Овальна", tulip.getPetalShape());
        assertFalse(tulip.isDouble());
    }

    @Test
    void testDoubleTulip() {
        Tulip tulip = new Tulip("Махровий", 50, 38, "Фіолетовий",
                LocalDate.now(), "Округла", true);
        assertTrue(tulip.isDouble());
    }

    @Test
    void testGetTypeName() {
        Tulip tulip = new Tulip("Т", 10, 10, "Б", LocalDate.now(), "О", false);
        assertEquals("Тюльпан", tulip.getTypeName());
    }

    @Test
    void testToString() {
        Tulip tulip = new Tulip("Тюльпан Голд", 40, 35, "Жовтий",
                LocalDate.now(), "Овальна", false);
        String str = tulip.toString();
        assertTrue(str.contains("Тюльпан"));
        assertTrue(str.contains("Тюльпан Голд"));
    }
}
