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

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class CatalogPane extends VBox {

    private static final Logger logger = LogManager.getLogger(CatalogPane.class);
    private final FlowerShop shop;
    private final Consumer<String> statusCallback;
    private TableView<Flower> table;

    private ObservableList<Flower> allFlowers;
    private FilteredList<Flower> filteredFlowers;

    private TextField nameSearchField;
    private final Map<String, CheckBox> typeCheckboxes = new LinkedHashMap<>();
    private TextField priceMinField;
    private TextField priceMaxField;
    private TextField stemMinField;
    private TextField stemMaxField;
    private final Map<String, CheckBox> colorCheckboxes = new LinkedHashMap<>();
    private Label resultsCountLabel;

    private static final String[][] COLORS = {
            {"Червоний", "#E53935"},
            {"Рожевий", "#EC407A"},
            {"Білий", "#FAFAFA"},
            {"Жовтий", "#FDD835"},
            {"Помаранчевий", "#FB8C00"},
            {"Фіолетовий", "#8E24AA"},
            {"Синій", "#1E88E5"},
            {"Бірюзовий", "#26A69A"},
            {"Кремовий", "#FFF8E1"},
            {"Бордовий", "#880E4F"},
            {"Лавандовий", "#B39DDB"},
            {"Персиковий", "#FFAB91"},
            {"Коралловий", "#FF7043"},
    };

    private static final String[] FLOWER_TYPES = {
            "Троянда", "Тюльпан", "Ромашка", "Лілія", "Півонія", "Орхідея"
    };

    private static final String[] FLOWER_TYPE_EMOJIS = {
            "🌹", "🌷", "🌼", "🌺", "🌸", "🪷"
    };

    public CatalogPane(FlowerShop shop, Consumer<String> statusCallback) {
        this.shop = shop;
        this.statusCallback = statusCallback;

        setSpacing(20);
        setPadding(new Insets(30));
        getStyleClass().add("panel");

        Label title = new Label("\uD83C\uDF39  Каталог квітів");
        title.getStyleClass().add("panel-title");
        Label subtitle = new Label("Асортимент із " + shop.getCatalog().size() + " квітів • 6 видів");
        subtitle.getStyleClass().add("panel-subtitle");
        VBox header = new VBox(4, title, subtitle);

        HBox content = createContent();
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(header, content);
        refresh();
    }

    private HBox createContent() {
        VBox filterPanel = createFilterPanel();
        filterPanel.setMinWidth(240);
        filterPanel.setPrefWidth(260);

        VBox tablePanel = createTablePanel();
        HBox.setHgrow(tablePanel, Priority.ALWAYS);

        HBox content = new HBox(20, filterPanel, tablePanel);
        return content;
    }



    private VBox createFilterPanel() {
        Label filterTitle = new Label("⚙  Фільтри");
        filterTitle.getStyleClass().add("filter-main-title");

        VBox nameSection = createNameSearchSection();

        VBox typeSection = createTypeFilterSection();

        VBox priceSection = createPriceFilterSection();

        VBox stemSection = createStemFilterSection();

        VBox colorSection = createColorFilterSection();

        Button resetBtn = new Button("↺  Скинути фільтри");
        resetBtn.getStyleClass().add("btn-reset");
        resetBtn.setMaxWidth(Double.MAX_VALUE);
        resetBtn.setOnAction(e -> resetFilters());

        resultsCountLabel = new Label("Знайдено: 0 з 0 квітів");
        resultsCountLabel.getStyleClass().add("filter-results-label");

        VBox filterContent = new VBox(16,
                filterTitle,
                nameSection,
                createFilterSeparator(),
                typeSection,
                createFilterSeparator(),
                priceSection,
                createFilterSeparator(),
                stemSection,
                createFilterSeparator(),
                colorSection,
                createFilterSeparator(),
                resetBtn,
                resultsCountLabel
        );
        filterContent.setPadding(new Insets(4));

        ScrollPane scroll = new ScrollPane(filterContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.getStyleClass().add("filter-scroll");

        VBox panel = new VBox(scroll);
        panel.getStyleClass().add("card");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return panel;
    }

    private Region createFilterSeparator() {
        Region sep = new Region();
        sep.getStyleClass().add("filter-separator");
        sep.setMinHeight(1);
        sep.setMaxHeight(1);
        return sep;
    }

    private VBox createNameSearchSection() {
        Label title = new Label("Назва");
        title.getStyleClass().add("filter-section-title");

        nameSearchField = new TextField();
        nameSearchField.setPromptText("\uD83D\uDD0D  Пошук за назвою...");
        nameSearchField.getStyleClass().add("search-field");
        nameSearchField.setMaxWidth(Double.MAX_VALUE);
        nameSearchField.textProperty().addListener((obs, o, n) -> applyFilters());

        return new VBox(6, title, nameSearchField);
    }

    private VBox createTypeFilterSection() {
        Label title = new Label("Тип квітки");
        title.getStyleClass().add("filter-section-title");

        VBox checkboxes = new VBox(4);
        for (int i = 0; i < FLOWER_TYPES.length; i++) {
            CheckBox cb = new CheckBox(FLOWER_TYPE_EMOJIS[i] + "  " + FLOWER_TYPES[i]);
            cb.setSelected(true);
            cb.getStyleClass().add("filter-checkbox");
            cb.selectedProperty().addListener((obs, o, n) -> applyFilters());
            typeCheckboxes.put(FLOWER_TYPES[i], cb);
            checkboxes.getChildren().add(cb);
        }

        Hyperlink selectAll = new Hyperlink("Обрати все");
        selectAll.getStyleClass().add("filter-link");
        selectAll.setOnAction(e -> {
            typeCheckboxes.values().forEach(cb -> cb.setSelected(true));
        });
        Hyperlink deselectAll = new Hyperlink("Зняти все");
        deselectAll.getStyleClass().add("filter-link");
        deselectAll.setOnAction(e -> {
            typeCheckboxes.values().forEach(cb -> cb.setSelected(false));
        });
        HBox links = new HBox(8, selectAll, deselectAll);

        return new VBox(6, title, checkboxes, links);
    }

    private VBox createPriceFilterSection() {
        Label title = new Label("Ціна (грн)");
        title.getStyleClass().add("filter-section-title");

        priceMinField = new TextField();
        priceMinField.setPromptText("Від");
        priceMinField.getStyleClass().add("filter-range-field");
        priceMinField.textProperty().addListener((obs, o, n) -> {
            if (!n.isEmpty() && !n.matches("\\d*\\.?\\d*")) priceMinField.setText(o);
            else applyFilters();
        });

        priceMaxField = new TextField();
        priceMaxField.setPromptText("До");
        priceMaxField.getStyleClass().add("filter-range-field");
        priceMaxField.textProperty().addListener((obs, o, n) -> {
            if (!n.isEmpty() && !n.matches("\\d*\\.?\\d*")) priceMaxField.setText(o);
            else applyFilters();
        });

        HBox rangeBox = new HBox(8, priceMinField, new Label("—"), priceMaxField);
        rangeBox.setAlignment(Pos.CENTER_LEFT);

        return new VBox(6, title, rangeBox);
    }

    private VBox createStemFilterSection() {
        Label title = new Label("Довжина стебла (см)");
        title.getStyleClass().add("filter-section-title");

        stemMinField = new TextField();
        stemMinField.setPromptText("Від");
        stemMinField.getStyleClass().add("filter-range-field");
        stemMinField.textProperty().addListener((obs, o, n) -> {
            if (!n.isEmpty() && !n.matches("\\d*\\.?\\d*")) stemMinField.setText(o);
            else applyFilters();
        });

        stemMaxField = new TextField();
        stemMaxField.setPromptText("До");
        stemMaxField.getStyleClass().add("filter-range-field");
        stemMaxField.textProperty().addListener((obs, o, n) -> {
            if (!n.isEmpty() && !n.matches("\\d*\\.?\\d*")) stemMaxField.setText(o);
            else applyFilters();
        });

        HBox rangeBox = new HBox(8, stemMinField, new Label("—"), stemMaxField);
        rangeBox.setAlignment(Pos.CENTER_LEFT);

        return new VBox(6, title, rangeBox);
    }

    private VBox createColorFilterSection() {
        Label title = new Label("Колір");
        title.getStyleClass().add("filter-section-title");

        FlowPane colorFlow = new FlowPane(6, 6);
        colorFlow.setPrefWrapLength(220);

        for (String[] c : COLORS) {
            CheckBox cb = new CheckBox(c[0]);
            cb.setSelected(true);
            cb.getStyleClass().add("filter-checkbox");

            Rectangle rect = new Rectangle(10, 10);
            rect.setArcWidth(3);
            rect.setArcHeight(3);
            rect.setFill(Color.web(c[1]));
            rect.setStroke(Color.web("#ccc"));
            cb.setGraphic(rect);

            cb.selectedProperty().addListener((obs, o, n) -> applyFilters());
            colorCheckboxes.put(c[0], cb);
            colorFlow.getChildren().add(cb);
        }

        Hyperlink selectAll = new Hyperlink("Обрати все");
        selectAll.getStyleClass().add("filter-link");
        selectAll.setOnAction(e -> {
            colorCheckboxes.values().forEach(cb -> cb.setSelected(true));
        });
        Hyperlink deselectAll = new Hyperlink("Зняти все");
        deselectAll.getStyleClass().add("filter-link");
        deselectAll.setOnAction(e -> {
            colorCheckboxes.values().forEach(cb -> cb.setSelected(false));
        });
        HBox links = new HBox(8, selectAll, deselectAll);

        return new VBox(6, title, colorFlow, links);
    }



    private void applyFilters() {
        if (filteredFlowers == null) return;

        String nameFilter = nameSearchField.getText() == null ? "" : nameSearchField.getText().trim().toLowerCase();

        Set<String> selectedTypes = new HashSet<>();
        for (Map.Entry<String, CheckBox> entry : typeCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) selectedTypes.add(entry.getKey());
        }

        Double priceMin = parseOptionalDouble(priceMinField.getText());
        Double priceMax = parseOptionalDouble(priceMaxField.getText());
        Double stemMin = parseOptionalDouble(stemMinField.getText());
        Double stemMax = parseOptionalDouble(stemMaxField.getText());

        Set<String> selectedColors = new HashSet<>();
        for (Map.Entry<String, CheckBox> entry : colorCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) selectedColors.add(entry.getKey());
        }

        filteredFlowers.setPredicate(flower -> {
            if (!nameFilter.isEmpty() && !flower.getName().toLowerCase().contains(nameFilter)) {
                return false;
            }
            if (!selectedTypes.contains(flower.getTypeName())) {
                return false;
            }
            if (priceMin != null && flower.getPrice() < priceMin) return false;
            if (priceMax != null && flower.getPrice() > priceMax) return false;
            if (stemMin != null && flower.getStemLength() < stemMin) return false;
            if (stemMax != null && flower.getStemLength() > stemMax) return false;
            if (!selectedColors.contains(flower.getColor())) {
                return false;
            }
            return true;
        });

        updateResultsCount();
        logger.debug("Фільтри застосовано: знайдено " + filteredFlowers.size() + " з " + allFlowers.size());
    }

    private void resetFilters() {
        nameSearchField.clear();
        typeCheckboxes.values().forEach(cb -> cb.setSelected(true));
        priceMinField.clear();
        priceMaxField.clear();
        stemMinField.clear();
        stemMaxField.clear();
        colorCheckboxes.values().forEach(cb -> cb.setSelected(true));
        applyFilters();
        logger.info("Фільтри скинуто.");
        statusCallback.accept("↺ Фільтри скинуто — показано всі квіти каталогу");
    }

    private void updateResultsCount() {
        int total = allFlowers != null ? allFlowers.size() : 0;
        int shown = filteredFlowers != null ? filteredFlowers.size() : 0;
        resultsCountLabel.setText(String.format("Знайдено: %d з %d квітів", shown, total));
        if (shown == total) {
            resultsCountLabel.setStyle("-fx-text-fill: #6B9A9A;");
        } else if (shown == 0) {
            resultsCountLabel.setStyle("-fx-text-fill: #E05555;");
        } else {
            resultsCountLabel.setStyle("-fx-text-fill: #2AA5A5;");
        }
    }

    private Double parseOptionalDouble(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }



    private VBox createTablePanel() {
        table = createTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox buttons = createButtons();

        VBox panel = new VBox(12, table, buttons);
        panel.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        return panel;
    }

    private TableView<Flower> createTable() {
        TableView<Flower> tv = new TableView<>();
        tv.setPlaceholder(new Label("Немає квітів, що відповідають фільтрам"));

        TableColumn<Flower, String> nameCol = col("Назва", 170, f -> f.getName());
        TableColumn<Flower, String> typeCol = col("Тип", 90, Flower::getTypeName);
        TableColumn<Flower, String> priceCol = col("Ціна (грн)", 90, f -> String.format("%.2f", f.getPrice()));

        TableColumn<Flower, String> colorCol = new TableColumn<>("Колір");
        colorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getColor()));
        colorCol.setPrefWidth(110);
        colorCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Rectangle rect = new Rectangle(14, 14);
                    rect.setArcWidth(4);
                    rect.setArcHeight(4);
                    rect.setFill(colorToFx(color));
                    rect.setStroke(Color.web("#ccc"));
                    HBox box = new HBox(6, rect, new Label(color));
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                    setText(null);
                }
            }
        });

        TableColumn<Flower, String> stemCol = col("Стебло (см)", 90, f -> String.format("%.0f", f.getStemLength()));
        TableColumn<Flower, String> freshCol = col("Свіжість", 100, f -> f.getFreshnessDate().toString());

        tv.getColumns().addAll(nameCol, typeCol, priceCol, colorCol, stemCol, freshCol);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    private TableColumn<Flower, String> col(String title, int width, java.util.function.Function<Flower, String> extractor) {
        TableColumn<Flower, String> c = new TableColumn<>(title);
        c.setCellValueFactory(d -> new SimpleStringProperty(extractor.apply(d.getValue())));
        c.setPrefWidth(width);
        return c;
    }

    private HBox createButtons() {
        Button addBtn = new Button("＋  Додати квітку");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showAddFlowerDialog());

        Button delBtn = new Button("\uD83D\uDDD1  Видалити");
        delBtn.getStyleClass().add("btn-danger");
        delBtn.setOnAction(e -> deleteSelectedFlower());

        HBox box = new HBox(12, addBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }



    private void showAddFlowerDialog() {
        logger.info("Користувач відкрив діалог додавання нової квітки.");
        Dialog<Flower> dialog = new Dialog<>();
        dialog.setTitle("Нова квітка");
        dialog.setHeaderText("Створення нової квітки для каталогу");
        dialog.setResizable(true);

        ButtonType addBtnType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(FLOWER_TYPES));
        typeBox.setValue("Троянда");
        typeBox.setMaxWidth(Double.MAX_VALUE);

        TextField nameField = new TextField();
        nameField.setPromptText("Назва квітки");

        TextField priceField = new TextField();
        priceField.setPromptText("0.00");
        priceField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*\\.?\\d*")) priceField.setText(o);
        });

        TextField stemField = new TextField();
        stemField.setPromptText("0");
        stemField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*\\.?\\d*")) stemField.setText(o);
        });

        ComboBox<String> colorBox = new ComboBox<>();
        for (String[] c : COLORS) colorBox.getItems().add(c[0]);
        colorBox.setValue("Червоний");
        colorBox.setMaxWidth(Double.MAX_VALUE);
        colorBox.setCellFactory(lv -> createColorCell());
        colorBox.setButtonCell(createColorCell());

        VBox specificFields = new VBox(8);
        updateSpecificFields(specificFields, "Троянда");

        typeBox.setOnAction(e -> updateSpecificFields(specificFields, typeBox.getValue()));

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;
        grid.add(label("Тип квітки:"), 0, row);      grid.add(typeBox, 1, row++);
        grid.add(label("Назва:"), 0, row);             grid.add(nameField, 1, row++);
        grid.add(label("Ціна (грн):"), 0, row);        grid.add(priceField, 1, row++);
        grid.add(label("Стебло (см):"), 0, row);       grid.add(stemField, 1, row++);
        grid.add(label("Колір:"), 0, row);             grid.add(colorBox, 1, row++);
        grid.add(new Separator(), 0, row++, 2, 1);
        grid.add(specificFields, 0, row, 2, 1);

        ColumnConstraints cc1 = new ColumnConstraints(120);
        ColumnConstraints cc2 = new ColumnConstraints(240);
        grid.getColumnConstraints().addAll(cc1, cc2);

        dialog.getDialogPane().lookupButton(addBtnType).setDisable(true);
        nameField.textProperty().addListener((o, a, b) -> validateForm(dialog, addBtnType, nameField, priceField, stemField));
        priceField.textProperty().addListener((o, a, b) -> validateForm(dialog, addBtnType, nameField, priceField, stemField));
        stemField.textProperty().addListener((o, a, b) -> validateForm(dialog, addBtnType, nameField, priceField, stemField));

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(420);

        dialog.setResultConverter(btn -> {
            if (btn == addBtnType) {
                try {
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    double stem = Double.parseDouble(stemField.getText().trim());
                    String color = colorBox.getValue();
                    return buildFlower(typeBox.getValue(), name, price, stem, color, specificFields);
                } catch (Exception ex) {
                    logger.error("Помилка при створенні квітки з даних форми.", ex);
                    showWarning("Помилка у введених даних: " + ex.getMessage());
                }
            }
            return null;
        });

        Optional<Flower> result = dialog.showAndWait();
        result.ifPresent(flower -> {
            shop.addFlower(flower);
            refresh();
            logger.info("Квітку \"" + flower.getName() + "\" (" + flower.getTypeName() + ") додано через UI.");
            statusCallback.accept("✅ Квітку \"" + flower.getName() + "\" (" + flower.getTypeName() + ") додано");
        });
    }

    private void validateForm(Dialog<?> dialog, ButtonType btnType, TextField name, TextField price, TextField stem) {
        boolean valid = !name.getText().trim().isEmpty()
                && !price.getText().trim().isEmpty()
                && !stem.getText().trim().isEmpty();
        try {
            if (valid) {
                Double.parseDouble(price.getText().trim());
                Double.parseDouble(stem.getText().trim());
            }
        } catch (NumberFormatException e) {
            valid = false;
        }
        dialog.getDialogPane().lookupButton(btnType).setDisable(!valid);
    }

    private void updateSpecificFields(VBox container, String type) {
        container.getChildren().clear();
        switch (type) {
            case "Троянда" -> {
                CheckBox thorns = new CheckBox("З шипами");
                thorns.setSelected(true);
                thorns.setId("thorns");
                ComboBox<String> budShape = new ComboBox<>(FXCollections.observableArrayList(
                        "Келих", "Бокаловидний", "Півонієвидний", "Класичний", "Розетковий"));
                budShape.setValue("Келих");
                budShape.setId("budShape");
                budShape.setMaxWidth(Double.MAX_VALUE);
                container.getChildren().addAll(label("Форма бутона:"), budShape, thorns);
            }
            case "Тюльпан" -> {
                ComboBox<String> petalShape = new ComboBox<>(FXCollections.observableArrayList(
                        "Овальна", "Витягнута", "Округла", "Зірчаста", "Бахромчата"));
                petalShape.setValue("Овальна");
                petalShape.setId("petalShape");
                petalShape.setMaxWidth(Double.MAX_VALUE);
                CheckBox dbl = new CheckBox("Махровий");
                dbl.setId("isDouble");
                container.getChildren().addAll(label("Форма пелюстки:"), petalShape, dbl);
            }
            case "Ромашка" -> {
                TextField core = new TextField("1.5");
                core.setId("coreSize");
                core.setPromptText("Розмір (см)");
                TextField petals = new TextField("18");
                petals.setId("petalCount");
                petals.setPromptText("Кількість");
                container.getChildren().addAll(label("Розмір серцевини (см):"), core, label("Кількість пелюсток:"), petals);
            }
            case "Лілія" -> {
                ComboBox<String> fragrance = new ComboBox<>(FXCollections.observableArrayList(
                        "Інтенсивний", "Солодкий", "Легкий", "Ніжний", "Без аромату"));
                fragrance.setValue("Легкий");
                fragrance.setId("fragrance");
                fragrance.setMaxWidth(Double.MAX_VALUE);
                TextField petals = new TextField("6");
                petals.setId("petalCount");
                container.getChildren().addAll(label("Аромат:"), fragrance, label("Кількість пелюсток:"), petals);
            }
            case "Півонія" -> {
                ComboBox<String> bloom = new ComboBox<>(FXCollections.observableArrayList(
                        "Бутон", "Напіввідкритий", "Відкритий", "Повністю розкритий"));
                bloom.setValue("Відкритий");
                bloom.setId("bloomStage");
                bloom.setMaxWidth(Double.MAX_VALUE);
                CheckBox fragrant = new CheckBox("Ароматна");
                fragrant.setSelected(true);
                fragrant.setId("isFragrant");
                container.getChildren().addAll(label("Стадія цвітіння:"), bloom, fragrant);
            }
            case "Орхідея" -> {
                ComboBox<String> variety = new ComboBox<>(FXCollections.observableArrayList(
                        "Фаленопсіс", "Дендробіум", "Цимбідіум", "Каттлея", "Ванда", "Онцидіум"));
                variety.setValue("Фаленопсіс");
                variety.setId("variety");
                variety.setMaxWidth(Double.MAX_VALUE);
                CheckBox epiphytic = new CheckBox("Епіфітна");
                epiphytic.setSelected(true);
                epiphytic.setId("isEpiphytic");
                container.getChildren().addAll(label("Сорт:"), variety, epiphytic);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Flower buildFlower(String type, String name, double price, double stem, String color, VBox specific) {
        LocalDate today = LocalDate.now();
        return switch (type) {
            case "Троянда" -> new Rose(name, price, stem, color, today,
                    findCheckBox(specific, "thorns").isSelected(),
                    findComboBox(specific, "budShape").getValue());
            case "Тюльпан" -> new Tulip(name, price, stem, color, today,
                    findComboBox(specific, "petalShape").getValue(),
                    findCheckBox(specific, "isDouble").isSelected());
            case "Ромашка" -> new Chamomile(name, price, stem, color, today,
                    Double.parseDouble(findTextField(specific, "coreSize").getText()),
                    Integer.parseInt(findTextField(specific, "petalCount").getText()));
            case "Лілія" -> new Lily(name, price, stem, color, today,
                    findComboBox(specific, "fragrance").getValue(),
                    Integer.parseInt(findTextField(specific, "petalCount").getText()));
            case "Півонія" -> new Peony(name, price, stem, color, today,
                    findComboBox(specific, "bloomStage").getValue(),
                    findCheckBox(specific, "isFragrant").isSelected());
            case "Орхідея" -> new Orchid(name, price, stem, color, today,
                    findComboBox(specific, "variety").getValue(),
                    findCheckBox(specific, "isEpiphytic").isSelected());
            default -> null;
        };
    }



    private void deleteSelectedFlower() {
        Flower sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showWarning("Оберіть квітку для видалення"); return; }
        int idx = shop.getCatalog().indexOf(sel);
        if (idx >= 0) {
            shop.removeFlower(idx);
            refresh();
            logger.info("Квітку \"" + sel.getName() + "\" видалено через UI.");
            statusCallback.accept("\uD83D\uDDD1 Квітку \"" + sel.getName() + "\" видалено");
        }
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("info-label");
        return l;
    }

    private ListCell<String> createColorCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Rectangle rect = new Rectangle(16, 16);
                    rect.setArcWidth(4);
                    rect.setArcHeight(4);
                    rect.setFill(colorToFx(item));
                    rect.setStroke(Color.web("#ccc"));
                    HBox box = new HBox(8, rect, new Label(item));
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                    setText(null);
                }
            }
        };
    }

    private Color colorToFx(String name) {
        for (String[] c : COLORS) {
            if (c[0].equals(name)) return Color.web(c[1]);
        }
        return Color.LIGHTGRAY;
    }

    @SuppressWarnings("unchecked")
    private ComboBox<String> findComboBox(VBox container, String id) {
        return (ComboBox<String>) container.getChildren().stream()
                .filter(n -> id.equals(n.getId())).findFirst().orElse(null);
    }

    private CheckBox findCheckBox(VBox container, String id) {
        return (CheckBox) container.getChildren().stream()
                .filter(n -> id.equals(n.getId())).findFirst().orElse(null);
    }

    private TextField findTextField(VBox container, String id) {
        return (TextField) container.getChildren().stream()
                .filter(n -> id.equals(n.getId())).findFirst().orElse(null);
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setTitle("Увага");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void refresh() {
        allFlowers = FXCollections.observableArrayList(shop.getCatalog());
        filteredFlowers = new FilteredList<>(allFlowers, f -> true);
        table.setItems(filteredFlowers);
        applyFilters();
    }
}
