package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(value = RUNTIME)
@Target({
        ElementType.TYPE
})
public @interface __EncryptedEntity {

    String value() default "cryptoAttribute";
}
