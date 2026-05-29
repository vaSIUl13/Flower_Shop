package flowers;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Orchid.
 */
class OrchidTest {

    @Test
    void testConstructorAndGetters() {
        Orchid o = new Orchid("Орхідея Фаленопсіс", 200, 45, "Білий",
                LocalDate.of(2026, 5, 20), "Фаленопсіс", true);
        assertEquals("Орхідея Фаленопсіс", o.getName());
        assertEquals(200, o.getPrice());
        assertEquals(45, o.getStemLength());
        assertEquals("Білий", o.getColor());
        assertEquals("Фаленопсіс", o.getVariety());
        assertTrue(o.isEpiphytic());
    }

    @Test
    void testNotEpiphytic() {
        Orchid o = new Orchid("Цимбідіум", 220, 60, "Жовтий",
                LocalDate.now(), "Цимбідіум", false);
        assertFalse(o.isEpiphytic());
    }

    @Test
    void testGetTypeName() {
        Orchid o = new Orchid("О", 10, 10, "Б", LocalDate.now(), "Ф", true);
        assertEquals("Орхідея", o.getTypeName());
    }

    @Test
    void testToString() {
        Orchid o = new Orchid("Орхідея Дендробіум", 180, 50, "Фіолетовий",
                LocalDate.now(), "Дендробіум", true);
        String str = o.toString();
        assertTrue(str.contains("Орхідея"));
        assertTrue(str.contains("(Дендробіум)"));
    }
}
