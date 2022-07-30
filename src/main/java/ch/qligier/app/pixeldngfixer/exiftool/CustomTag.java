package ch.qligier.app.pixeldngfixer.exiftool;

import com.thebuzzmedia.exiftool.Tag;

/**
 * Custom tags used in {@link PhotoMetadataHandler} that are missing from the
 * {@link com.thebuzzmedia.exiftool.core.StandardTag}s.
 *
 * @author Quentin Ligier
 **/
public enum CustomTag implements Tag {

    GPS_IMG_DIRECTION("GPSImgDirection", Type.STRING),
    GPS_IMG_DIRECTION_REF("GPSImgDirectionRef", Type.STRING),
    OFFSET_TIME("OffsetTime", Type.STRING),
    HDRP_MAKER_NOTE("HDRPMakerNote", Type.STRING);

    private final String name;
    private final Type type;

    private CustomTag(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.name;
    }

    public <T> T parse(String value) {
        return this.type.parse(value);
    }

    private enum Type {
        STRING {
            @Override
            public <T> T parse(String value) {
                return (T) value;
            }
        };

        public abstract <T> T parse(String var1);
    }
}
