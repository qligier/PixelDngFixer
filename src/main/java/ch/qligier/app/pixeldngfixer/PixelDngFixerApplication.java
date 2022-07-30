package ch.qligier.app.pixeldngfixer;

import ch.qligier.app.pixeldngfixer.gui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The Pixel DNG Fixer application.
 *
 * @author Quentin Ligier
 */
public class PixelDngFixerApplication extends Application {

    /**
     * Constructor.
     */
    public PixelDngFixerApplication() {
        // Enhance the font anti-aliasing
        System.setProperty("prism.lcdtext", "false");
    }

    /**
     * The main entry point for all JavaFX applications. The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param stage the primary stage for this application, onto which the application scene can be set. Applications
     *              may create other stages, if needed, but they will not be primary stages.
     * @throws IOException if the view template is not found.
     */
    @Override
    public void start(final Stage stage) throws IOException {
        final var fxmlLoader = new FXMLLoader(PixelDngFixerApplication.class.getResource("/views/main-view.fxml"));
        final var scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle(Config.APP_NAME);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(Utils.getLogo());

        final MainController mainController = fxmlLoader.getController();
        mainController.setStage(stage);

        stage.show();
    }
}
