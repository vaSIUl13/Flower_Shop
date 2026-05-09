package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import services.FlowerShop;
import flowers.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Панель каталогу квітів з покращеним UI:
 * - 6 типів квітів
 * - ColorPicker-стиль вибору кольору
 * - Валідація вводу
 */
public class CatalogPane extends VBox {

    private final FlowerShop shop;
    private final Consumer<String> statusCallback;
    private TableView<Flower> table;

    // Предвизначені кольори з hex-кодами
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

        table = createTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox buttons = createButtons();
        getChildren().addAll(new VBox(4, title, subtitle), table, buttons);
        refresh();
    }

    private TableView<Flower> createTable() {
        TableView<Flower> tv = new TableView<>();
        tv.setPlaceholder(new Label("Каталог порожній — натисніть «Додати квітку»"));

        TableColumn<Flower, String> nameCol = col("Назва", 170, f -> f.getName());
        TableColumn<Flower, String> typeCol = col("Тип", 90, Flower::getTypeName);
        TableColumn<Flower, String> priceCol = col("Ціна (грн)", 90, f -> String.format("%.2f", f.getPrice()));

        // Колонка кольору з кольоровим квадратиком
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

    /** Фабрика колонок для стислості */
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

    // ==================== ДІАЛОГ ДОДАВАННЯ КВІТКИ ====================

    private void showAddFlowerDialog() {
        Dialog<Flower> dialog = new Dialog<>();
        dialog.setTitle("Нова квітка");
        dialog.setHeaderText("Створення нової квітки для каталогу");
        dialog.setResizable(true);

        ButtonType addBtnType = new ButtonType("Додати", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        // Загальні поля
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(FLOWER_TYPES));
        typeBox.setValue("Троянда");
        typeBox.setMaxWidth(Double.MAX_VALUE);

        TextField nameField = new TextField();
        nameField.setPromptText("Назва квітки");

        TextField priceField = new TextField();
        priceField.setPromptText("0.00");
        // Числова валідація ціни
        priceField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*\\.?\\d*")) priceField.setText(o);
        });

        TextField stemField = new TextField();
        stemField.setPromptText("0");
        stemField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*\\.?\\d*")) stemField.setText(o);
        });

        // Комбобокс кольору з кольоровими прямокутниками
        ComboBox<String> colorBox = new ComboBox<>();
        for (String[] c : COLORS) colorBox.getItems().add(c[0]);
        colorBox.setValue("Червоний");
        colorBox.setMaxWidth(Double.MAX_VALUE);
        colorBox.setCellFactory(lv -> createColorCell());
        colorBox.setButtonCell(createColorCell());

        // Специфічні поля для кожного типу
        VBox specificFields = new VBox(8);
        updateSpecificFields(specificFields, "Троянда");

        typeBox.setOnAction(e -> updateSpecificFields(specificFields, typeBox.getValue()));

        // Головна сітка
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

        // Деактивувати кнопку "Додати" поки не заповнено
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
                    showWarning("Помилка у введених даних: " + ex.getMessage());
                }
            }
            return null;
        });

        Optional<Flower> result = dialog.showAndWait();
        result.ifPresent(flower -> {
            shop.addFlower(flower);
            refresh();
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

    /** Оновлює специфічні поля під обраний тип квітки */
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

    /** Створює квітку з даних форми */
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

    // ==================== Helpers ====================

    private void deleteSelectedFlower() {
        Flower sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showWarning("Оберіть квітку для видалення"); return; }
        int idx = shop.getCatalog().indexOf(sel);
        if (idx >= 0) {
            shop.removeFlower(idx);
            refresh();
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

    /** Конвертує українську назву кольору в JavaFX Color */
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
        table.setItems(FXCollections.observableArrayList(shop.getCatalog()));
        table.refresh();
    }
}
