package com.github.jinahya.persistence.crypto;

import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Meta-annotating with another qualifier does NOT make an annotation a CDI qualifier; it has to
// carry @Qualifier itself.
@Qualifier
@__EncryptionServiceQualifier
@Retention(
        RetentionPolicy.RUNTIME
)
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER
})
public @interface _EncryptionServiceQualifier {

}
