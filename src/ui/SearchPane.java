package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.FlowerShop;
import flowers.*;

import java.util.List;
import java.util.function.Consumer;

public class SearchPane extends VBox {

    private static final Logger logger = LogManager.getLogger(SearchPane.class);
    private final FlowerShop shop;
    private final Consumer<String> statusCallback;

    private ComboBox<String> bouquetCombo;
    private TextField minField;
    private TextField maxField;
    private TableView<Flower> resultsTable;
    private Label resultLabel;

    public SearchPane(FlowerShop shop, Consumer<String> statusCallback) {
        this.shop = shop;
        this.statusCallback = statusCallback;

        setSpacing(20);
        setPadding(new Insets(30));
        getStyleClass().add("panel");


        Label title = new Label("\uD83D\uDD0D  Пошук квітів");
        title.getStyleClass().add("panel-title");
        Label subtitle = new Label("Знайдіть квіти у букеті за довжиною стебла");
        subtitle.getStyleClass().add("panel-subtitle");
        VBox header = new VBox(4, title, subtitle);


        VBox searchForm = createSearchForm();


        VBox resultsBox = createResultsPanel();
        VBox.setVgrow(resultsBox, Priority.ALWAYS);

        getChildren().addAll(header, searchForm, resultsBox);
        refresh();
    }

    private VBox createSearchForm() {
        bouquetCombo = new ComboBox<>();
        bouquetCombo.setPromptText("Оберіть букет...");
        bouquetCombo.setMaxWidth(300);

        minField = new TextField();
        minField.setPromptText("Мінімум (см)");
        minField.setPrefWidth(120);

        maxField = new TextField();
        maxField.setPromptText("Максимум (см)");
        maxField.setPrefWidth(120);

        Button searchBtn = new Button("\uD83D\uDD0D  Шукати");
        searchBtn.getStyleClass().add("btn-primary");
        searchBtn.setOnAction(e -> performSearch());

        HBox rangeBox = new HBox(10,
                new Label("Від:"), minField,
                new Label("До:"), maxField,
                searchBtn
        );
        rangeBox.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(12,
                new Label("Букет:"), bouquetCombo,
                new Label("Діапазон довжини стебла:"), rangeBox
        );
        form.getStyleClass().add("card");
        for (javafx.scene.Node child : form.getChildren()) {
            if (child instanceof Label) {
                child.getStyleClass().add("section-title");
            }
        }
        return form;
    }

    private VBox createResultsPanel() {
        resultLabel = new Label("Результати пошуку:");
        resultLabel.getStyleClass().add("section-title");

        resultsTable = new TableView<>();
        resultsTable.setPlaceholder(new Label("Виконайте пошук для відображення результатів"));
        VBox.setVgrow(resultsTable, Priority.ALWAYS);

        TableColumn<Flower, String> nameCol = new TableColumn<>("Назва");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(180);

        TableColumn<Flower, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTypeName()));
        typeCol.setPrefWidth(100);

        TableColumn<Flower, String> priceCol = new TableColumn<>("Ціна (грн)");
        priceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getPrice())));
        priceCol.setPrefWidth(100);

        TableColumn<Flower, String> stemCol = new TableColumn<>("Стебло (см)");
        stemCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.1f", d.getValue().getStemLength())));
        stemCol.setPrefWidth(100);

        TableColumn<Flower, String> colorCol = new TableColumn<>("Колір");
        colorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getColor()));
        colorCol.setPrefWidth(100);

        resultsTable.getColumns().addAll(nameCol, typeCol, priceCol, stemCol, colorCol);
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox box = new VBox(10, resultLabel, resultsTable);
        box.getStyleClass().add("card");
        VBox.setVgrow(resultsTable, Priority.ALWAYS);
        return box;
    }

    private void performSearch() {
        int idx = bouquetCombo.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            showWarning("Спочатку оберіть букет!");
            return;
        }

        double min, max;
        try {
            min = Double.parseDouble(minField.getText().trim());
            max = Double.parseDouble(maxField.getText().trim());
        } catch (NumberFormatException e) {
            logger.warn("Некоректні числові значення для пошуку: min='" + minField.getText() + "', max='" + maxField.getText() + "'");
            showWarning("Введіть коректні числові значення для діапазону!");
            return;
        }

        if (min > max) {
            showWarning("Мінімум не може бути більшим за максимум!");
            return;
        }

        Bouquet b = shop.getBouquets().get(idx);
        List<Flower> found = b.findFlowersByStemLength(min, max);

        resultsTable.setItems(FXCollections.observableArrayList(found));
        resultLabel.setText(String.format("Знайдено %d квіт(ів) у букеті \"%s\" зі стеблом від %.1f до %.1f см:",
                found.size(), b.getName(), min, max));
        logger.info(String.format("Пошук: знайдено %d квіт(ів) у букеті '%s' (стебло: %.1f–%.1f см).",
                found.size(), b.getName(), min, max));
        statusCallback.accept(String.format("🔍 Знайдено %d квіт(ів)", found.size()));
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setTitle("Увага");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void refresh() {
        bouquetCombo.getItems().clear();
        for (Bouquet b : shop.getBouquets()) {
            bouquetCombo.getItems().add(b.getName());
        }
    }
}
