package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnumTest<E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE> {

    public abstract static class __OfStringTest<E extends Enum<E> & __AttributeEnum.__OfString<E>>
            extends __AttributeEnumTest<E, String> {

        /**
         * Creates a new instance for testing the specified enum class.
         *
         * @param enumClass the enum class to test.
         * @see #enumClass;
         */
        protected __OfStringTest(final Class<E> enumClass) {
            super(enumClass, String.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified enum class.
     *
     * @param enumClass the enum class to test.
     * @see #enumClass;
     */
    protected __AttributeEnumTest(final Class<E> enumClass, final Class<ATTRIBUTE> attributeClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Accepts each pair of enum constant and attribute value to the specified consumer.
     *
     * @param consumer the consumer.
     */
    protected void acceptEnumConstantAndAttributeValue(final BiConsumer<? super E, ? super ATTRIBUTE> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        for (final E enumConstant : enumClass.getEnumConstants()) {
            final var attributeValue = enumConstant.attributeValue();
            consumer.accept(enumConstant, attributeValue);
        }
    }

    /**
     * Applies a stream of all enum constants, of {@link #enumClass}, to the specified function, and returns the
     * result.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #acceptEnumConstantStream(Consumer)
     */
    protected <R> R applyEnumConstantStream(final Function<? super Stream<E>, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<E>builder();
        acceptEnumConstantAndAttributeValue((ec, av) -> builder.add(ec));
        return function.apply(builder.build());
    }

    /**
     * Accepts a stream of all enum constants, of {@link #enumClass}, to the specified consumer.
     *
     * @param consumer the consumer.
     * @see #applyEnumConstantStream(Function)
     */
    protected void acceptEnumConstantStream(final Consumer<? super Stream<E>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyEnumConstantStream(s -> {
            consumer.accept(s);
            return null;
        });
    }

    protected <R> R applyAttributeValueStream(final Function<? super Stream<ATTRIBUTE>, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        final var builder = Stream.<ATTRIBUTE>builder();
        acceptEnumConstantAndAttributeValue((ec, av) -> builder.add(av));
        return function.apply(builder.build());
    }

    protected void acceptAttributeValueStream(final Consumer<? super Stream<ATTRIBUTE>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        applyAttributeValueStream(s -> {
            consumer.accept(s);
            return null;
        });
    }

    // ------------------------------------------------------------------------------------------------------- enumClass

    // -------------------------------------------------------------------------------------------------- attributeClass

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The enum class to test.
     */
    protected final Class<E> enumClass;

    /**
     * The type attribute of the {@link #enumClass}.
     */
    protected final Class<ATTRIBUTE> attributeClass;
}
