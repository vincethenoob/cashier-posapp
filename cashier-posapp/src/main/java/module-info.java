module guis.cashierposapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens guis.cashierposapp to javafx.fxml, javafx.graphics;
    exports guis.cashierposapp;
}