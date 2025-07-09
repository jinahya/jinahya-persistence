package com.github.jinahya.persistence.more;

import jakarta.validation.constraints.NotNull;

public interface __AttributeEnum<ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE> {

    interface __OfString<ENUM extends Enum<ENUM> & __OfString<ENUM>> extends __AttributeEnum<ENUM, String> {

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @apiNote The {@code attributeValue()} method of {@code __OfString} class returns
         *         {@link Enum#name() this.name()}.
         */
        @Override
        @SuppressWarnings({"unchecked"})
        default String attributeValue() {
            return ((Enum<ENUM>) this).name();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the attribute value of this enum constant.
     *
     * @return the attribute value of this enum constant.
     */
    @NotNull
    ATTRIBUTE attributeValue();
}
