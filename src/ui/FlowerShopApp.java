package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.FlowerShop;

public class FlowerShopApp extends Application {

    private static final Logger logger = LogManager.getLogger(FlowerShopApp.class);
    private FlowerShop shop;
    private StackPane contentArea;
    private Label statusLabel;

    private CatalogPane catalogPane;
    private BouquetPane bouquetPane;
    private SearchPane searchPane;

    private Button activeButton;

    @Override
    public void start(Stage stage) {
        logger.info("Запуск JavaFX-додатку Квітковий Магазин...");


        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.fatal("Критична необроблена помилка у потоці '" + thread.getName() + "'", throwable);
        });

        try {
            shop = new FlowerShop();
            logger.info("Сервіс FlowerShop ініціалізовано успішно.");
        } catch (Exception e) {
            logger.fatal("Не вдалося ініціалізувати FlowerShop!", e);
            throw e;
        }

        BorderPane root = new BorderPane();


        VBox sidebar = createSidebar();
        root.setLeft(sidebar);


        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);


        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);


        catalogPane = new CatalogPane(shop, this::setStatus);
        bouquetPane = new BouquetPane(shop, this::setStatus);
        searchPane = new SearchPane(shop, this::setStatus);
        logger.info("UI-панелі (Каталог, Букети, Пошук) створено.");


        showCatalog(null);

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("\uD83C\uDF38 Квітковий Магазин");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();


        stage.setOnCloseRequest(event -> {
            logger.info("Користувач закрив додаток. Завершення роботи.");
        });

        logger.info("Додаток запущено та готовий до роботи.");
    }



    private VBox createSidebar() {

        Label logo = new Label("\uD83C\uDF3C Квітковий");
        logo.getStyleClass().add("sidebar-logo");
        Label logoSub = new Label("М А Г А З И Н");
        logoSub.getStyleClass().add("sidebar-logo-sub");
        VBox logoBox = new VBox(2, logo, logoSub);
        logoBox.getStyleClass().add("sidebar-header");
        logoBox.setAlignment(Pos.CENTER);

        Region sep1 = new Region();
        sep1.getStyleClass().add("sidebar-separator");


        Button btnCatalog = createSidebarButton("\uD83C\uDF39  Каталог");
        btnCatalog.setOnAction(e -> showCatalog(btnCatalog));
        activeButton = btnCatalog;
        btnCatalog.getStyleClass().add("sidebar-btn-active");

        Button btnBouquets = createSidebarButton("\uD83D\uDC90  Букети");
        btnBouquets.setOnAction(e -> showBouquets(btnBouquets));

        Button btnSearch = createSidebarButton("\uD83D\uDD0D  Пошук");
        btnSearch.setOnAction(e -> showSearch(btnSearch));

        Region sep2 = new Region();
        sep2.getStyleClass().add("sidebar-separator");

        Button btnSave = createSidebarButton("\uD83D\uDCBE  Зберегти");
        btnSave.setOnAction(e -> {
            logger.info("Користувач натиснув 'Зберегти'.");
            shop.saveCatalogToFile();
            setStatus("✅ Усі дані збережено в базі даних SQLite автоматично!");
        });


        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox sidebar = new VBox(0, logoBox, sep1,
                btnCatalog, btnBouquets, btnSearch,
                spacer, sep2, btnSave);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void setActiveButton(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-btn-active");
        }
        activeButton = btn;
        if (btn != null) {
            btn.getStyleClass().add("sidebar-btn-active");
        }
    }



    private void showCatalog(Button btn) {
        logger.info("Навігація: перехід до панелі 'Каталог'.");
        catalogPane.refresh();
        contentArea.getChildren().setAll(catalogPane);
        if (btn != null) setActiveButton(btn);
    }

    private void showBouquets(Button btn) {
        logger.info("Навігація: перехід до панелі 'Букети'.");
        bouquetPane.refresh();
        contentArea.getChildren().setAll(bouquetPane);
        setActiveButton(btn);
    }

    private void showSearch(Button btn) {
        logger.info("Навігація: перехід до панелі 'Пошук'.");
        searchPane.refresh();
        contentArea.getChildren().setAll(searchPane);
        setActiveButton(btn);
    }



    private HBox createStatusBar() {
        statusLabel = new Label("Ласкаво просимо до Квіткового Магазину! \uD83C\uDF3B");
        statusLabel.getStyleClass().add("status-label");

        HBox bar = new HBox(statusLabel);
        bar.getStyleClass().add("status-bar");
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
