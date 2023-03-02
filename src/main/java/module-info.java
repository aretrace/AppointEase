module com.appointease.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.appointease.app.controller;
    opens com.appointease.app.controller to javafx.fxml;
    exports com.appointease.app.model;
    opens com.appointease.app.model;
    exports com.appointease.app;
    opens com.appointease.app to javafx.fxml;
}