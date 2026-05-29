package flowers;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Chamomile.
 */
class ChamomileTest {

    @Test
    void testConstructorAndGetters() {
        Chamomile c = new Chamomile("Ромашка Польова", 25, 25, "Білий",
                LocalDate.of(2026, 5, 19), 1.5, 18);
        assertEquals("Ромашка Польова", c.getName());
        assertEquals(25, c.getPrice());
        assertEquals(25, c.getStemLength());
        assertEquals("Білий", c.getColor());
        assertEquals(1.5, c.getCoreSize(), 0.001);
        assertEquals(18, c.getPetalCount());
    }

    @Test
    void testGetTypeName() {
        Chamomile c = new Chamomile("Р", 10, 10, "Б", LocalDate.now(), 1.0, 12);
        assertEquals("Ромашка", c.getTypeName());
    }

    @Test
    void testToString() {
        Chamomile c = new Chamomile("Ромашка Садова", 30, 35, "Білий",
                LocalDate.now(), 2.0, 24);
        String str = c.toString();
        assertTrue(str.contains("Ромашка"));
    }
}
