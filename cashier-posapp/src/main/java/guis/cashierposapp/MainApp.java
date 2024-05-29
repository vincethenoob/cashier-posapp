package guis.cashierposapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PosApp.fxml"));
        Parent root = loader.load();

        PosAppController controller = loader.getController();
        // You can do any additional initialization of the controller here if needed

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cashier POS App");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
