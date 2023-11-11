package ch.qligier.app.pixeldngfixer.exiftool;

import ch.qligier.app.pixeldngfixer.common.JpegRawPair;
import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.Tag;
import com.thebuzzmedia.exiftool.core.StandardTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The photo metadata reader and writer.
 *
 * @author Quentin Ligier
 **/
public class PhotoMetadataHandler {

    /**
     * ExifTool tags that are to be fixed.
     */
    public static final List<Tag> BROKEN_TAGS = List.of(
        StandardTag.ISO,
        StandardTag.APERTURE,
        StandardTag.SHUTTER_SPEED,
        StandardTag.METERING_MODE,
        StandardTag.FNUMBER,
        StandardTag.EXPOSURE_TIME,
        StandardTag.EXPOSURE_PROGRAM,
        StandardTag.GPS_LATITUDE,
        StandardTag.GPS_LATITUDE_REF,
        StandardTag.GPS_LONGITUDE,
        StandardTag.GPS_LONGITUDE_REF,
        StandardTag.GPS_ALTITUDE,
        StandardTag.GPS_ALTITUDE_REF,
        StandardTag.GPS_TIMESTAMP,
        StandardTag.SUB_SEC_TIME_ORIGINAL,
        CustomTag.GPS_IMG_DIRECTION,
        CustomTag.GPS_IMG_DIRECTION_REF,
        CustomTag.OFFSET_TIME
    );

    /**
     * The ExifTool pool size.
     */
    private static final int POOL_SIZE = 2;

    /**
     * The ExifTool interface.
     */
    private final ExifTool exifTool;

    /**
     * Constructor. It builds the default ExifTool interface, the executable must be in the path.
     */
    public PhotoMetadataHandler() {
        this.exifTool = new ExifToolBuilder()
            .withPoolSize(POOL_SIZE)
            .enableStayOpen()
            .build();
    }

    /**
     * A helper that retrieves and pretty-print the metadata of a pair of file, to facilitate comparison.
     *
     * @param filePair The file pair.
     * @throws IOException if an exception arises when running ExifTool.
     */
    public void showMetadata(final JpegRawPair filePair) throws IOException {
        final List<Tag> tags = Arrays.asList(StandardTag.values());
        final var metadataJpg = this.exifTool.getImageMeta(filePair.getJpegFile(), tags);
        final var metadataDng = this.exifTool.getImageMeta(filePair.getRawFile(), tags);

        for (final var tag : tags) {
            if (!metadataJpg.containsKey(tag) && !metadataDng.containsKey(tag)) {
                continue;
            }

            System.out.printf(
                "%1$30s %2$-30s %3$-30s %4$b%n",
                tag.toString(),
                metadataJpg.get(tag),
                metadataDng.get(tag),
                Objects.equals(metadataDng.get(tag), metadataJpg.get(tag))
            );
        }
        System.out.println();
    }

    /**
     * Copies metadata from one image to another. Only the metadata selected in {@code tags} are copied.
     *
     * @param filePair                 The pair of photos to process. The key is the metadata source, the value is the
     *                                 metadata destination.
     * @param tags                     The list of ExifTool tags to copy.
     * @param createBackupBeforeFixing Whether to create backups before fixing the destination files or not.
     * @throws IOException if an exception arises when running ExifTool.
     */
    public void copyMetadata(final JpegRawPair filePair,
                             final List<Tag> tags,
                             final boolean createBackupBeforeFixing) throws IOException {
        final var metadata = this.exifTool.getImageMeta(filePair.getJpegFile(), tags);

        final var options = new ArrayList<String>(3);
        options.add("-ignoreMinorErrors"); // Ignore minor errors and warnings.
        options.add("-fixBase"); // Fix the base for maker notes offsets.
        if (!createBackupBeforeFixing) {
            options.add("-overwrite_original"); // Overwrite the original FILE (instead of preserving it by adding
            // '_original' to the file name) when writing information to an image.
        }
        this.exifTool.setImageMeta(filePair.getRawFile(), () -> options, metadata);
    }
}
