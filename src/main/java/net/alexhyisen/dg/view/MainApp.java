package net.alexhyisen.dg.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application{
    private MainController mainController;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("DocumentGenerator");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        mainController = loader.getController();
        mainController.setMainApp(this);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    Stage getPrimaryStage() {
        return primaryStage;
    }
}
