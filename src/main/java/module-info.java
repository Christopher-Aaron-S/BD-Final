module com.example.projectbd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens MainApp to javafx.fxml;
    exports MainApp;

    opens DBConnector to javafx.fxml;
    exports DBConnector;
}
