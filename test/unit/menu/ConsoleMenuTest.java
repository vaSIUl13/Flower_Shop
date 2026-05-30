package menu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.FlowerShop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleMenuTest {

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
        shop = new FlowerShop();
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
    void testRunInvalidChoice() {
        // Пробуємо ввести неправильний вибір, а потім кінець потоку (EOF)
        provideInput("999\n");
        ConsoleMenu menu = new ConsoleMenu(shop);
        
        assertThrows(NoSuchElementException.class, menu::run);
        assertTrue(outContent.toString().contains("Невірний вибір."));
    }
    
    @Test
    void testRunStringChoice() {
        // Пробуємо ввести рядок замість числа
        provideInput("abc\n999\n");
        ConsoleMenu menu = new ConsoleMenu(shop);
        
        assertThrows(NoSuchElementException.class, menu::run);
    }

    @Test
    void testRunValidChoice() {
        // Вибираємо 1 (Переглянути каталог)
        provideInput("1\n");
        ConsoleMenu menu = new ConsoleMenu(shop);
        
        assertThrows(NoSuchElementException.class, menu::run);
        assertTrue(outContent.toString().contains("=== Каталог квітів ==="));
    }
}
