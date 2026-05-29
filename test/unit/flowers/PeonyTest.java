package flowers;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Peony.
 */
class PeonyTest {

    @Test
    void testConstructorAndGetters() {
        Peony p = new Peony("Півонія Сара Бернар", 150, 50, "Рожевий",
                LocalDate.of(2026, 5, 20), "Відкритий", true);
        assertEquals("Півонія Сара Бернар", p.getName());
        assertEquals(150, p.getPrice());
        assertEquals(50, p.getStemLength());
        assertEquals("Рожевий", p.getColor());
        assertEquals("Відкритий", p.getBloomStage());
        assertTrue(p.isFragrant());
    }

    @Test
    void testNotFragrant() {
        Peony p = new Peony("П", 100, 40, "Б", LocalDate.now(), "Бутон", false);
        assertFalse(p.isFragrant());
    }

    @Test
    void testGetTypeName() {
        Peony p = new Peony("П", 10, 10, "Б", LocalDate.now(), "Бутон", true);
        assertEquals("Півонія", p.getTypeName());
    }

    @Test
    void testToString() {
        Peony p = new Peony("Півонія Ред", 155, 48, "Червоний",
                LocalDate.now(), "Бутон", true);
        String str = p.toString();
        assertTrue(str.contains("Півонія"));
        assertTrue(str.contains("[Бутон]"));
    }
}
