package ui;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlowerShopAppTest {

    private FlowerShopApp app;

    @Start
    private void start(Stage stage) {
        app = new FlowerShopApp();
        app.start(stage);
    }

    // ==================== ТЕСТИ НАВІГАЦІЇ ====================

    @Test
    @Order(1)
    void testApplicationStarts(FxRobot robot) {
        verifyThat("🌹  Каталог", (Button btn) -> btn.isVisible());
    }

    @Test
    @Order(2)
    void testNavigateToBouquets(FxRobot robot) {
        robot.clickOn("💐  Букети");
        verifyThat("💐  Управління букетами", (Label label) -> label.isVisible());
    }

    @Test
    @Order(3)
    void testNavigateToSearch(FxRobot robot) {
        robot.clickOn("🔍  Пошук");
        verifyThat("🔍  Пошук квітів", (Label label) -> label.isVisible());
    }

    @Test
    @Order(4)
    void testNavigateBackToCatalog(FxRobot robot) {
        robot.clickOn("🔍  Пошук");
        robot.clickOn("🌹  Каталог");
        verifyThat("🌹  Каталог квітів", (Label label) -> label.isVisible());
    }

    // ==================== ТЕСТИ КАТАЛОГУ ТА ФІЛЬТРІВ ====================

    @Test
    @Order(10)
    void testAddFlowerThroughDialog(FxRobot robot) {
        TableView<?> table = robot.lookup(".table-view").queryAs(TableView.class);
        int initialCount = table.getItems().size();

        robot.clickOn("＋  Додати квітку");

        // Обмежуємо пошук текстових полів лише всередині відкритого діалогу
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        
        TextField nameField = robot.from(dialogPane).lookup(".text-field").nth(0).queryAs(TextField.class);
        TextField priceField = robot.from(dialogPane).lookup(".text-field").nth(1).queryAs(TextField.class);
        TextField stemField = robot.from(dialogPane).lookup(".text-field").nth(2).queryAs(TextField.class);

        // Заповнюємо поля
        robot.clickOn(nameField).write("Тестова Троянда");
        robot.clickOn(priceField).write("99");
        robot.clickOn(stemField).write("50");

        robot.clickOn("Додати");

        assertEquals(initialCount + 1, table.getItems().size());
    }

    @Test
    @Order(11)
    void testCatalogNameFilter(FxRobot robot) {
        TableView<?> table = robot.lookup(".table-view").queryAs(TableView.class);
        
        // Знаходимо поле пошуку за prompt text або CSS
        TextField searchField = robot.lookup(".search-field").queryAs(TextField.class);
        
        robot.clickOn(searchField).write("Тестова Троянда");
        
        // Має лишитися хоча б одна або скільки завгодно, головне перевірити фільтрацію
        assertTrue(table.getItems().size() > 0);
        
        robot.clickOn(searchField).eraseText(15); // очищаємо
    }

    @Test
    @Order(12)
    void testCatalogResetFilters(FxRobot robot) {
        TableView<?> table = robot.lookup(".table-view").queryAs(TableView.class);
        int allCount = table.getItems().size();
        
        // Змінюємо фільтри, натискаючи на лінк "Зняти все"
        javafx.scene.Node link = robot.lookup(".filter-link").nth(1).query();
        robot.clickOn(link);
        
        // Таблиця повинна очиститись або змінити розмір
        assertTrue(table.getItems().size() <= allCount);
        
        // Кнопка скидання може бути за межами видимості ScrollPane,
        // тому викликаємо її програмно через interact
        Button resetBtn = robot.lookup(".btn-reset").queryAs(Button.class);
        robot.interact(resetBtn::fire);
        
        // Перевіряємо, що розмір відновився
        assertEquals(allCount, table.getItems().size());
    }

    @Test
    @Order(13)
    void testDeleteFlower(FxRobot robot) {
        TableView<?> table = robot.lookup(".table-view").queryAs(TableView.class);
        int sizeBefore = table.getItems().size();
        
        // Вибираємо останній рядок
        robot.interact(() -> {
            table.getSelectionModel().select(sizeBefore - 1);
        });

        robot.clickOn("🗑  Видалити");
        assertEquals(sizeBefore - 1, table.getItems().size());
    }

    // ==================== ТЕСТИ БУКЕТІВ ====================

    @Test
    @Order(20)
    void testCreateBouquet(FxRobot robot) {
        robot.clickOn("💐  Букети");

        robot.clickOn("＋  Створити букет");
        robot.write("GUI Букет");
        robot.clickOn("OK");

        @SuppressWarnings("unchecked")
        ListView<String> list = (ListView<String>) robot.lookup(".list-view").queryAs(ListView.class);
        assertNotNull(list);
        assertTrue(list.getItems().size() > 0);
    }

    @Test
    @Order(21)
    void testRenameBouquet(FxRobot robot) {
        robot.clickOn("💐  Букети");
        @SuppressWarnings("unchecked")
        ListView<String> list = (ListView<String>) robot.lookup(".list-view").queryAs(ListView.class);
        
        robot.interact(() -> {
            list.getSelectionModel().select("GUI Букет");
        });

        robot.clickOn("✏  Перейменувати");
        robot.eraseText(9); // очищаємо "GUI Букет"
        robot.write("Перейменований Букет");
        robot.clickOn("OK");

        assertTrue(list.getItems().contains("Перейменований Букет"));
    }

    @Test
    @Order(22)
    void testSearchBouquet(FxRobot robot) {
        robot.clickOn("💐  Букети");
        @SuppressWarnings("unchecked")
        ListView<String> list = (ListView<String>) robot.lookup(".list-view").queryAs(ListView.class);
        
        // Поле пошуку букету має клас search-field
        TextField searchField = robot.lookup(".search-field").queryAs(TextField.class);
        robot.clickOn(searchField).write("Перейменований");
        
        assertEquals(1, list.getItems().size());
        
        robot.clickOn(searchField).eraseText(15);
    }

    @Test
    @Order(23)
    void testDeleteBouquet(FxRobot robot) {
        robot.clickOn("💐  Букети");
        @SuppressWarnings("unchecked")
        ListView<String> list = (ListView<String>) robot.lookup(".list-view").queryAs(ListView.class);
        
        robot.interact(() -> {
            list.getSelectionModel().select("Перейменований Букет");
        });

        int sizeBefore = list.getItems().size();
        robot.clickOn("🗑  Видалити букет");
        
        assertEquals(sizeBefore - 1, list.getItems().size());
    }

    // ==================== ТЕСТИ ПОШУКУ (Розділ) ====================

    @Test
    @Order(30)
    void testSearchWithoutBouquetSelected(FxRobot robot) {
        robot.clickOn("🔍  Пошук");
        TextField minField = robot.lookup(".text-field").nth(0).queryAs(TextField.class);
        TextField maxField = robot.lookup(".text-field").nth(1).queryAs(TextField.class);
        robot.clickOn(minField).write("10");
        robot.clickOn(maxField).write("100");
        robot.clickOn("🔍  Шукати");

        DialogPane alert = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(alert);
        robot.clickOn("OK");
    }
}
