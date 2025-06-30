module org.example.powt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.powt to javafx.fxml;
    exports org.example.powt;
}