package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing subclass of {@link __AttributeEnum}.
 *
 * @param <ENUM>         enum type parameter
 * @param <ATTRIBUTE> attribute type parameter
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnumTest<ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
        extends ___AttributeEnumTest<ENUM, ATTRIBUTE> {

    public abstract static class __OfStringTest<ENUM extends Enum<ENUM> & __AttributeEnum.__OfString<ENUM>>
            extends __AttributeEnumTest<ENUM, String> {

        /**
         * Creates a new instance for testing the specified enum class.
         *
         * @param enumClass the enum class to test.
         * @see #enumClass
         */
        protected __OfStringTest(final Class<ENUM> enumClass) {
            super(enumClass, String.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified enum class.
     *
     * @param enumClass the enum class to test.
     * @see #enumClass
     */
    protected __AttributeEnumTest(final Class<ENUM> enumClass, final Class<ATTRIBUTE> attributeClass) {
        super(enumClass, attributeClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("no nulls in all attributeValues")
    @Test
    protected void attributeValue_NotNull_EachEnumConstant() {
        acceptEnumConstantAndAttributeValue((ec, av) -> {
            assertThat(av)
                    .as("attributeValue of %s: %s", ec, av)
                    .isNotNull();
        });
    }

    @DisplayName("no duplicates in all attributeValues")
    @Test
    protected void attributeValues_NoDuplicates_AllEnumConstants() {
        applyAttributeValueStream(s -> {
            assertThat(s)
                    .as("attributeValues of %s", enumClass)
                    .doesNotHaveDuplicates();
            return null;
        });
    }
}
