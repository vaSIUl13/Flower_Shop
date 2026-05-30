package database;

import flowers.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseManagerTest {

    private static DatabaseManager db;
    private static final String TEST_DB = "test_flower_shop.db";

    @BeforeEach
    void setUp() {
        new File(TEST_DB).delete();
        db = new TestDatabaseManager();
    }

    @AfterEach
    void tearDown() {
        new File(TEST_DB).delete();
    }

    static class TestDatabaseManager extends DatabaseManager {
        @Override
        protected String getDbUrl() {
            return "jdbc:sqlite:" + TEST_DB;
        }
    }



    @Test
    void testInsertAndGetRose() {
        Rose rose = new Rose("Тест Троянда", 80, 60, "Червоний",
                LocalDate.of(2026, 5, 20), true, "Келих");
        int id = db.insertFlower(rose);
        assertTrue(id > 0, "ID має бути позитивним після вставки");
        assertEquals(id, rose.getId());

        List<Flower> flowers = db.getAllFlowers();
        boolean found = flowers.stream().anyMatch(f ->
                f.getName().equals("Тест Троянда") && f instanceof Rose);
        assertTrue(found, "Троянда повинна бути в списку");
    }

    @Test
    void testInsertAndGetTulip() {
        Tulip tulip = new Tulip("Тест Тюльпан", 45, 40, "Жовтий",
                LocalDate.of(2026, 5, 20), "Овальна", false);
        int id = db.insertFlower(tulip);
        assertTrue(id > 0);

        List<Flower> flowers = db.getAllFlowers();
        boolean found = flowers.stream().anyMatch(f ->
                f.getName().equals("Тест Тюльпан") && f instanceof Tulip);
        assertTrue(found);
    }

    @Test
    void testInsertAndGetChamomile() {
        Chamomile ch = new Chamomile("Тест Ромашка", 25, 25, "Білий",
                LocalDate.of(2026, 5, 19), 1.5, 18);
        int id = db.insertFlower(ch);
        assertTrue(id > 0);

        List<Flower> flowers = db.getAllFlowers();
        boolean found = flowers.stream().anyMatch(f ->
                f.getName().equals("Тест Ромашка") && f instanceof Chamomile);
        assertTrue(found);
    }

    @Test
    void testInsertAndGetLily() {
        Lily lily = new Lily("Тест Лілія", 120, 70, "Білий",
                LocalDate.of(2026, 5, 20), "Інтенсивний", 6);
        int id = db.insertFlower(lily);
        assertTrue(id > 0);

        List<Flower> flowers = db.getAllFlowers();
        boolean found = flowers.stream().anyMatch(f ->
                f.getName().equals("Тест Лілія") && f instanceof Lily);
        assertTrue(found);
    }

    @Test
    void testInsertAndGetPeony() {
        Peony peony = new Peony("Тест Півонія", 150, 50, "Рожевий",
                LocalDate.of(2026, 5, 20), "Відкритий", true);
        int id = db.insertFlower(peony);
        assertTrue(id > 0);

        List<Flower> flowers = db.getAllFlowers();
        boolean found = flowers.stream().anyMatch(f ->
                f.getName().equals("Тест Півонія") && f instanceof Peony);
        assertTrue(found);
    }

    @Test
    void testInsertAndGetOrchid() {
        Orchid orchid = new Orchid("Тест Орхідея", 200, 45, "Білий",
                LocalDate.of(2026, 5, 20), "Фаленопсіс", true);
        int id = db.insertFlower(orchid);
        assertTrue(id > 0);

        List<Flower> flowers = db.getAllFlowers();
        boolean found = flowers.stream().anyMatch(f ->
                f.getName().equals("Тест Орхідея") && f instanceof Orchid);
        assertTrue(found);
    }

    @Test
    void testDeleteFlower() {
        Rose rose = new Rose("Видалити Мене", 50, 30, "Білий",
                LocalDate.now(), false, "Класичний");
        int id = db.insertFlower(rose);
        assertTrue(id > 0);

        db.deleteFlower(id);

        List<Flower> flowers = db.getAllFlowers();
        boolean found = flowers.stream().anyMatch(f -> f.getName().equals("Видалити Мене"));
        assertFalse(found, "Квітка повинна бути видалена");
    }



    @Test
    void testInsertAndGetBouquet() {
        Bouquet bouquet = new Bouquet("Тест Букет");
        int id = db.insertBouquet(bouquet);
        assertTrue(id > 0);
        assertEquals(id, bouquet.getId());

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        boolean found = bouquets.stream().anyMatch(b -> b.getName().equals("Тест Букет"));
        assertTrue(found);
    }

    @Test
    void testDeleteBouquet() {
        Bouquet bouquet = new Bouquet("Видалити Букет");
        int id = db.insertBouquet(bouquet);
        assertTrue(id > 0);

        db.deleteBouquet(id);

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        boolean found = bouquets.stream().anyMatch(b -> b.getName().equals("Видалити Букет"));
        assertFalse(found);
    }

    @Test
    void testUpdateBouquetName() {
        Bouquet bouquet = new Bouquet("Стара Назва");
        int id = db.insertBouquet(bouquet);
        assertTrue(id > 0);

        db.updateBouquetName(id, "Нова Назва");

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        Bouquet loaded = bouquets.stream()
                .filter(b -> b.getId() == id)
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals("Нова Назва", loaded.getName());
    }



    @Test
    void testAddFlowerToBouquet() {
        Rose rose = new Rose("Букетна Троянда", 80, 60, "Червоний",
                LocalDate.now(), true, "Келих");
        db.insertFlower(rose);

        Bouquet bouquet = new Bouquet("Букет з Трояндою");
        db.insertBouquet(bouquet);

        db.addFlowerToBouquet(bouquet.getId(), rose.getId());

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        Bouquet loaded = bouquets.stream()
                .filter(b -> b.getName().equals("Букет з Трояндою"))
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals(1, loaded.getFlowers().size());
        assertEquals("Букетна Троянда", loaded.getFlowers().get(0).getName());
    }

    @Test
    void testRemoveFlowerFromBouquet() {
        Rose rose = new Rose("Видалити з Букету", 80, 60, "Червоний",
                LocalDate.now(), true, "Келих");
        db.insertFlower(rose);

        Bouquet bouquet = new Bouquet("Букет Тест");
        db.insertBouquet(bouquet);
        db.addFlowerToBouquet(bouquet.getId(), rose.getId());

        db.removeOneFlowerFromBouquet(bouquet.getId(), rose.getId());

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        Bouquet loaded = bouquets.stream()
                .filter(b -> b.getName().equals("Букет Тест"))
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertTrue(loaded.getFlowers().isEmpty());
    }



    @Test
    void testInsertAndLoadAccessory() {
        Bouquet bouquet = new Bouquet("Букет з Аксесуаром");
        db.insertBouquet(bouquet);

        Accessory acc = new Accessory("Стрічка", 15, "Червоний");
        int accId = db.insertAccessory(bouquet.getId(), acc);
        assertTrue(accId > 0);

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        Bouquet loaded = bouquets.stream()
                .filter(b -> b.getName().equals("Букет з Аксесуаром"))
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals(1, loaded.getAccessories().size());
        assertEquals("Стрічка", loaded.getAccessories().get(0).getName());
        assertEquals("Червоний", loaded.getAccessories().get(0).getColor());
    }

    @Test
    void testInsertAccessoryWithoutColor() {
        Bouquet bouquet = new Bouquet("Букет Без Кольору");
        db.insertBouquet(bouquet);

        Accessory acc = new Accessory("Папір", 30);
        int accId = db.insertAccessory(bouquet.getId(), acc);
        assertTrue(accId > 0);

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        Bouquet loaded = bouquets.stream()
                .filter(b -> b.getName().equals("Букет Без Кольору"))
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals("", loaded.getAccessories().get(0).getColor());
    }

    @Test
    void testDeleteAccessory() {
        Bouquet bouquet = new Bouquet("Букет Видалити Акс");
        db.insertBouquet(bouquet);

        Accessory acc = new Accessory("Бантик", 25, "Рожевий");
        db.insertAccessory(bouquet.getId(), acc);
        assertTrue(acc.getId() > 0);

        db.deleteAccessory(acc.getId());

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        Bouquet loaded = bouquets.stream()
                .filter(b -> b.getName().equals("Букет Видалити Акс"))
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertTrue(loaded.getAccessories().isEmpty());
    }



    @Test
    void testInitDatabaseCreatesTablesIdempotent() {
        assertDoesNotThrow(() -> db.initDatabase());
    }

    @Test
    void testGetAllFlowersEmptyDatabase() {
        List<Flower> flowers = db.getAllFlowers();
        assertNotNull(flowers);
    }

    @Test
    void testGetAllBouquetsEmptyDatabase() {
        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        assertNotNull(bouquets);
    }



    @Test
    void testRoseSpecificFieldsPersisted() {
        Rose rose = new Rose("Деталі Троянди", 80, 60, "Червоний",
                LocalDate.of(2026, 5, 20), true, "Бокаловидний");
        db.insertFlower(rose);

        List<Flower> flowers = db.getAllFlowers();
        Rose loaded = flowers.stream()
                .filter(f -> f.getName().equals("Деталі Троянди"))
                .map(f -> (Rose) f)
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertTrue(loaded.isHasThorns());
        assertEquals("Бокаловидний", loaded.getBudShape());
    }

    @Test
    void testTulipSpecificFieldsPersisted() {
        Tulip tulip = new Tulip("Деталі Тюльпана", 50, 40, "Фіолетовий",
                LocalDate.of(2026, 5, 20), "Бахромчата", true);
        db.insertFlower(tulip);

        List<Flower> flowers = db.getAllFlowers();
        Tulip loaded = flowers.stream()
                .filter(f -> f.getName().equals("Деталі Тюльпана"))
                .map(f -> (Tulip) f)
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals("Бахромчата", loaded.getPetalShape());
        assertTrue(loaded.isDouble());
    }

    @Test
    void testLilySpecificFieldsPersisted() {
        Lily lily = new Lily("Деталі Лілії", 120, 70, "Білий",
                LocalDate.of(2026, 5, 20), "Солодкий", 8);
        db.insertFlower(lily);

        List<Flower> flowers = db.getAllFlowers();
        Lily loaded = flowers.stream()
                .filter(f -> f.getName().equals("Деталі Лілії"))
                .map(f -> (Lily) f)
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals("Солодкий", loaded.getFragrance());
        assertEquals(8, loaded.getPetalCount());
    }

    @Test
    void testPeonySpecificFieldsPersisted() {
        Peony peony = new Peony("Деталі Півонії", 150, 50, "Рожевий",
                LocalDate.of(2026, 5, 20), "Напіввідкритий", true);
        db.insertFlower(peony);

        List<Flower> flowers = db.getAllFlowers();
        Peony loaded = flowers.stream()
                .filter(f -> f.getName().equals("Деталі Півонії"))
                .map(f -> (Peony) f)
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals("Напіввідкритий", loaded.getBloomStage());
        assertTrue(loaded.isFragrant());
    }

    @Test
    void testOrchidSpecificFieldsPersisted() {
        Orchid orchid = new Orchid("Деталі Орхідеї", 200, 45, "Білий",
                LocalDate.of(2026, 5, 20), "Дендробіум", false);
        db.insertFlower(orchid);

        List<Flower> flowers = db.getAllFlowers();
        Orchid loaded = flowers.stream()
                .filter(f -> f.getName().equals("Деталі Орхідеї"))
                .map(f -> (Orchid) f)
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals("Дендробіум", loaded.getVariety());
        assertFalse(loaded.isEpiphytic());
    }

    @Test
    void testMultipleFlowersInBouquet() {
        Rose rose = new Rose("Р1", 80, 60, "Ч", LocalDate.now(), true, "К");
        Tulip tulip = new Tulip("Т1", 40, 35, "Ж", LocalDate.now(), "О", false);
        db.insertFlower(rose);
        db.insertFlower(tulip);

        Bouquet bouquet = new Bouquet("Мікс");
        db.insertBouquet(bouquet);
        db.addFlowerToBouquet(bouquet.getId(), rose.getId());
        db.addFlowerToBouquet(bouquet.getId(), tulip.getId());

        List<Bouquet> bouquets = db.getAllBouquetsWithContents();
        Bouquet loaded = bouquets.stream()
                .filter(b -> b.getName().equals("Мікс"))
                .findFirst().orElse(null);

        assertNotNull(loaded);
        assertEquals(2, loaded.getFlowers().size());
    }

    @Test
    void testAccessoryWithNullColor() {
        Bouquet bouquet = new Bouquet("Букет null");
        db.insertBouquet(bouquet);

        Accessory acc = new Accessory("Тест", 10, null);
        int id = db.insertAccessory(bouquet.getId(), acc);
        assertTrue(id > 0);
    }
}
