module org.thedrake3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.prefs;

    opens org.thedrake3 to javafx.fxml;
    exports org.thedrake3;
    exports org.thedrake3.Controller;
    opens org.thedrake3.Controller to javafx.fxml;
}