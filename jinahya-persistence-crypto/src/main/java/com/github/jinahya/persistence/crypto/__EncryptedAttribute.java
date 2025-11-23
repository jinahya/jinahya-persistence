package com.github.jinahya.persistence.crypto;

import jakarta.persistence.metamodel.Attribute;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Function;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(value = RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.METHOD
})
public @interface __EncryptedAttribute {

    Function<? extends Attribute<?, ?>, String> DEFAULT_ENCRYPTED_ATTRIBUTE_NAME = a -> a.getName() + "_encrypted__";

    // -----------------------------------------------------------------------------------------------------------------
    String encryptedAttribute() default "";
}
