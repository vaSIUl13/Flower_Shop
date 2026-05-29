package flowers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Accessory.
 */
class AccessoryTest {

    @Test
    void testConstructorWithColor() {
        Accessory acc = new Accessory("Стрічка атласна", 15, "Червоний");
        assertEquals("Стрічка атласна", acc.getName());
        assertEquals(15, acc.getPrice());
        assertEquals("Червоний", acc.getColor());
    }

    @Test
    void testConstructorWithoutColor() {
        Accessory acc = new Accessory("Обгортковий папір", 35);
        assertEquals("Обгортковий папір", acc.getName());
        assertEquals(35, acc.getPrice());
        assertEquals("", acc.getColor());
    }

    @Test
    void testIdDefaultAndSetter() {
        Accessory acc = new Accessory("Бантик", 25, "Рожевий");
        assertEquals(-1, acc.getId());
        acc.setId(10);
        assertEquals(10, acc.getId());
    }

    @Test
    void testToStringWithColor() {
        Accessory acc = new Accessory("Стрічка", 15, "Білий");
        String str = acc.toString();
        assertTrue(str.contains("Стрічка"));
        assertTrue(str.contains("Білий"));
        assertTrue(str.contains("15.0 грн"));
    }

    @Test
    void testToStringWithoutColor() {
        Accessory acc = new Accessory("Папір", 30);
        String str = acc.toString();
        assertTrue(str.contains("Папір"));
        assertTrue(str.contains("30.0 грн"));
        // Не повинен містити ", " перед ціною, бо кольору немає
        assertFalse(str.contains(", ("));
    }

    @Test
    void testToStringNullColor() {
        Accessory acc = new Accessory("Тест", 10, null);
        String str = acc.toString();
        assertNotNull(str);
        assertTrue(str.contains("Тест"));
    }
}
