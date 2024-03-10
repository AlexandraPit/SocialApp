module com.example.project_sa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    //requires org.junit.jupiter.api;
    requires java.prefs;


    opens com.example.project_sa to javafx.fxml;
    exports com.example.project_sa;

    opens com.example.project_sa.domain to javafx.fxml;
    exports com.example.project_sa.domain;

    opens com.example.project_sa.service to javafx.fxml;
    exports com.example.project_sa.service;

    opens com.example.project_sa.repository to javafx.fxml;
    exports com.example.project_sa.repository;

    opens com.example.project_sa.Controller to javafx.fxml;
    exports com.example.project_sa.Controller;


}