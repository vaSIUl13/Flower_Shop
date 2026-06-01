package com.flowershop.controller;

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
import com.flowershop.service.FlowerShopService;
import com.flowershop.model.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BouquetPane extends VBox {

    private static final Logger logger = LogManager.getLogger(BouquetPane.class);
    private final FlowerShopService shop;
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

    private static final String[][] ACC_COLORS = {
            {"Червоний", "#E53935"}, {"Рожевий", "#EC407A"}, {"Білий", "#FAFAFA"},
            {"Жовтий", "#FDD835"}, {"Помаранчевий", "#FB8C00"}, {"Фіолетовий", "#8E24AA"},
            {"Синій", "#1E88E5"}, {"Бірюзовий", "#26A69A"}, {"Кремовий", "#FFF8E1"},
            {"Бордовий", "#880E4F"}, {"Золотий", "#FFD700"}, {"Срібний", "#C0C0C0"},
            {"Зелений", "#43A047"}, {"Чорний", "#333333"},
    };

    public BouquetPane(FlowerShopService shop, Consumer<String> statusCallback) {
        this.shop = shop;
        this.statusCallback = statusCallback;

        setSpacing(20);
        setPadding(new Insets(30));
        getStyleClass().add("panel");

        Label title = new Label("\uD83D\uDC90  Управління букетами");
        title.getStyleClass().add("panel-title");
        Label subtitle = new Label("Створюйте та наповнюйте букети з квітів каталогу");
        subtitle.getStyleClass().add("panel-subtitle");
        VBox header = new VBox(4, title, subtitle);

        HBox content = createContent();
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(header, content);
        refresh();
    }

    private HBox createContent() {
        VBox leftPanel = createBouquetListPanel();
        leftPanel.setMinWidth(220);
        leftPanel.setPrefWidth(240);

        detailBox = createDetailPanel();
        HBox.setHgrow(detailBox, Priority.ALWAYS);

        HBox content = new HBox(20, leftPanel, detailBox);
        return content;
    }

    private VBox createBouquetListPanel() {
        Label listTitle = new Label("Список букетів");
        listTitle.getStyleClass().add("section-title");

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

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Додати квітку до букету");
        dialog.setHeaderText(null);
        dialog.setResizable(true);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        Label header = new Label("🌸  Оберіть квітку з каталогу");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d6a4f;");
        Label sub = new Label("Букет: \"" + b.getName() + "\"");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Пошук за назвою...");
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 8 16; -fx-font-size: 13px;");

        ObservableList<Flower> flowerData = FXCollections.observableArrayList(catalog);
        FilteredList<Flower> filtered = new FilteredList<>(flowerData, f -> true);

        searchField.textProperty().addListener((obs, o, n) -> {
            String q = n == null ? "" : n.trim().toLowerCase();
            filtered.setPredicate(f -> q.isEmpty()
                    || f.getName().toLowerCase().contains(q)
                    || f.getTypeName().toLowerCase().contains(q));
        });

        TableView<Flower> flowerTable = new TableView<>(filtered);
        flowerTable.setPrefHeight(340);
        flowerTable.setPrefWidth(580);
        flowerTable.setPlaceholder(new Label("Немає квітів за вашим запитом"));
        flowerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Flower, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(180);

        TableColumn<Flower, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTypeName()));
        typeCol.setPrefWidth(100);
        typeCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null); setStyle("");
                } else {
                    setText(type);
                    String bg = switch (type) {
                        case "Троянда"  -> "-fx-background-color: #FFCDD2; -fx-text-fill: #B71C1C;";
                        case "Тюльпан"  -> "-fx-background-color: #F3E5F5; -fx-text-fill: #6A1B9A;";
                        case "Ромашка"  -> "-fx-background-color: #FFFDE7; -fx-text-fill: #F57F17;";
                        case "Лілія"    -> "-fx-background-color: #FFF3E0; -fx-text-fill: #E65100;";
                        case "Півонія"  -> "-fx-background-color: #FCE4EC; -fx-text-fill: #AD1457;";
                        case "Орхідея"  -> "-fx-background-color: #E8EAF6; -fx-text-fill: #283593;";
                        default         -> "";
                    };
                    setStyle(bg + " -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });

        TableColumn<Flower, String> priceCol = new TableColumn<>("Ціна");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f грн", d.getValue().getPrice())));
        priceCol.setPrefWidth(90);

        TableColumn<Flower, String> colorCol = new TableColumn<>("Колір");
        colorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getColor()));
        colorCol.setPrefWidth(90);

        TableColumn<Flower, String> stemCol = new TableColumn<>("Стебло");
        stemCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.0f см", d.getValue().getStemLength())));
        stemCol.setPrefWidth(70);

        flowerTable.getColumns().addAll(nameCol, typeCol, priceCol, colorCol, stemCol);

        Label qtyLabel = new Label("Кількість:");
        qtyLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 99, 1);
        qtySpinner.setEditable(true);
        qtySpinner.setPrefWidth(80);

        Button addBtn = new Button("＋  Додати до букету");
        addBtn.setStyle("-fx-background-color: linear-gradient(to right, #2d6a4f, #40916c);"
                + " -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;"
                + " -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        addBtn.setDisable(true);

        Label selectedInfo = new Label("← Оберіть квітку з таблиці");
        selectedInfo.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");

        flowerTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            addBtn.setDisable(n == null);
            if (n != null) {
                selectedInfo.setText("✓ " + n.getTypeName() + ": " + n.getName() + " — " + String.format("%.2f грн", n.getPrice()));
                selectedInfo.setStyle("-fx-text-fill: #2d6a4f; -fx-font-weight: bold;");
            } else {
                selectedInfo.setText("← Оберіть квітку з таблиці");
                selectedInfo.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            }
        });

        addBtn.setOnAction(e -> {
            Flower selected = flowerTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int qty = qtySpinner.getValue();
                for (int i = 0; i < qty; i++) {
                    shop.addFlowerToBouquet(b, selected);
                }
                refreshDetails();
                logger.info("Додано " + qty + " шт. \"" + selected.getName() + "\" до букету \"" + b.getName() + "\".");
                statusCallback.accept("✅ Додано " + qty + " шт. \"" + selected.getName() + "\" до букету");
                selectedInfo.setText("✅ Додано " + qty + " шт. \"" + selected.getName() + "\"!");
                selectedInfo.setStyle("-fx-text-fill: #2d6a4f; -fx-font-weight: bold;");
            }
        });

        HBox bottomRow = new HBox(12, qtyLabel, qtySpinner, addBtn, selectedInfo);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setPadding(new Insets(8, 0, 0, 0));

        VBox content = new VBox(10, header, sub, searchField, flowerTable, bottomRow);
        content.setPadding(new Insets(16));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(640);
        dialog.getDialogPane().setPrefHeight(520);

        dialog.showAndWait();
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
        dialog.setHeaderText(null);
        dialog.setResizable(true);
        ButtonType addType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        Label header = new Label("🎀  Оберіть аксесуар");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6A1B9A;");
        Label sub = new Label("Букет: \"" + b.getName() + "\"");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        String[] accEmojis = {"🎗", "🎗", "💌", "🎀", "🎀", "📄", "📦", "✨", "🕸", "🧶",
                "🧺", "🎁", "💌", "📌", "💎", "🌿", "✨", "🎇"};

        ListView<String> accList = new ListView<>();
        accList.setPrefHeight(200);
        for (int i = 0; i < PREDEFINED_ACCESSORIES.length; i++) {
            accList.getItems().add(accEmojis[i] + "  " + PREDEFINED_ACCESSORIES[i][0] + "  —  " + PREDEFINED_ACCESSORIES[i][1] + " грн");
        }
        accList.getSelectionModel().selectFirst();
        accList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 13px; -fx-padding: 8 12;");
                }
            }
        });

        Label priceDisplay = new Label("Ціна: " + PREDEFINED_ACCESSORIES[0][1] + " грн");
        priceDisplay.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2d6a4f;"
                + " -fx-background-color: #E8F5E9; -fx-padding: 8 16; -fx-background-radius: 8;");

        accList.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            int idx = n.intValue();
            if (idx >= 0 && idx < PREDEFINED_ACCESSORIES.length) {
                priceDisplay.setText("Ціна: " + PREDEFINED_ACCESSORIES[idx][1] + " грн");
            }
        });

        Label colorTitle = new Label("Колір:");
        colorTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        FlowPane colorFlow = new FlowPane(6, 6);
        colorFlow.setPrefWrapLength(400);
        ToggleGroup colorGroup = new ToggleGroup();
        for (String[] c : ACC_COLORS) {
            ToggleButton tb = new ToggleButton(c[0]);
            tb.setToggleGroup(colorGroup);
            tb.setUserData(c[0]);

            Rectangle rect = new Rectangle(12, 12);
            rect.setArcWidth(3);
            rect.setArcHeight(3);
            rect.setFill(Color.web(c[1]));
            rect.setStroke(Color.web("#ccc"));
            tb.setGraphic(rect);

            tb.setStyle("-fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 16;"
                    + " -fx-border-radius: 16; -fx-border-color: #ddd;");
            tb.selectedProperty().addListener((obs, o, n) -> {
                if (n) {
                    tb.setStyle("-fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 16;"
                            + " -fx-border-radius: 16; -fx-border-color: " + c[1] + ";"
                            + " -fx-background-color: " + c[1] + "22; -fx-font-weight: bold;");
                } else {
                    tb.setStyle("-fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 16;"
                            + " -fx-border-radius: 16; -fx-border-color: #ddd;");
                }
            });

            if (c[0].equals("Білий")) { tb.setSelected(true); }
            colorFlow.getChildren().add(tb);
        }

        VBox content = new VBox(10, header, sub, accList, priceDisplay,
                new Separator(), colorTitle, colorFlow);
        content.setPadding(new Insets(16));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);

        dialog.setResultConverter(btn -> {
            if (btn == addType) {
                int idx = accList.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    String name = PREDEFINED_ACCESSORIES[idx][0];
                    double price = Double.parseDouble(PREDEFINED_ACCESSORIES[idx][1]);
                    Toggle selected = colorGroup.getSelectedToggle();
                    String color = selected != null ? (String) selected.getUserData() : "Білий";
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


