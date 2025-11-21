package com.github.jinahya.persistence.crypto;

import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Retention(
        RetentionPolicy.RUNTIME
)
@Target({
        ElementType.TYPE
})
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public @interface __EncryptionServiceQualifier {

}
