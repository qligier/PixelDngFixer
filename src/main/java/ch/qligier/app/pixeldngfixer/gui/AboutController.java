package ch.qligier.app.pixeldngfixer.gui;

import ch.qligier.app.pixeldngfixer.Config;
import ch.qligier.app.pixeldngfixer.exiftool.PhotoMetadataHandler;
import ch.qligier.app.pixeldngfixer.Utils;
import com.sandec.mdfx.MarkdownView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The GUI controller of the 'about' window.
 *
 * @author Quentin Ligier
 **/
public class AboutController implements Initializable {

    /**
     * JavaFX markup.
     */
    @FXML
    protected ScrollPane changelogScrollPane;

    @FXML
    protected ScrollPane creditsScrollPane;

    @FXML
    protected ScrollPane metadataScrollPane;

    @FXML
    protected Text licenseText;

    @FXML
    protected Label appNameLabel;

    @FXML
    protected Label appVersionLabel;

    @FXML
    protected Hyperlink gitHubLink;

    /**
     * Called to initialize a controller after its root element has been completely processed.
     *
     * @param url            The location used to resolve relative paths for the root object, or {@code null} if the
     *                       location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if the root object was not
     *                       localized.
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        this.appNameLabel.setText(Config.APP_NAME);
        this.appVersionLabel.setText(Config.APP_VERSION);

        final var metadataDescription = new StringBuilder();
        metadataDescription.append("The following metadata seems either invalid or absent from DNG files and will be " +
            "copied:\n\n");
        for (final var tag : PhotoMetadataHandler.BROKEN_TAGS) {
            metadataDescription.append("- ").append(tag.getDisplayName()).append("\n");
        }

        this.licenseText.setText(this.loadResource("/LICENSE.txt"));
        this.changelogScrollPane.setContent(new CustomMarkdownView(this.loadResource("/CHANGELOG.md")));
        this.creditsScrollPane.setContent(new CustomMarkdownView(this.loadResource("/NOTICE.md")));
        this.metadataScrollPane.setContent(new CustomMarkdownView(metadataDescription.toString()));
    }

    /**
     * Opens the GitHub project in a browser.
     */
    @FXML
    protected void openGitHub() {
        Utils.openBrowser("https://www.github.com/qligier/PixelDngFixer");
        this.gitHubLink.setVisited(false);
    }

    /**
     * Opens my personal website in a browser.
     */
    @FXML
    protected void openPersonalSite() {
        Utils.openBrowser("https://www.qligier.ch");
        this.gitHubLink.setVisited(false);
    }

    /**
     * Fetches a resource and reads it as a {@link String}.
     *
     * @param path The resource path.
     * @return the resource content or an empty string.
     */
    private String loadResource(final String path) {
        try (final var is = AboutController.class.getResourceAsStream(path)) {
            if (is == null) {
                return "";
            }
            return new String(is.readAllBytes());
        } catch (final IOException e) {
            Utils.showException(e);
            return "";
        }
    }

    /**
     * A customization of the markdown view component.
     */
    @SuppressWarnings("java:S110")
    private static final class CustomMarkdownView extends MarkdownView {

        public CustomMarkdownView(final String mdString) {
            super(mdString);
            final var cssPath = Objects.requireNonNull(AboutController.class.getResource("mdfx.css")).toExternalForm();
            this.getStylesheets().clear();
            this.getStylesheets().add(cssPath);
            this.setPrefWidth(420);
            this.setMaxWidth(420);
        }


        @Override
        public void setLink(final Node node, final String link, final String description) {
            node.setOnMouseClicked(e -> Utils.openBrowser(link.strip()));
            Tooltip.install(node, new Tooltip(link));
        }
    }
}
