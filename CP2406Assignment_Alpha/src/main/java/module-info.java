module rainfallanalyser.cp2406assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires commons.csv;


    opens rainfallanalyser.cp2406assignment_alpha to javafx.fxml;
    exports rainfallanalyser.cp2406assignment_alpha;
}