package app.seniedotodolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TodoAPP extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TodoAPP.class.getResource("Todolistlayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720,400 );
        stage.setTitle("Seniedotodolist");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}