package flowers;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FlowerTest {

    @Test
    void testGetName() {
        Rose flower = new Rose("Тест", 100, 50, "Білий", LocalDate.now(), true, "Келих");
        assertEquals("Тест", flower.getName());
    }

    @Test
    void testGetPrice() {
        Rose flower = new Rose("Тест", 150.50, 50, "Білий", LocalDate.now(), true, "Келих");
        assertEquals(150.50, flower.getPrice(), 0.01);
    }

    @Test
    void testGetStemLength() {
        Rose flower = new Rose("Тест", 100, 65.5, "Білий", LocalDate.now(), true, "Келих");
        assertEquals(65.5, flower.getStemLength(), 0.01);
    }

    @Test
    void testGetColor() {
        Rose flower = new Rose("Тест", 100, 50, "Червоний", LocalDate.now(), true, "Келих");
        assertEquals("Червоний", flower.getColor());
    }

    @Test
    void testGetFreshnessDate() {
        LocalDate date = LocalDate.of(2026, 5, 15);
        Rose flower = new Rose("Тест", 100, 50, "Білий", date, true, "Келих");
        assertEquals(date, flower.getFreshnessDate());
    }

    @Test
    void testIdDefaultMinusOne() {
        Rose flower = new Rose("Тест", 100, 50, "Білий", LocalDate.now(), true, "Келих");
        assertEquals(-1, flower.getId());
    }

    @Test
    void testSetId() {
        Rose flower = new Rose("Тест", 100, 50, "Білий", LocalDate.now(), true, "Келих");
        flower.setId(100);
        assertEquals(100, flower.getId());
    }

    @Test
    void testToStringFormat() {
        Rose flower = new Rose("Моя Троянда", 85.50, 55, "Рожевий",
                LocalDate.now(), true, "Келих");
        String str = flower.toString();
        assertTrue(str.contains("Моя Троянда"));
        assertTrue(str.contains("85,50") || str.contains("85.50"));
        assertTrue(str.contains("Рожевий"));
    }

    @Test
    void testIsSerializable() {
        Rose flower = new Rose("Тест", 100, 50, "Білий", LocalDate.now(), true, "Келих");
        assertTrue(flower instanceof java.io.Serializable);
    }

    @Test
    void testAllFlowerTypesHaveTypeName() {
        LocalDate d = LocalDate.now();
        assertEquals("Троянда", new Rose("Р", 10, 10, "Ч", d, true, "К").getTypeName());
        assertEquals("Тюльпан", new Tulip("Т", 10, 10, "Ч", d, "О", false).getTypeName());
        assertEquals("Ромашка", new Chamomile("Р", 10, 10, "Б", d, 1.0, 12).getTypeName());
        assertEquals("Лілія", new Lily("Л", 10, 10, "Б", d, "Л", 6).getTypeName());
        assertEquals("Півонія", new Peony("П", 10, 10, "Р", d, "В", true).getTypeName());
        assertEquals("Орхідея", new Orchid("О", 10, 10, "Б", d, "Ф", true).getTypeName());
    }
}
