package com.github.jinahya.persistence.test.util.condition;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Disables the annotated test method on EclipseLink, unless EclipseLink wove every one of the named classes; the
 * method runs on any other provider.
 * <p>
 * For a test of what EclipseLink does only for a woven class, e.g. take an entity graph which names a basic
 * attribute, which it takes as a fetch group, and has a fetch group manager for only for a woven class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote The classes are named, and the annotation is for methods only, for the reason
 *         {@link DisabledIfWovenByEclipseLink} gives.
 * @see DisabledIfWovenByEclipseLink
 * @see com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils#isEclipseLink()
 * @see com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils#isWovenByEclipseLink(Class)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(UnwovenEclipseLinkCondition.class)
public @interface DisabledOnUnwovenEclipseLink {

    /**
     * Returns the binary names of the classes to check, e.g. {@code com.example.Foo}, or {@code com.example.Foo$Bar}
     * for a nested class.
     *
     * @return the binary names of the classes to check.
     */
    String[] value();

    /**
     * Returns a reason, reported when the annotated test is disabled.
     *
     * @return a reason, reported when the annotated test is disabled; an empty string for none.
     */
    String disabledReason() default "";
}
