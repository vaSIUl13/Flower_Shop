package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.FlowerShop;

/**
 * Головний клас JavaFX-додатку «Квітковий Магазин».
 * Забезпечує бокову навігацію та перемикання між панелями.
 */
public class FlowerShopApp extends Application {

    private FlowerShop shop;
    private StackPane contentArea;
    private Label statusLabel;

    private CatalogPane catalogPane;
    private BouquetPane bouquetPane;
    private SearchPane searchPane;

    private Button activeButton;

    @Override
    public void start(Stage stage) {
        shop = new FlowerShop();

        BorderPane root = new BorderPane();

        // Бокова панель
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Область контенту
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);

        // Статус-бар
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);

        // Створення панелей
        catalogPane = new CatalogPane(shop, this::setStatus);
        bouquetPane = new BouquetPane(shop, this::setStatus);
        searchPane = new SearchPane(shop, this::setStatus);

        // Показати каталог за замовчуванням
        showCatalog(null);

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("\uD83C\uDF38 Квітковий Магазин");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    /* ================ Бокова панель ================ */

    private VBox createSidebar() {
        // Логотип
        Label logo = new Label("\uD83C\uDF3C Квітковий");
        logo.getStyleClass().add("sidebar-logo");
        Label logoSub = new Label("М А Г А З И Н");
        logoSub.getStyleClass().add("sidebar-logo-sub");
        VBox logoBox = new VBox(2, logo, logoSub);
        logoBox.getStyleClass().add("sidebar-header");
        logoBox.setAlignment(Pos.CENTER);

        Region sep1 = new Region();
        sep1.getStyleClass().add("sidebar-separator");

        // Кнопки навігації
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
            shop.saveCatalogToFile();
            setStatus("✅ Усі дані збережено в базі даних SQLite автоматично!");
        });

        // Spacer
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

    /* ================ Навігація ================ */

    private void showCatalog(Button btn) {
        catalogPane.refresh();
        contentArea.getChildren().setAll(catalogPane);
        if (btn != null) setActiveButton(btn);
    }

    private void showBouquets(Button btn) {
        bouquetPane.refresh();
        contentArea.getChildren().setAll(bouquetPane);
        setActiveButton(btn);
    }

    private void showSearch(Button btn) {
        searchPane.refresh();
        contentArea.getChildren().setAll(searchPane);
        setActiveButton(btn);
    }

    /* ================ Статус-бар ================ */

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
