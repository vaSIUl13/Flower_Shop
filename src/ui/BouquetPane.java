package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.FlowerShop;
import flowers.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Панель управління букетами — створення, редагування, сортування.
 */
public class BouquetPane extends VBox {

    private static final Logger logger = LogManager.getLogger(BouquetPane.class);
    private final FlowerShop shop;
    private final Consumer<String> statusCallback;

    private ListView<String> bouquetList;
    private ObservableList<String> bouquetNames;
    private FilteredList<String> filteredBouquetNames;
    private TextField searchField;

    private TableView<Flower> flowersTable;
    private TableView<Accessory> accessoriesTable;
    private Label priceLabel;
    private Label detailTitle;
    private VBox detailBox;

    // Предвизначені аксесуари: {назва, ціна}
    private static final String[][] PREDEFINED_ACCESSORIES = {
            {"Стрічка атласна", "15"},
            {"Стрічка органза", "20"},
            {"Стрічка \"З любов'ю\"", "18"},
            {"Бантик малий", "25"},
            {"Бантик великий", "40"},
            {"Обгортковий папір", "35"},
            {"Крафт-папір", "30"},
            {"Целофан прозорий", "20"},
            {"Сітка флористична", "40"},
            {"Фетр декоративний", "45"},
            {"Кошик плетений", "80"},
            {"Коробка подарункова", "120"},
            {"Листівка", "15"},
            {"Шпильки декоративні", "10"},
            {"Перлини декоративні", "30"},
            {"Зелень декоративна", "25"},
            {"Блискітки", "12"},
            {"Піропатрон (бенгальський вогонь)", "35"},
    };

    // Кольори для аксесуарів
    private static final String[][] ACC_COLORS = {
            {"Червоний", "#E53935"}, {"Рожевий", "#EC407A"}, {"Білий", "#FAFAFA"},
            {"Жовтий", "#FDD835"}, {"Помаранчевий", "#FB8C00"}, {"Фіолетовий", "#8E24AA"},
            {"Синій", "#1E88E5"}, {"Бірюзовий", "#26A69A"}, {"Кремовий", "#FFF8E1"},
            {"Бордовий", "#880E4F"}, {"Золотий", "#FFD700"}, {"Срібний", "#C0C0C0"},
            {"Зелений", "#43A047"}, {"Чорний", "#333333"},
    };

    public BouquetPane(FlowerShop shop, Consumer<String> statusCallback) {
        this.shop = shop;
        this.statusCallback = statusCallback;

        setSpacing(20);
        setPadding(new Insets(30));
        getStyleClass().add("panel");

        // Заголовок
        Label title = new Label("\uD83D\uDC90  Управління букетами");
        title.getStyleClass().add("panel-title");
        Label subtitle = new Label("Створюйте та наповнюйте букети з квітів каталогу");
        subtitle.getStyleClass().add("panel-subtitle");
        VBox header = new VBox(4, title, subtitle);

        // Основний контент — SplitPane
        HBox content = createContent();
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(header, content);
        refresh();
    }

    private HBox createContent() {
        // Ліва панель — список букетів
        VBox leftPanel = createBouquetListPanel();
        leftPanel.setMinWidth(220);
        leftPanel.setPrefWidth(240);

        // Права панель — деталі букету
        detailBox = createDetailPanel();
        HBox.setHgrow(detailBox, Priority.ALWAYS);

        HBox content = new HBox(20, leftPanel, detailBox);
        return content;
    }

