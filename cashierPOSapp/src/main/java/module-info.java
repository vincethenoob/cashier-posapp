module app.cashierposapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens app.cashierposapp to javafx.fxml;
    exports app.cashierposapp;
}