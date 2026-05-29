package services;

import flowers.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тести для сервісу FlowerShop.
 * Перевіряє логіку додавання, видалення, створення букетів тощо.
 */
class FlowerShopTest {

    private FlowerShop shop;

    @BeforeEach
    void setUp() {
        shop = new FlowerShop();
    }

    // ==================== КАТАЛОГ ====================

    @Test
    void testCatalogNotEmpty() {
        // FlowerShop ініціалізує каталог, якщо він порожній
        assertFalse(shop.getCatalog().isEmpty());
    }

    @Test
    void testAddFlower() {
        int initialSize = shop.getCatalog().size();
        Rose rose = new Rose("Нова Троянда", 100, 70, "Червоний",
                LocalDate.now(), true, "Келих");
        shop.addFlower(rose);
        assertEquals(initialSize + 1, shop.getCatalog().size());
        assertTrue(rose.getId() > 0, "Квітка повинна отримати ID з БД");
    }

    @Test
    void testRemoveFlowerValidIndex() {
        int initialSize = shop.getCatalog().size();
        shop.removeFlower(0);
        assertEquals(initialSize - 1, shop.getCatalog().size());
    }

    @Test
    void testRemoveFlowerInvalidNegativeIndex() {
        int initialSize = shop.getCatalog().size();
        shop.removeFlower(-1);
        assertEquals(initialSize, shop.getCatalog().size());
    }

    @Test
    void testRemoveFlowerInvalidLargeIndex() {
        int initialSize = shop.getCatalog().size();
        shop.removeFlower(999);
        assertEquals(initialSize, shop.getCatalog().size());
    }

    // ==================== БУКЕТИ ====================

    @Test
    void testCreateNewBouquet() {
        int initialSize = shop.getBouquets().size();
        Bouquet b = shop.createNewBouquet("Тестовий Букет");
        assertNotNull(b);
        assertEquals("Тестовий Букет", b.getName());
        assertTrue(b.getId() > 0);
        assertEquals(initialSize + 1, shop.getBouquets().size());
    }

    @Test
    void testRemoveBouquetValidIndex() {
        shop.createNewBouquet("Для Видалення");
        int sizeAfterAdd = shop.getBouquets().size();
        shop.removeBouquet(sizeAfterAdd - 1);
        assertEquals(sizeAfterAdd - 1, shop.getBouquets().size());
    }

    @Test
    void testRemoveBouquetInvalidIndex() {
        int initialSize = shop.getBouquets().size();
        shop.removeBouquet(-1);
        shop.removeBouquet(999);
        assertEquals(initialSize, shop.getBouquets().size());
    }

    @Test
    void testRenameBouquet() {
        Bouquet b = shop.createNewBouquet("До Перейменування");
        assertNotNull(b);
        shop.renameBouquet(b, "Після Перейменування");
        assertEquals("Після Перейменування", b.getName());

        List<Bouquet> bouquets = shop.getBouquets();
        Bouquet loaded = bouquets.stream()
                .filter(bq -> bq.getId() == b.getId())
                .findFirst().orElse(null);
        assertNotNull(loaded);
        assertEquals("Після Перейменування", loaded.getName());
    }

    // ==================== КВІТИ В БУКЕТІ ====================

    @Test
    void testAddFlowerToBouquet() {
        Bouquet b = shop.createNewBouquet("Букет З Квіткою");
        Rose rose = new Rose("Букетна Троянда", 80, 60, "Ч",
                LocalDate.now(), true, "Келих");
        shop.addFlower(rose);

        shop.addFlowerToBouquet(b, rose);
        assertEquals(1, b.getFlowers().size());
        assertEquals("Букетна Троянда", b.getFlowers().get(0).getName());
    }

    @Test
    void testRemoveFlowerFromBouquetValidIndex() {
        Bouquet b = shop.createNewBouquet("Букет Видалення");
        Flower flower = shop.getCatalog().get(0);
        shop.addFlowerToBouquet(b, flower);
        assertEquals(1, b.getFlowers().size());

        shop.removeFlowerFromBouquet(b, 0);
        assertTrue(b.getFlowers().isEmpty());
    }

    @Test
    void testRemoveFlowerFromBouquetInvalidIndex() {
        Bouquet b = shop.createNewBouquet("Букет Невалідний");
        Flower flower = shop.getCatalog().get(0);
        shop.addFlowerToBouquet(b, flower);

        shop.removeFlowerFromBouquet(b, -1);
        shop.removeFlowerFromBouquet(b, 5);
        assertEquals(1, b.getFlowers().size());
    }

    // ==================== АКСЕСУАРИ ====================

    @Test
    void testAddAccessoryToBouquet() {
        Bouquet b = shop.createNewBouquet("Букет З Аксесуаром");
        Accessory acc = new Accessory("Стрічка", 15, "Білий");
        shop.addAccessoryToBouquet(b, acc);

        assertEquals(1, b.getAccessories().size());
        assertEquals("Стрічка", b.getAccessories().get(0).getName());
        assertTrue(acc.getId() > 0);
    }

    @Test
    void testRemoveAccessoryFromBouquetValidIndex() {
        Bouquet b = shop.createNewBouquet("Букет Акс Видалення");
        Accessory acc = new Accessory("Бантик", 25, "Рожевий");
        shop.addAccessoryToBouquet(b, acc);

        shop.removeAccessoryFromBouquet(b, 0);
        assertTrue(b.getAccessories().isEmpty());
    }

    @Test
    void testRemoveAccessoryFromBouquetInvalidIndex() {
        Bouquet b = shop.createNewBouquet("Букет Акс Невалідний");
        Accessory acc = new Accessory("Папір", 30);
        shop.addAccessoryToBouquet(b, acc);

        shop.removeAccessoryFromBouquet(b, -1);
        shop.removeAccessoryFromBouquet(b, 5);
        assertEquals(1, b.getAccessories().size());
    }

    // ==================== ДОПОМІЖНІ ====================

    @Test
    void testSaveCatalogToFile() {
        // Метод просто логує; не повинен кидати виняток
        assertDoesNotThrow(() -> shop.saveCatalogToFile());
    }

    @Test
    void testGetCatalogReturnsList() {
        List<Flower> catalog = shop.getCatalog();
        assertNotNull(catalog);
    }

    @Test
    void testGetBouquetsReturnsList() {
        List<Bouquet> bouquets = shop.getBouquets();
        assertNotNull(bouquets);
    }

    @Test
    void testMultipleFlowersInOneBouquet() {
        Bouquet b = shop.createNewBouquet("Великий Букет");
        List<Flower> catalog = shop.getCatalog();
        assertTrue(catalog.size() >= 3, "Каталог повинен мати щонайменше 3 квітки");

        shop.addFlowerToBouquet(b, catalog.get(0));
        shop.addFlowerToBouquet(b, catalog.get(1));
        shop.addFlowerToBouquet(b, catalog.get(2));

        assertEquals(3, b.getFlowers().size());
    }

    @Test
    void testAddMultipleAccessoriesToBouquet() {
        Bouquet b = shop.createNewBouquet("Аксесуарний Букет");
        shop.addAccessoryToBouquet(b, new Accessory("Стрічка", 15, "Білий"));
        shop.addAccessoryToBouquet(b, new Accessory("Папір", 30));
        shop.addAccessoryToBouquet(b, new Accessory("Бантик", 25, "Рожевий"));

        assertEquals(3, b.getAccessories().size());
    }
}
