module com.example.toomanybarbershopgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;

    opens com.example.toomanybarbershopgui to javafx.fxml;
    exports com.example.toomanybarbershopgui;
}