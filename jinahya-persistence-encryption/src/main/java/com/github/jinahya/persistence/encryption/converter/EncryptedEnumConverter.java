package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfLang;

/**
 * The base of the converters of {@code enum} attributes: a constant is encoded by its {@link Enum#name() name}, never
 * its ordinal, so reordering constants is safe, and renaming one is a breaking change for the rows already written.
 * <p>
 * A converter has to name its type, so each enum type gets a subclass of its own, declared by the application. The
 * subclass declares {@code implements AttributeConverter<E, byte[]>} again, with its enum type: EclipseLink reads the
 * type arguments from the class's own interface clause only, and maps the column as a {@code VARCHAR} otherwise.
 * <pre>{@code
 * @Converter
 * public class EncryptedColorConverter extends EncryptedEnumConverter<Color>
 *         implements AttributeConverter<Color, byte[]> {
 *     public EncryptedColorConverter() { super(Color.class); }
 * }
 * }</pre>
 *
 * @param <E> the enum type.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class EncryptedEnumConverter<E extends Enum<E>> extends EncryptedAttributeConverter<E> {

    /**
     * Creates a new instance for the specified enum type.
     *
     * @param enumClass the enum type.
     */
    protected EncryptedEnumConverter(final Class<E> enumClass) {
        super(enumClass, OfLang::encodeEnum, b -> OfLang.decodeEnum(b, enumClass));
    }
}
