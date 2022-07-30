package ch.qligier.app.pixeldngfixer.exiftool;

import com.thebuzzmedia.exiftool.ExecutionStrategy;
import com.thebuzzmedia.exiftool.Version;
import com.thebuzzmedia.exiftool.logs.Logger;
import com.thebuzzmedia.exiftool.logs.LoggerFactory;
import com.thebuzzmedia.exiftool.process.CommandExecutor;
import com.thebuzzmedia.exiftool.process.OutputHandler;
import com.thebuzzmedia.exiftool.process.command.CommandBuilder;

import java.io.IOException;
import java.util.List;

/**
 * A custom execution strategy for the ExifTool interface.
 *
 * @author Quentin Ligier
 **/
public class CustomExecutionStrategy implements ExecutionStrategy {
    private static final Logger log = LoggerFactory.getLogger(CustomExecutionStrategy.class);

    /**
     * Empty constructor.
     */
    public CustomExecutionStrategy() {
    }

    /**
     * The ExifTool command executor.
     *
     * @param executor  ExifTool withExecutor.
     * @param exifTool  ExifTool withPath.
     * @param arguments Command line arguments.
     * @param handler   Handler to read command output.
     * @throws IOException if an exception arises when running ExifTool.
     */
    public void execute(final CommandExecutor executor,
                        final String exifTool,
                        final List<String> arguments,
                        final OutputHandler handler) throws IOException {
        log.debug("Using ExifTool in non-daemon mode (-stay_open False)...");
        final var cmd = CommandBuilder.builder(exifTool, arguments.size() + 2)
            .addArgument("-m") // Ignore minor errors
            .addArgument("-n") // Numeric format
            .addAll(arguments)
            .build();
        log.debug("Executing command: {}", cmd.toString());
        executor.execute(cmd, handler);
    }

    public boolean isSupported(final Version version) {
        return true;
    }

    public boolean isRunning() {
        return false;
    }

    public void close() {
    }

    public void shutdown() {
    }
}
