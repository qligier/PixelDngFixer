package ch.qligier.app.pixeldngfixer.common;

import javafx.util.Pair;

import java.io.File;

/**
 * A wrapper for a pair of pictures: a JPEG and a RAW file that have the same name.
 *
 * @author Quentin Ligier
 **/
public class JpegRawPair extends Pair<File, File> {

    public JpegRawPair(final File jpegFile, final File rawFile) {
        super(jpegFile, rawFile);
    }

    public File getJpegFile() {
        return getKey();
    }

    public File getRawFile() {
        return getValue();
    }
}