    private VBox createBouquetListPanel() {
        Label listTitle = new Label("Список букетів");
        listTitle.getStyleClass().add("section-title");

        // Поле пошуку букетів
        searchField = new TextField();
        searchField.setPromptText("\uD83D\uDD0D  Пошук букету...");
        searchField.getStyleClass().add("search-field");
        searchField.setMaxWidth(Double.MAX_VALUE);

        bouquetList = new ListView<>();
        bouquetNames = FXCollections.observableArrayList();
        filteredBouquetNames = new FilteredList<>(bouquetNames, s -> true);
        bouquetList.setItems(filteredBouquetNames);
        bouquetList.setPlaceholder(new Label("Ще немає букетів"));
        VBox.setVgrow(bouquetList, Priority.ALWAYS);

        // Фільтрація при введенні тексту
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.trim().toLowerCase();
            if (filter.isEmpty()) {
                filteredBouquetNames.setPredicate(s -> true);
                bouquetList.setPlaceholder(new Label("Ще немає букетів"));
            } else {
                filteredBouquetNames.setPredicate(s -> s.toLowerCase().contains(filter));
                bouquetList.setPlaceholder(new Label("Нічого не знайдено"));
            }
            logger.debug("Фільтр букетів: '" + newVal + "', знайдено: " + filteredBouquetNames.size());
        });

        bouquetList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Знаходимо реальний індекс у повному списку
                int realIndex = bouquetNames.indexOf(newVal);
                showBouquetDetails(realIndex);
            } else {
                showBouquetDetails(-1);
            }
        });

        Button createBtn = new Button("＋  Створити букет");
        createBtn.getStyleClass().add("btn-primary");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> createBouquet());

        Button renameBtn = new Button("✏  Перейменувати");
        renameBtn.getStyleClass().add("btn-secondary");
        renameBtn.setMaxWidth(Double.MAX_VALUE);
        renameBtn.setOnAction(e -> renameBouquet());

        Button deleteBtn = new Button("\uD83D\uDDD1  Видалити букет");
        deleteBtn.getStyleClass().addAll("btn-danger", "btn-small");
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setOnAction(e -> deleteBouquet());

        VBox panel = new VBox(10, listTitle, searchField, bouquetList, createBtn, renameBtn, deleteBtn);
        panel.getStyleClass().add("card");
        return panel;
    }

    private VBox createDetailPanel() {
        detailTitle = new Label("Оберіть букет зі списку");
        detailTitle.getStyleClass().add("section-title");

        // Таблиця квітів у букеті
        Label flowersLabel = new Label("Квіти:");
        flowersLabel.getStyleClass().add("section-title");

        flowersTable = new TableView<>();
        flowersTable.setPlaceholder(new Label("Букет порожній"));
        flowersTable.setPrefHeight(200);

        TableColumn<Flower, String> fNameCol = new TableColumn<>("Назва");
        fNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        fNameCol.setPrefWidth(160);

        TableColumn<Flower, String> fTypeCol = new TableColumn<>("Тип");
        fTypeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTypeName()));
        fTypeCol.setPrefWidth(90);

        TableColumn<Flower, String> fPriceCol = new TableColumn<>("Ціна");
        fPriceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f грн", d.getValue().getPrice())));
        fPriceCol.setPrefWidth(90);

        TableColumn<Flower, String> fColorCol = new TableColumn<>("Колір");
        fColorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getColor()));
        fColorCol.setPrefWidth(80);

        flowersTable.getColumns().addAll(fNameCol, fTypeCol, fPriceCol, fColorCol);
        flowersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Таблиця аксесуарів
        Label accLabel = new Label("Аксесуари:");
        accLabel.getStyleClass().add("section-title");

        accessoriesTable = new TableView<>();
        accessoriesTable.setPlaceholder(new Label("Немає аксесуарів"));
        accessoriesTable.setPrefHeight(120);

        TableColumn<Accessory, String> aNameCol = new TableColumn<>("Назва");
        aNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        aNameCol.setPrefWidth(160);

        TableColumn<Accessory, String> aColorCol = new TableColumn<>("Колір");
        aColorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getColor()));
        aColorCol.setPrefWidth(90);
        aColorCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null || color.isEmpty()) {
                    setText(null); setGraphic(null);
                } else {
                    Rectangle rect = new Rectangle(12, 12);
                    rect.setArcWidth(3); rect.setArcHeight(3);
                    rect.setFill(accColorToFx(color));
                    rect.setStroke(Color.web("#ccc"));
                    HBox box = new HBox(5, rect, new Label(color));
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box); setText(null);
                }
            }
        });

        TableColumn<Accessory, String> aPriceCol = new TableColumn<>("Ціна");
        aPriceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f грн", d.getValue().getPrice())));
        aPriceCol.setPrefWidth(80);

        accessoriesTable.getColumns().addAll(aNameCol, aColorCol, aPriceCol);
        accessoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Кнопки дій
        Button addFlowerBtn = new Button("＋ Квітку");
        addFlowerBtn.getStyleClass().add("btn-primary");
        addFlowerBtn.setOnAction(e -> addFlowerToBouquet());

        Button removeFlowerBtn = new Button("－ Квітку");
        removeFlowerBtn.getStyleClass().addAll("btn-danger", "btn-small");
        removeFlowerBtn.setOnAction(e -> removeFlowerFromBouquet());

        Button addAccBtn = new Button("＋ Аксесуар");
        addAccBtn.getStyleClass().add("btn-secondary");
        addAccBtn.setOnAction(e -> addAccessoryToBouquet());

        Button removeAccBtn = new Button("－ Аксесуар");
        removeAccBtn.getStyleClass().addAll("btn-danger", "btn-small");
        removeAccBtn.setOnAction(e -> removeAccessoryFromBouquet());

        Button sortBtn = new Button("⇅  Сортувати за свіжістю");
        sortBtn.getStyleClass().add("btn-secondary");
        sortBtn.setOnAction(e -> sortBouquet());

        HBox actionRow1 = new HBox(10, addFlowerBtn, removeFlowerBtn, addAccBtn, removeAccBtn);
        actionRow1.setAlignment(Pos.CENTER_LEFT);

        HBox actionRow2 = new HBox(10, sortBtn);
        actionRow2.setAlignment(Pos.CENTER_LEFT);

        // Ціна
        priceLabel = new Label("Загальна вартість: 0.00 грн");
        priceLabel.getStyleClass().add("price-label");

        VBox detail = new VBox(12, detailTitle, flowersLabel, flowersTable, accLabel, accessoriesTable,
                actionRow1, actionRow2, priceLabel);
        detail.getStyleClass().add("card");
        return detail;
    }

    private Bouquet getSelectedBouquet() {
        String selectedName = bouquetList.getSelectionModel().getSelectedItem();
        if (selectedName != null) {
            int realIdx = bouquetNames.indexOf(selectedName);
            if (realIdx >= 0 && realIdx < shop.getBouquets().size()) {
                return shop.getBouquets().get(realIdx);
            }
        }
        return null;
    }

    private void showBouquetDetails(int index) {
        if (index < 0 || index >= shop.getBouquets().size()) {
            detailTitle.setText("Оберіть букет зі списку");
            flowersTable.setItems(FXCollections.observableArrayList());
            accessoriesTable.setItems(FXCollections.observableArrayList());
            priceLabel.setText("Загальна вартість: 0.00 грн");
            return;
        }
        Bouquet b = shop.getBouquets().get(index);
        detailTitle.setText("Букет: \"" + b.getName() + "\"");
        flowersTable.setItems(FXCollections.observableArrayList(b.getFlowers()));
        accessoriesTable.setItems(FXCollections.observableArrayList(b.getAccessories()));
        priceLabel.setText(String.format("Загальна вартість: %.2f грн", b.calculateTotalPrice()));
    }

    private void createBouquet() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Новий букет");
        dialog.setHeaderText("Створення нового букету");
        dialog.setContentText("Назва букету:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                shop.createNewBouquet(name.trim());
                refresh();
                bouquetList.getSelectionModel().selectLast();
                logger.info("Створено новий букет: \"" + name.trim() + "\" через UI.");
                statusCallback.accept("✅ Букет \"" + name.trim() + "\" створено");
            }
        });
    }

    private void renameBouquet() {
        Bouquet b = getSelectedBouquet();
        if (b == null) { showWarning("Спочатку оберіть букет для перейменування"); return; }

        TextInputDialog dialog = new TextInputDialog(b.getName());
        dialog.setTitle("Перейменувати букет");
        dialog.setHeaderText("Редагування назви букету");
        dialog.setContentText("Нова назва:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (!newName.trim().isEmpty() && !newName.trim().equals(b.getName())) {
                String oldName = b.getName();
                shop.renameBouquet(b, newName.trim());
                refresh();
                // Вибрати перейменований букет у списку
                bouquetList.getSelectionModel().select(newName.trim());
                logger.info("Перейменовано букет: \"" + oldName + "\" → \"" + newName.trim() + "\" через UI.");
                statusCallback.accept("✏ Букет перейменовано: \"" + oldName + "\" → \"" + newName.trim() + "\"");
            }
        });
    }

    private void deleteBouquet() {
        String selectedName = bouquetList.getSelectionModel().getSelectedItem();
        if (selectedName == null) {
            showWarning("Оберіть букет для видалення");
            return;
        }
        int realIdx = bouquetNames.indexOf(selectedName);
        if (realIdx < 0) return;
        shop.removeBouquet(realIdx);
        refresh();
        logger.info("Видалено букет: \"" + selectedName + "\" через UI.");
        statusCallback.accept("\uD83D\uDDD1 Букет \"" + selectedName + "\" видалено");
    }

    private void addFlowerToBouquet() {
        Bouquet b = getSelectedBouquet();
        if (b == null) { showWarning("Спочатку оберіть букет"); return; }

        List<Flower> catalog = shop.getCatalog();
        if (catalog.isEmpty()) { showWarning("Каталог порожній! Спочатку додайте квіти до каталогу."); return; }

        ChoiceDialog<Flower> dialog = new ChoiceDialog<>(catalog.get(0), catalog);
        dialog.setTitle("Додати квітку");
        dialog.setHeaderText("Оберіть квітку з каталогу");
        dialog.setContentText("Квітка:");

        Optional<Flower> result = dialog.showAndWait();
        result.ifPresent(flower -> {
            // Запитати кількість
            TextInputDialog qtyDialog = new TextInputDialog("1");
            qtyDialog.setTitle("Кількість");
            qtyDialog.setHeaderText("Скільки додати?");
            qtyDialog.setContentText("Кількість:");
            Optional<String> qtyResult = qtyDialog.showAndWait();
            qtyResult.ifPresent(qtyStr -> {
                try {
                    int qty = Integer.parseInt(qtyStr.trim());
                    for (int i = 0; i < qty; i++) {
                        shop.addFlowerToBouquet(b, flower);
                    }
                    refreshDetails();
                    logger.info("Додано " + qty + " шт. \"" + flower.getName() + "\" до букету \"" + b.getName() + "\".");
                    statusCallback.accept("✅ Додано " + qty + " шт. \"" + flower.getName() + "\" до букету");
                } catch (NumberFormatException ex) {
                    logger.warn("Невірний формат кількості при додаванні квітки до букету: '" + qtyStr + "'");
                    showWarning("Невірне число!");
                }
            });
        });
    }

    private void removeFlowerFromBouquet() {
        Bouquet b = getSelectedBouquet();
        if (b == null) { showWarning("Спочатку оберіть букет"); return; }

        Flower selected = flowersTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showWarning("Оберіть квітку для видалення"); return; }

        int idx = b.getFlowers().indexOf(selected);
        shop.removeFlowerFromBouquet(b, idx);
        refreshDetails();
        logger.info("Видалено квітку \"" + selected.getName() + "\" з букету \"" + b.getName() + "\".");
        statusCallback.accept("🗑 Квітку видалено з букету");
    }

    private void addAccessoryToBouquet() {
        Bouquet b = getSelectedBouquet();
        if (b == null) { showWarning("Спочатку оберіть букет"); return; }

        Dialog<Accessory> dialog = new Dialog<>();
        dialog.setTitle("Додати аксесуар");
        dialog.setHeaderText("Оберіть аксесуар та колір");
        dialog.setResizable(true);
        ButtonType addType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        // Комбобокс з предвизначеними аксесуарами
        ComboBox<String> accBox = new ComboBox<>();
        for (String[] a : PREDEFINED_ACCESSORIES) {
            accBox.getItems().add(a[0] + "  —  " + a[1] + " грн");
        }
        accBox.setValue(accBox.getItems().get(0));
        accBox.setMaxWidth(Double.MAX_VALUE);

        // Поле ціни — автозаповнення, тільки для читання
        Label priceDisplay = new Label(PREDEFINED_ACCESSORIES[0][1] + " грн");
        priceDisplay.getStyleClass().add("price-label");

        accBox.setOnAction(e -> {
            int idx = accBox.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                priceDisplay.setText(PREDEFINED_ACCESSORIES[idx][1] + " грн");
            }
        });

        // Комбобокс кольору з кольоровими маркерами
        ComboBox<String> colorBox = new ComboBox<>();
        for (String[] c : ACC_COLORS) colorBox.getItems().add(c[0]);
        colorBox.setValue("Білий");
        colorBox.setMaxWidth(Double.MAX_VALUE);
        colorBox.setCellFactory(lv -> createAccColorCell());
        colorBox.setButtonCell(createAccColorCell());

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Аксесуар:"), 0, 0);  grid.add(accBox, 1, 0);
        grid.add(new Label("Колір:"), 0, 1);      grid.add(colorBox, 1, 1);
        grid.add(new Label("Ціна:"), 0, 2);       grid.add(priceDisplay, 1, 2);

        ColumnConstraints cc1 = new ColumnConstraints(90);
        ColumnConstraints cc2 = new ColumnConstraints(280);
        grid.getColumnConstraints().addAll(cc1, cc2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(430);

        dialog.setResultConverter(btn -> {
            if (btn == addType) {
                int idx = accBox.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    String name = PREDEFINED_ACCESSORIES[idx][0];
                    double price = Double.parseDouble(PREDEFINED_ACCESSORIES[idx][1]);
                    String color = colorBox.getValue();
                    return new Accessory(name, price, color);
                }
            }
            return null;
        });

        Optional<Accessory> result = dialog.showAndWait();
        result.ifPresent(acc -> {
            shop.addAccessoryToBouquet(b, acc);
            refreshDetails();
            logger.info("Додано аксесуар \"" + acc.getName() + "\" (" + acc.getColor() + ") до букету \"" + b.getName() + "\".");
            statusCallback.accept("✅ " + acc.getName() + " (" + acc.getColor() + ") додано до букету");
        });
    }

    private void removeAccessoryFromBouquet() {
        Bouquet b = getSelectedBouquet();
        if (b == null) { showWarning("Спочатку оберіть букет"); return; }

        Accessory selected = accessoriesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showWarning("Оберіть аксесуар для видалення"); return; }

        int idx = b.getAccessories().indexOf(selected);
        shop.removeAccessoryFromBouquet(b, idx);
        refreshDetails();
        logger.info("Видалено аксесуар \"" + selected.getName() + "\" з букету \"" + b.getName() + "\".");
        statusCallback.accept("🗑 Аксесуар видалено");
    }

    private void sortBouquet() {
        Bouquet b = getSelectedBouquet();
        if (b == null) { showWarning("Спочатку оберіть букет"); return; }
        b.sortFlowersByFreshness();
        refreshDetails();
        logger.info("Квіти у букеті \"" + b.getName() + "\" відсортовано за свіжістю.");
        statusCallback.accept("⇅ Квіти у букеті \"" + b.getName() + "\" відсортовано за свіжістю");
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setTitle("Увага");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void refreshDetails() {
        String selectedName = bouquetList.getSelectionModel().getSelectedItem();
        if (selectedName != null) {
            int realIdx = bouquetNames.indexOf(selectedName);
            showBouquetDetails(realIdx);
        } else {
            showBouquetDetails(-1);
        }
    }

    public void refresh() {
        bouquetNames.clear();
        for (Bouquet b : shop.getBouquets()) {
            bouquetNames.add(b.getName());
        }
        // Зберігаємо фільтр після оновлення
        String filter = searchField.getText();
        if (filter != null && !filter.trim().isEmpty()) {
            String f = filter.trim().toLowerCase();
            filteredBouquetNames.setPredicate(s -> s.toLowerCase().contains(f));
        } else {
            filteredBouquetNames.setPredicate(s -> true);
        }
        String selectedName = bouquetList.getSelectionModel().getSelectedItem();
        if (selectedName != null) {
            int realIdx = bouquetNames.indexOf(selectedName);
            showBouquetDetails(realIdx);
        } else {
            showBouquetDetails(-1);
        }
    }

    // ==================== Допоміжні методи кольорів ====================

    private ListCell<String> createAccColorCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                } else {
                    Rectangle rect = new Rectangle(14, 14);
                    rect.setArcWidth(4); rect.setArcHeight(4);
                    rect.setFill(accColorToFx(item));
                    rect.setStroke(Color.web("#ccc"));
                    HBox box = new HBox(6, rect, new Label(item));
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box); setText(null);
                }
            }
        };
    }

    private Color accColorToFx(String name) {
        for (String[] c : ACC_COLORS) {
            if (c[0].equals(name)) return Color.web(c[1]);
        }
        return Color.LIGHTGRAY;
    }
}
