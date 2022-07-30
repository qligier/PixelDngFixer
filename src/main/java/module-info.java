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
    requires flexmark;
    requires flexmark.util.collection;
    requires flexmark.util.misc;
    requires flexmark.util.options;
    requires flexmark.util.sequence;
    requires flexmark.util.visitor;
    requires flexmark.ext.attributes;
    requires flexmark.ext.gfm.tasklist;
    requires flexmark.ext.tables;

    opens ch.qligier.app.pixeldngfixer to javafx.graphics;
    opens ch.qligier.app.pixeldngfixer.gui to javafx.fxml;
}
