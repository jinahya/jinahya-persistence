package com.github.jinahya.persistence.test.util.condition;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables the annotated test class or method only when Hibernate ORM is on the class path.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils#isHibernateOrm()
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(PersistenceProviderCondition.class)
public @interface EnabledOnHibernateOrm {

    /**
     * Returns a reason, reported when the annotated test is disabled.
     *
     * @return a reason, reported when the annotated test is disabled; an empty string for none.
     */
    String disabledReason() default "";
}
