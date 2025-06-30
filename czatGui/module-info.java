module org.example.czatgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.czatgui to javafx.fxml;
    exports org.example.czatgui;
}