package ch.qligier.app.pixeldngfixer;

import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The finder of Pixel photo pairs. A photo pair is a JPG and a DNG (RAW) file from the same photo (identifier by
 * their same filename).
 *
 * @author Quentin Ligier
 **/
public class PixelPhotoPairFinder {

    /**
     * Searches for Pixel photo pairs in the given {@code directory}.
     *
     * @param directory The directory in which to search.
     * @return a list of found Pixel photo pairs. The key is the JPG file, the value is the DNG file.
     * @throws FileNotFoundException if the {@code directory} does not exist or is not readable.
     */
    public List<Pair<File, File>> findPixelPhotos(final File directory) throws FileNotFoundException {
        if (!directory.isDirectory() || !directory.canRead()) {
            throw new FileNotFoundException("The directory is not found");
        }
        final var files = Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                .filter(file -> file.isFile() && file.getName().startsWith("PXL_"))
                .filter(file -> file.getName().endsWith(".dng") || file.getName().endsWith(".jpg"))
                .sorted()
                .toList();
        final var foundPairs = new ArrayList<Pair<File, File>>(0);

        String lastDngFilename = null;
        File lastDngFile = null;
        for (final var file : files) {
            final var filename = file.getName().substring(0, file.getName().length() - 4);
            if (file.getName().endsWith(".dng")) {
                lastDngFile = file;
                lastDngFilename = filename;
            } else if (file.getName().endsWith(".jpg") && filename.equals(lastDngFilename)) {
                foundPairs.add(new Pair<>(file, lastDngFile));
            }
        }
        return foundPairs;
    }
}
