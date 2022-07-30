package ch.qligier.app.pixeldngfixer.exiftool;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.Tag;
import com.thebuzzmedia.exiftool.core.StandardTag;
import javafx.util.Pair;

import java.io.File;
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
     * The ExifTool interface.
     */
    private final ExifTool exifTool;

    /**
     * Constructor. It builds the default ExifTool interface, the executable must be in the path.
     */
    public PhotoMetadataHandler() {
        this.exifTool = new ExifToolBuilder()
            .withStrategy(new CustomExecutionStrategy())
            .build();
    }

    /**
     * @param filePair
     * @throws IOException
     */
    public void showMetadata(final Pair<File, File> filePair) throws IOException {
        final List<Tag> tags = Arrays.asList(StandardTag.values());
        final var metadataJpg = this.exifTool.getImageMeta(filePair.getKey(), tags);
        final var metadataDng = this.exifTool.getImageMeta(filePair.getValue(), tags);

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
     * @param createBackupBeforeFixing Whether to create backups before fixing the destination files or not.
     * @throws IOException if an exception arises when running ExifTool.
     * @params tags The list of ExifTool tags to copy.
     */
    public void copyMetadata(final Pair<File, File> filePair,
                             final List<Tag> tags,
                             final boolean createBackupBeforeFixing) throws IOException {
        final var metadata = this.exifTool.getImageMeta(filePair.getKey(), tags);

        final var options = new ArrayList<String>(1);
        options.add("-F");
        if (!createBackupBeforeFixing) {
            options.add("-overwrite_original");
        }
        this.exifTool.setImageMeta(filePair.getValue(), () -> options, metadata);
    }
}
