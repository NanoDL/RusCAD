module ru.ruslan.spring.cad {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens ru.ruslan.spring.cad to javafx.fxml;
    exports ru.ruslan.spring.cad;
    exports ru.ruslan.spring.cad.Controllers;
    opens ru.ruslan.spring.cad.Controllers to javafx.fxml;
    exports ru.ruslan.spring.cad.Models;
    opens ru.ruslan.spring.cad.Models to javafx.fxml;
    exports ru.ruslan.spring.cad.Interfaces;
    opens ru.ruslan.spring.cad.Interfaces to javafx.fxml;
}