package flowers;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Lily.
 */
class LilyTest {

    @Test
    void testConstructorAndGetters() {
        Lily lily = new Lily("Лілія Касабланка", 120, 70, "Білий",
                LocalDate.of(2026, 5, 20), "Інтенсивний", 6);
        assertEquals("Лілія Касабланка", lily.getName());
        assertEquals(120, lily.getPrice());
        assertEquals(70, lily.getStemLength());
        assertEquals("Білий", lily.getColor());
        assertEquals("Інтенсивний", lily.getFragrance());
        assertEquals(6, lily.getPetalCount());
    }

    @Test
    void testGetTypeName() {
        Lily lily = new Lily("Л", 10, 10, "Б", LocalDate.now(), "Легкий", 4);
        assertEquals("Лілія", lily.getTypeName());
    }

    @Test
    void testToString() {
        Lily lily = new Lily("Лілія Стар", 130, 75, "Рожевий",
                LocalDate.now(), "Солодкий", 6);
        String str = lily.toString();
        assertTrue(str.contains("Лілія"));
        assertTrue(str.contains("аромат: Солодкий"));
    }
}
