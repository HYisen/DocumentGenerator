package net.alexhyisen.dg.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import net.alexhyisen.dg.model.HttpExtractor;
import net.alexhyisen.dg.model.Item;
import net.alexhyisen.dg.model.JsExtractor;
import net.alexhyisen.dg.model.Utility;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;


public class MainController {

    private MainApp mainApp;

    private boolean initialized;

    private ObservableList<Item> items;

    @FXML
    private TableColumn<Item, String> labelTableColumn;
    @FXML
    private TableColumn<Item, String> nameTableColumn;
    @FXML
    private TableColumn<Item, String> clsTableColumn;
    @FXML
    private TableColumn<Item, String> idTableColumn;
    @FXML
    private TableColumn<Item, String> readonlyTableColumn;
    @FXML
    private TableColumn<Item, String> requiredTableColumn;
    @FXML
    private TableView<Item> dataTableView;
    @FXML
    private TextField htmlTextField;
    @FXML
    private TextField jsTextField;

    @FXML
    private void initialize() {
        labelTableColumn.setCellValueFactory(v -> v.getValue().labelProperty());
        nameTableColumn.setCellValueFactory(v -> v.getValue().nameProperty());
        clsTableColumn.setCellValueFactory(v -> v.getValue().clsProperty());
        idTableColumn.setCellValueFactory(v -> v.getValue().idProperty());
        readonlyTableColumn.setCellValueFactory(v -> v.getValue().readonlyProperty());
        requiredTableColumn.setCellValueFactory(v -> v.getValue().requiredProperty());

        dataTableView.getSelectionModel().setCellSelectionEnabled(true);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        items = FXCollections.observableArrayList();
        dataTableView.setItems(items);

        initialized = false;

        htmlTextField.setText(".\\sample.jsp");
        jsTextField.setText(".\\sample.js");
    }

    @FXML
    protected void handleGoButtonAction() {
        if (!initialized) {
            makeTableViewCopyable();
            initialized = true;
        }

        try {
            Map<String, Map<String, String>> htmlData = new HttpExtractor().extract(Paths.get(htmlTextField.getText()));
            Map<String, Map<String, String>> jsData = new JsExtractor().extract(Paths.get(jsTextField.getText()));
            update(Utility.merge(htmlData, jsData));
            dataTableView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleHtmlSelectButtonAction() {
        selectFile("Select HTML File", htmlTextField);
    }

    @FXML
    protected void handleJsSelectButtonAction() {
        selectFile("Select JavaScript File", jsTextField);
    }

    private void selectFile(String title, TextField linkedTarget) {
        File dir = Paths.get(linkedTarget.getText()).toFile().getParentFile();
        File file = mainApp.openFile(title, dir);
        if (file != null) {
            linkedTarget.setText(file.toPath().toString());
        }
    }

    private void update(Map<String, Map<String, String>> orig) {
        items.clear();
        orig.forEach((k, v) -> {
            String label = v.getOrDefault("label", "");
            String cls = v.getOrDefault("class", "");
            String id = k;
            String readonly = v.getOrDefault("readonly", "");
            String required = v.getOrDefault("required", "");

            //TODO generate name from id with cls
            String name = "";

            Item item = new Item(label, name, cls, id, readonly, required);
            items.add(item);
        });
        System.out.println("update complete");
    }

    private void makeTableViewCopyable() {
        //modified from
        //stackoverflow.com/questions/11347535/javafx-tableview-copy-to-clipboard
        //stackoverflow.com/questions/25170119/allow-user-to-copy-data-from-tableview
        MenuItem item = new MenuItem("Copy");
        EventHandler<ActionEvent> handler = event -> {
            ObservableList<TablePosition> posList = dataTableView.getSelectionModel().getSelectedCells();
            int old_r = -1;
            StringBuilder clipboardString = new StringBuilder();
            for (TablePosition p : posList) {
                int r = p.getRow();
                int c = p.getColumn();
                Object cell = dataTableView.getColumns().get(c).getCellData(r);
                if (cell == null)
                    cell = "";
                if (old_r == r)
                    clipboardString.append('\t');
                else if (old_r != -1)
                    clipboardString.append('\n');
                clipboardString.append(cell);
                old_r = r;
            }
            final ClipboardContent content = new ClipboardContent();
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        };
        //The following line make the procedure can not be put in initialize(),
        //and an insistence to do so would end up with a NullPointerException.
        mainApp.getPrimaryStage().getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY),
                () -> handler.handle(null)
        );
        item.setOnAction(handler);
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        dataTableView.setContextMenu(menu);
    }

    void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
