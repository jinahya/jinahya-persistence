package com.github.jinahya.persistence.test.util.condition;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Disables the annotated test method when EclipseLink wove any of the named classes, or one of their superclasses.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote The classes are named, not given as class literals, and the annotation is for methods only. A class
 *         is woven only when it is loaded after a persistence unit registered EclipseLink's transformer, and loading
 *         it earlier keeps it unwoven for the rest of the run. A class literal in an annotation is loaded as soon as
 *         the annotations of the element it annotates are read, which JUnit does while it discovers the tests, before
 *         any {@code @BeforeAll}; a named class is loaded only when this condition is evaluated, which, for a method,
 *         is after the {@code @BeforeAll} methods of its class ran.
 * @see com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils#isWovenByEclipseLink(Class)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(WovenByEclipseLinkCondition.class)
public @interface DisabledIfWovenByEclipseLink {

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
