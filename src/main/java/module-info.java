module com.example.project2ai {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project2ai to javafx.fxml;
    exports com.example.project2ai;
}