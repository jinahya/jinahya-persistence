package com.github.jinahya.persistence.crypto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@__EncryptionServiceQualifier
@Retention(
        RetentionPolicy.RUNTIME
)
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.PARAMETER
})
public @interface _EncryptionServiceQualifier {

}
