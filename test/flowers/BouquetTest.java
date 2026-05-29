package flowers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для класу Bouquet.
 */
class BouquetTest {

    private Bouquet bouquet;
    private Rose rose;
    private Tulip tulip;
    private Chamomile chamomile;

    @BeforeEach
    void setUp() {
        bouquet = new Bouquet("Весняний");
        rose = new Rose("Троянда Гран-Прі", 80, 60, "Червоний",
                LocalDate.of(2026, 5, 20), true, "Келих");
        tulip = new Tulip("Тюльпан Голд", 40, 35, "Жовтий",
                LocalDate.of(2026, 5, 18), "Овальна", false);
        chamomile = new Chamomile("Ромашка Польова", 25, 25, "Білий",
                LocalDate.of(2026, 5, 19), 1.5, 18);
    }

    @Test
    void testConstructorAndName() {
        assertEquals("Весняний", bouquet.getName());
    }

    @Test
    void testIdDefaultAndSetter() {
        assertEquals(-1, bouquet.getId());
        bouquet.setId(5);
        assertEquals(5, bouquet.getId());
    }

    @Test
    void testAddAndGetFlowers() {
        assertTrue(bouquet.getFlowers().isEmpty());
        bouquet.addFlower(rose);
        bouquet.addFlower(tulip);
        assertEquals(2, bouquet.getFlowers().size());
        assertEquals(rose, bouquet.getFlowers().get(0));
        assertEquals(tulip, bouquet.getFlowers().get(1));
    }

    @Test
    void testAddAndGetAccessories() {
        assertTrue(bouquet.getAccessories().isEmpty());
        Accessory acc1 = new Accessory("Стрічка", 15, "Червоний");
        Accessory acc2 = new Accessory("Папір", 30);
        bouquet.addAccessory(acc1);
        bouquet.addAccessory(acc2);
        assertEquals(2, bouquet.getAccessories().size());
    }

    @Test
    void testCalculateTotalPriceEmptyBouquet() {
        assertEquals(0, bouquet.calculateTotalPrice());
    }

    @Test
    void testCalculateTotalPriceWithFlowersOnly() {
        bouquet.addFlower(rose);  // 80
        bouquet.addFlower(tulip); // 40
        assertEquals(120, bouquet.calculateTotalPrice(), 0.01);
    }

    @Test
    void testCalculateTotalPriceWithFlowersAndAccessories() {
        bouquet.addFlower(rose);       // 80
        bouquet.addFlower(tulip);      // 40
        bouquet.addAccessory(new Accessory("Стрічка", 15, "Білий")); // 15
        bouquet.addAccessory(new Accessory("Папір", 30));             // 30
        assertEquals(165, bouquet.calculateTotalPrice(), 0.01);
    }

    @Test
    void testSortFlowersByFreshness() {
        // Додаємо квіти з різними датами свіжості
        bouquet.addFlower(tulip);      // 2026-05-18 (найстаріша)
        bouquet.addFlower(chamomile);  // 2026-05-19
        bouquet.addFlower(rose);       // 2026-05-20 (найсвіжіша)

        bouquet.sortFlowersByFreshness(); // За спаданням (найсвіжіші перші)

        assertEquals(rose, bouquet.getFlowers().get(0));       // 2026-05-20
        assertEquals(chamomile, bouquet.getFlowers().get(1));  // 2026-05-19
        assertEquals(tulip, bouquet.getFlowers().get(2));      // 2026-05-18
    }

    @Test
    void testFindFlowersByStemLength() {
        bouquet.addFlower(rose);       // 60 см
        bouquet.addFlower(tulip);      // 35 см
        bouquet.addFlower(chamomile);  // 25 см

        // Пошук квітів зі стеблом 30-65 см
        List<Flower> result = bouquet.findFlowersByStemLength(30, 65);
        assertEquals(2, result.size());
        assertTrue(result.contains(rose));
        assertTrue(result.contains(tulip));
    }

    @Test
    void testFindFlowersByStemLengthNoResults() {
        bouquet.addFlower(rose);       // 60 см
        bouquet.addFlower(tulip);      // 35 см

        List<Flower> result = bouquet.findFlowersByStemLength(100, 200);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindFlowersByStemLengthExactBounds() {
        bouquet.addFlower(rose);  // 60 см
        List<Flower> result = bouquet.findFlowersByStemLength(60, 60);
        assertEquals(1, result.size());
    }

    @Test
    void testRemoveFlowerValidIndex() {
        bouquet.addFlower(rose);
        bouquet.addFlower(tulip);
        bouquet.removeFlower(0);
        assertEquals(1, bouquet.getFlowers().size());
        assertEquals(tulip, bouquet.getFlowers().get(0));
    }

    @Test
    void testRemoveFlowerInvalidIndex() {
        bouquet.addFlower(rose);
        bouquet.removeFlower(-1);  // Невалідний
        bouquet.removeFlower(5);   // Невалідний
        assertEquals(1, bouquet.getFlowers().size());
    }

    @Test
    void testRemoveAccessoryValidIndex() {
        Accessory acc = new Accessory("Стрічка", 15);
        bouquet.addAccessory(acc);
        bouquet.removeAccessory(0);
        assertTrue(bouquet.getAccessories().isEmpty());
    }

    @Test
    void testRemoveAccessoryInvalidIndex() {
        Accessory acc = new Accessory("Стрічка", 15);
        bouquet.addAccessory(acc);
        bouquet.removeAccessory(-1);
        bouquet.removeAccessory(5);
        assertEquals(1, bouquet.getAccessories().size());
    }

    @Test
    void testToString() {
        bouquet.addFlower(rose);
        String str = bouquet.toString();
        assertTrue(str.contains("Весняний"));
        assertTrue(str.contains("Квітів: 1"));
    }

    @Test
    void testToStringEmpty() {
        String str = bouquet.toString();
        assertTrue(str.contains("Квітів: 0"));
    }
}
