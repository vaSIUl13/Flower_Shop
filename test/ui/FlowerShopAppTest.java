package ui;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import services.FlowerShop;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Автоматизовані GUI-тести для FlowerShopApp з використанням TestFX.
 * Тестує навігацію, створення/видалення елементів, пошук.
 */
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
        // Перевіряємо що додаток запустився та показує кнопку каталогу
        verifyThat("🌹  Каталог", (Button btn) -> btn.isVisible());
    }

    @Test
    @Order(2)
    void testNavigateToBouquets(FxRobot robot) {
        robot.clickOn("💐  Букети");
        // Перевіряємо що панель букетів відобразилась
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

    // ==================== ТЕСТ ЗБЕРЕЖЕННЯ ====================

    @Test
    @Order(5)
    void testSaveButton(FxRobot robot) {
        robot.clickOn("💾  Зберегти");
        // Перевіряємо статус-бар
        verifyThat("✅ Усі дані збережено в базі даних SQLite автоматично!",
                (Label label) -> label.isVisible());
    }

    // ==================== ТЕСТИ КАТАЛОГУ ====================

    @Test
    @Order(10)
    void testCatalogTableVisible(FxRobot robot) {
        // Каталог повинен мати таблицю з квітами
        TableView<?> table = robot.lookup(".table-view").queryAs(TableView.class);
        assertNotNull(table);
        assertTrue(table.getItems().size() > 0, "Каталог не повинен бути порожнім");
    }

    @Test
    @Order(11)
    void testAddFlowerDialogOpens(FxRobot robot) {
        // Натискаємо кнопку "Додати квітку"
        robot.clickOn("＋  Додати квітку");
        // Перевіряємо що діалог відкрився
        DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogPane, "Діалог додавання квітки повинен відкритися");
        // Закриваємо діалог за допомогою клавіатури, щоб не залежати від локалізації тексту кнопки
        robot.type(KeyCode.ESCAPE);
    }

    @Test
    @Order(12)
    void testAddFlowerThroughDialog(FxRobot robot) {
        TableView<?> table = robot.lookup(".table-view").queryAs(TableView.class);
        int initialCount = table.getItems().size();

        robot.clickOn("＋  Додати квітку");

        // Заповнюємо поля
        robot.clickOn(".text-field").write("Тестова Троянда");
        // Переходимо до поля ціни (другий TextField)
        TextField priceField = robot.lookup(".text-field").nth(1).queryAs(TextField.class);
        robot.clickOn(priceField).write("99");
        TextField stemField = robot.lookup(".text-field").nth(2).queryAs(TextField.class);
        robot.clickOn(stemField).write("50");

        // Натискаємо "Додати"
        robot.clickOn("Додати");

        // Перевіряємо що квітка додалась
        assertEquals(initialCount + 1, table.getItems().size());
    }

    @Test
    @Order(13)
    void testDeleteFlowerWithoutSelection(FxRobot robot) {
        // Натискаємо видалити без вибору — має з'явитися попередження
        robot.clickOn("🗑  Видалити");
        DialogPane alert = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(alert, "Попередження повинно з'явитися");
        robot.clickOn("OK");
    }

    @Test
    @Order(14)
    void testSelectAndDeleteFlower(FxRobot robot) {
        // Спочатку додаємо квітку для видалення
        robot.clickOn("＋  Додати квітку");
        robot.clickOn(".text-field").write("Видалити Мене");
        TextField priceField = robot.lookup(".text-field").nth(1).queryAs(TextField.class);
        robot.clickOn(priceField).write("10");
        TextField stemField = robot.lookup(".text-field").nth(2).queryAs(TextField.class);
        robot.clickOn(stemField).write("20");
        robot.clickOn("Додати");

        TableView<?> table = robot.lookup(".table-view").queryAs(TableView.class);
        int sizeAfterAdd = table.getItems().size();

        // Вибираємо останній рядок
        robot.interact(() -> {
            table.getSelectionModel().select(sizeAfterAdd - 1);
        });

        // Видаляємо
        robot.clickOn("🗑  Видалити");
        assertEquals(sizeAfterAdd - 1, table.getItems().size());
    }

    // ==================== ТЕСТИ БУКЕТІВ ====================

    @Test
    @Order(20)
    void testCreateBouquet(FxRobot robot) {
        robot.clickOn("💐  Букети");

        robot.clickOn("＋  Створити букет");
        // Вводимо назву в діалог
        robot.write("Тестовий Букет GUI");
        robot.clickOn("OK");

        // Перевіряємо що букет з'явився в списку
        ListView<?> list = robot.lookup(".list-view").queryAs(ListView.class);
        assertNotNull(list);
        assertTrue(list.getItems().size() > 0);
    }

    @Test
    @Order(21)
    void testDeleteBouquetWithoutSelection(FxRobot robot) {
        robot.clickOn("💐  Букети");
        robot.clickOn("🗑  Видалити букет");
        // Повинно з'явитися попередження
        DialogPane alert = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(alert);
        robot.clickOn("OK");
    }

    // ==================== ТЕСТИ ПОШУКУ ====================

    @Test
    @Order(30)
    void testSearchPanelVisible(FxRobot robot) {
        robot.clickOn("🔍  Пошук");
        verifyThat("🔍  Пошук квітів", (Label label) -> label.isVisible());
    }

    @Test
    @Order(31)
    void testSearchWithoutBouquetSelected(FxRobot robot) {
        robot.clickOn("🔍  Пошук");
        // Заповнюємо поля пошуку
        TextField minField = robot.lookup(".text-field").nth(0).queryAs(TextField.class);
        TextField maxField = robot.lookup(".text-field").nth(1).queryAs(TextField.class);
        robot.clickOn(minField).write("10");
        robot.clickOn(maxField).write("100");
        robot.clickOn("🔍  Шукати");

        // Повинно з'явитися попередження
        DialogPane alert = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(alert);
        robot.clickOn("OK");
    }

    @Test
    @Order(32)
    void testSearchWithInvalidInput(FxRobot robot) {
        robot.clickOn("🔍  Пошук");

        // Вибираємо букет (якщо є)
        ComboBox<?> combo = robot.lookup(".combo-box").queryAs(ComboBox.class);
        if (combo != null && !combo.getItems().isEmpty()) {
            robot.clickOn(combo);
            robot.type(KeyCode.DOWN);
            robot.type(KeyCode.ENTER);

            // Вводимо невалідні дані
            TextField minField = robot.lookup(".text-field").nth(0).queryAs(TextField.class);
            robot.clickOn(minField).write("abc");
            TextField maxField = robot.lookup(".text-field").nth(1).queryAs(TextField.class);
            robot.clickOn(maxField).write("xyz");
            robot.clickOn("🔍  Шукати");

            // Попередження
            DialogPane alert = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
            assertNotNull(alert);
            robot.clickOn("OK");
        }
    }
}
