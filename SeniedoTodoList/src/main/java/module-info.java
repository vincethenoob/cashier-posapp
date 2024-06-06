module app.seniedotodolist {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.seniedotodolist to javafx.fxml;
    exports app.seniedotodolist;
}