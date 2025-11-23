package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Inherited
@Retention(value = RUNTIME)
@Target({
        ElementType.TYPE
})
public @interface __EncryptedEntity {

    String DEFAULT_ENCRYPTION_IDENTIFIER = "encryptionIdentifier__";

    //    String encryptionIdentifierAttribute() default "__encryptionIdentifier";
    String encryptionIdentifierAttribute() default "";
}
