/**
 * PixelDngFixer
 *
 * @author Quentin Ligier
 **/

module pixel.dng.fixer {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.sandec.mdfx;
    requires com.github.mjeanroy.exiftool;
    requires java.desktop;

    opens ch.qligier.app.pixeldngfixer to javafx.graphics;
    opens ch.qligier.app.pixeldngfixer.gui to javafx.fxml;
}
