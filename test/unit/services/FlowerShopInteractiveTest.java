package services;

import flowers.Flower;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class FlowerShopInteractiveTest {

    private static final String TEST_DB = "test_flower_shop.db";
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;
    private FlowerShop shop;

    @org.junit.jupiter.api.BeforeAll
    static void initAll() {
        System.setProperty("test.db.url", "jdbc:sqlite:" + TEST_DB);
    }

    @BeforeEach
    void setUp() {
        new java.io.File(TEST_DB).delete();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    void testAddFlowerToCatalog() {
        // Вибираємо тип 1 (Троянда), назва "Інтерактивна Троянда", ціна 100, стебло 50, колір "Червоний"
        provideInput("1\nІнтерактивна Троянда\n100\n50\nЧервоний\n");
        shop = new FlowerShop();
        int initialSize = shop.getCatalog().size();

        shop.addFlowerToCatalog();

        assertEquals(initialSize + 1, shop.getCatalog().size());
        Flower f = shop.getCatalog().get(shop.getCatalog().size() - 1);
        assertEquals("Інтерактивна Троянда", f.getName());
    }

    @Test
    void testAddFlowerToCatalogDifferentTypes() {
        // Тип 2 (Тюльпан)
        provideInput("2\nТюльпан 2\n40\n30\nЖовтий\n");
        shop = new FlowerShop();
        shop.addFlowerToCatalog();
        assertTrue(outContent.toString().contains("Квітку додано."));
        
        // Тип 3 (Ромашка)
        provideInput("3\nРомашка 3\n30\n20\nБілий\n");
        shop = new FlowerShop(); // Re-init to use new scanner
        shop.addFlowerToCatalog();
        
        // Тип 4 (Лілія)
        provideInput("4\nЛілія 4\n50\n40\nРожевий\n");
        shop = new FlowerShop();
        shop.addFlowerToCatalog();
        
        // Тип 5 (Півонія)
        provideInput("5\nПівонія 5\n60\n45\nБілий\n");
        shop = new FlowerShop();
        shop.addFlowerToCatalog();
        
        // Тип 6 (Орхідея)
        provideInput("6\nОрхідея 6\n150\n50\nФіолетовий\n");
        shop = new FlowerShop();
        shop.addFlowerToCatalog();

        // Невірний тип -> за замовчуванням Троянда
        provideInput("999\nДефолтна Троянда\n100\n50\nЧервоний\n");
        shop = new FlowerShop();
        shop.addFlowerToCatalog();
        assertTrue(shop.getCatalog().get(shop.getCatalog().size() - 1).getName().contains("Дефолтна"));
    }

    @Test
    void testCreateBouquetInteractive() {
        provideInput("Мій Інтерактивний Букет\n");
        shop = new FlowerShop();
        int initialSize = shop.getBouquets().size();

        shop.createBouquet();

        assertEquals(initialSize + 1, shop.getBouquets().size());
        assertEquals("Мій Інтерактивний Букет", shop.getBouquets().get(shop.getBouquets().size() - 1).getName());
    }

    @Test
    void testViewAllBouquets() {
        shop = new FlowerShop();
        shop.createNewBouquet("Букет 1");
        shop.viewAllBouquets();
        assertTrue(outContent.toString().contains("Букет 1"));
    }

    @Test
    void testSortBouquetInteractive() {
        provideInput("1\n");
        shop = new FlowerShop();
        shop.createNewBouquet("Букет Для Сортування");
        
        shop.sortBouquet();
        
        assertTrue(outContent.toString().contains("Відсортовано."));
    }

    @Test
    void testSortBouquetEmpty() {
        shop = new FlowerShop();
        shop.getBouquets().clear();
        shop.sortBouquet(); // Не повинно нічого робити
        assertTrue(shop.getBouquets().isEmpty());
    }

    @Test
    void testEditBouquetInteractive() {
        FlowerShop tempShop = new FlowerShop();
        tempShop.createNewBouquet("Букет для Редагування");
        int bSize = tempShop.getBouquets().size();
        
        provideInput(bSize + "\n1\n2\n0\n1\nСтрічка\n10\n0\n");
        shop = new FlowerShop(); // Scanner now uses the provided input

        shop.editBouquet();

        assertEquals(2, shop.getBouquets().get(bSize - 1).getFlowers().size());
        assertEquals(1, shop.getBouquets().get(bSize - 1).getAccessories().size());
    }
    
    @Test
    void testEditBouquetEmpty() {
        shop = new FlowerShop();
        shop.getBouquets().clear();
        shop.editBouquet();
        assertTrue(outContent.toString().contains("Немає букетів."));
    }

    @Test
    void testReadIntAndDoubleWithInvalidInput() {
        // Вводимо неправильні дані для addFlowerToCatalog, потім правильні
        // Тип (invalid, 1), Назва ("Тест"), Ціна (invalid, 100), Стебло (invalid, 50), Колір ("Ч")
        provideInput("abc\n1\nТест\nabc\n100\nabc\n50\nЧ\n");
        shop = new FlowerShop();
        shop.addFlowerToCatalog();
        assertTrue(outContent.toString().contains("Квітку додано."));
    }
}
