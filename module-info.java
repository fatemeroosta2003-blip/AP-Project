module APP {
    requires com.google.gson;
    requires json.simple;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires java.desktop;
    exports view.controls;
    exports view;
    exports model;
    exports controller;
    opens view to javafx.fxml;
    opens model to javafx.fxml;
    opens controller to javafx.fxml;
    opens view.controls to javafx.fxml;
    opens view.controls.changers to javafx.fxml;
}