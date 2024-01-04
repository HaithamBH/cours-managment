module com.hichoma.coursmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.hichoma.coursManagement to javafx.fxml;
    opens com.hichoma.coursManagement.models to javafx.fxml;

    exports com.hichoma.coursManagement;
    exports com.hichoma.coursManagement.models;
}