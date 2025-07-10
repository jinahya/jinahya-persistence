package com.github.jinahya.persistence.more;

import jakarta.validation.constraints.NotNull;

/**
 * An interface for defining enum constants with attribute values.
 * <p>
 * For example, you can define an enum constant with a string attribute value.
 * {@snippet lang = java:
 * @jakarta.persistence.Entity
 * class MyEntity {
 *
 *     @jakarta.annotation.Nullable
 *     public Integer getPayGrade() {
 *          return payGrade;
 *     }
 *
 *     public void setPayGrade(@jakarta.annotation.Nullable final Integer payGrade) {
 *          this.payGrade = payGrade;
 *     }
 *
 *     @jakarta.annotation.Nullable
 *     @jakarta.persistence.Transient
 *     public PayGrade getPayGradeAsEnum() {
 *          return java.util.Optional.ofNullable(getPayGrade())
 *                  .map(v -> __AttributeEnumUtils.valueOfAttributeValue(PayGrade.class, v))
 *                  .orElse(null);
 *     }
 *
 *     public void setPayGradeAsEnum(@jakarta.annotation.Nullable final PayGrade payGradeAsEnum) {
 *          setPayGrade(
 *                  java.util.Optional.ofNullable(payGradeAsEnum)
 *                          .map(__AttributeEnum::attributeValue)
 *                          .orElse(null)
 *         );
 *     }
 *
 *     @jakarta.annotation.Nullable
 *     @jakarta.persistence.Basic(optional = true)
 *     @jakarta.persistence.Column(name = "pay_grade", nullable = true)
 *     private Integer payGrade; // maps to an UNSIGNED INTEGER column
 * }
 *
 * public enum PayGrade implements __AttributeEnum<PayGrade, Integer> {
 *
 *     // ...
 *
 *     E8(8),
 *
 *     E9(9);
 *
 *     PayGrade(@jakarta.annotation.Nonnull final Integer attributeValue) {
 *         this.attributeValue = java.util.Objects.requireNonNull(attributeValue, "attributeValue is null");
 *     }
 *
 *     @Override public Integer attributeValue() {
 *         return attributeValue;
 *     }
 *
 *     @jakarta.annotation.Nonnull
 *     private final Integer attributeValue;
 * }
 *}
 *
 * @param <SELF>      self type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S114", // Interface names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
public interface __AttributeEnum<SELF extends Enum<SELF> & __AttributeEnum<SELF, ATTRIBUTE>, ATTRIBUTE> {

    /**
     * An interface for defining enum constants with string attribute values.
     *
     * @param <SELF> self type parameter
     */
    @SuppressWarnings({
            "java:S114" // Interface names should comply with a naming convention
    })
    interface __OfString<SELF extends Enum<SELF> & __OfString<SELF>> extends __AttributeEnum<SELF, String> {

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @apiNote The {@code attributeValue()} method of {@code __OfString} class returns
         *         {@link Enum#name() this.name()}.
         */
        @Override
        default String attributeValue() {
            return ((Enum<?>) this).name();
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
