package ch.qligier.app.pixeldngfixer.gui;

import ch.qligier.app.pixeldngfixer.Config;
import ch.qligier.app.pixeldngfixer.exiftool.PhotoMetadataHandler;
import ch.qligier.app.pixeldngfixer.PixelPhotoPairFinder;
import ch.qligier.app.pixeldngfixer.Utils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * The GUI controller of the main window.
 *
 * @author Quentin Ligier
 */
public class MainController implements Initializable {

    /**
     * JavaFX markup.
     */
    @FXML
    protected TextArea logTextarea;

    @FXML
    protected ProgressBar progressBar;

    @FXML
    protected Label appNameLabel;

    @FXML
    protected Label photoDirLabel;

    @FXML
    protected CheckBox createBackupsCheckbox;

    @FXML
    protected Button processButton;

    /**
     * The selected directory for processing.
     */
    protected File selectedDir = new File(System.getProperty("user.home"));

    /**
     * Whether a backup is created before fixing files or not.
     */
    protected boolean createBackupBeforeFixing = true;

    /**
     * The time formatter for logs.
     */
    protected final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * The parent JavaFX {@link Stage}. A bit hacky.
     */
    protected Stage stage;

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
        this.photoDirLabel.setText(this.selectedDir.getAbsolutePath());
        this.createBackupsCheckbox.setSelected(this.createBackupBeforeFixing);
        this.progressBar.setDisable(true);
    }

    /**
     * The stage setter.
     *
     * @param stage The stage.
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Event handler for clicks of the 'Process photos' button.
     */
    @FXML
    protected void onProcessButtonClick() {
        this.progressBar.progressProperty().unbind();
        this.progressBar.setProgress(0);
        this.processButton.setDisable(true);
        this.progressBar.setDisable(false);

        final Runnable resetState = () -> {
            this.processButton.setDisable(false);
            this.progressBar.setDisable(true);
            this.progressBar.progressProperty().unbind();
            this.progressBar.setProgress(1);
        };

        final var createBackupBeforeFixing = this.createBackupBeforeFixing;
        final var photoDir = this.selectedDir;
        final Consumer<String> logProxy = this::addLog;
        // Create a task to process the photos
        try {
            final var task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    final var pixelPhotoPairFinder = new PixelPhotoPairFinder();
                    final var photoMetadataHandler = new PhotoMetadataHandler();

                    final var foundPairs = pixelPhotoPairFinder.findPixelPhotos(photoDir);
                    final var nbPairs = foundPairs.size();
                    updateMessage("Found " + nbPairs + " pairs of photos to process");
                    int i = 0;
                    updateProgress(i, nbPairs);
                    for (final var pair : foundPairs) {
                        ++i;
                        //photoMetadataHandler.showMetadata(pair);
                        photoMetadataHandler.copyMetadata(pair, PhotoMetadataHandler.BROKEN_TAGS,
                            createBackupBeforeFixing);
                        updateMessage("Processed file " + pair.getValue().getName());
                        updateProgress(i, nbPairs);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    logProxy.accept("Done!");
                    resetState.run();
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    logProxy.accept("Cancelled!");
                    resetState.run();
                }

                @Override
                protected void failed() {
                    super.failed();
                    logProxy.accept("Failed!");
                    resetState.run();
                }
            };
            // Bind the event handlers
            this.progressBar.progressProperty().bind(task.progressProperty());
            task.setOnFailed(evt -> Utils.showException(task.getException()));
            task.messageProperty().addListener((observable, oldValue, newValue) -> logProxy.accept(newValue));

            // Run the action on its own thread
            final var thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        } catch (final Exception e) {
            this.addLog("[ERROR] " + e.getMessage());
            resetState.run();
            Utils.showException(e);
        }
    }

    /**
     * Event handler for clicks of the 'Choose photo directory' button.
     */
    @FXML
    protected void onChooseDirClick() {
        final var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select the photo directory");
        directoryChooser.setInitialDirectory(this.selectedDir);
        final var dir = directoryChooser.showDialog(this.stage);
        if (dir != null && dir.isDirectory()) {
            this.selectedDir = dir;
            this.photoDirLabel.setText(dir.getAbsolutePath());
        }
    }

    /**
     * Event handler for clicks of the 'About this app' button.
     */
    @FXML
    protected void onAboutButtonClick() throws IOException {
        final var newWindow = new Stage();
        final var fxmlLoader = new FXMLLoader(MainController.class.getResource("/views/about-view.fxml"));
        final var scene = new Scene(fxmlLoader.load(), 450, 600);
        newWindow.setTitle("About " + Config.APP_NAME + " v" + Config.APP_VERSION);
        newWindow.setScene(scene);
        newWindow.setResizable(false);
        newWindow.getIcons().add(Utils.getLogo());
        newWindow.show();
    }

    /**
     * Event handler for clicks of the 'Create backup' checkbox.
     */
    @FXML
    protected void onCreateBackupsCheckboxClick() {
        this.createBackupBeforeFixing = this.createBackupsCheckbox.isSelected();
    }

    /**
     * Adds a message log to the dedicated textarea.
     *
     * @param log The message to add.
     */
    protected void addLog(final String log) {
        this.logTextarea.appendText(this.timeFormatter.format(LocalDateTime.now()) + ": " + log + "\n");
    }
}
