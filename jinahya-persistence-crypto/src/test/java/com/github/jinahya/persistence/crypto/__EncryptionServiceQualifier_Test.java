package com.github.jinahya.persistence.crypto;

import jakarta.inject.Qualifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __EncryptionServiceQualifier}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __EncryptionServiceQualifier_Test {

    /**
     * A holder which uses the qualifier at each of the places an injection point can appear. This class existing at all
     * is the test: a {@code TYPE}-only qualifier would not compile here.
     */
    @SuppressWarnings({"unused"})
    static class InjectionPoints {

        @__EncryptionServiceQualifier
        private __EncryptionService field;

        InjectionPoints(@__EncryptionServiceQualifier final __EncryptionService parameter) {
        }

        @__EncryptionServiceQualifier
        __EncryptionService producer() {
            return null;
        }
    }

    @DisplayName("it is a CDI qualifier")
    @Test
    void __isAQualifier() {
        assertThat(__EncryptionServiceQualifier.class.getAnnotation(Qualifier.class)).isNotNull();
    }

    @DisplayName("it is applicable where an injection point can appear")
    @Test
    void __targetsInjectionPoints() {
        assertThat(__EncryptionServiceQualifier.class.getAnnotation(Target.class).value())
                .contains(ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER);
    }

    @DisplayName("a derived qualifier carries @Qualifier itself, not merely another qualifier")
    @Test
    void __derivedQualifierIsItselfAQualifier() {
        // meta-annotating with another qualifier does not make an annotation a CDI qualifier
        assertThat(_EncryptionServiceQualifier.class.getAnnotation(Qualifier.class)).isNotNull();
    }
}
