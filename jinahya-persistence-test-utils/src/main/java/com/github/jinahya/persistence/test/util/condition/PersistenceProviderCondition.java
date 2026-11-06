package com.github.jinahya.persistence.test.util.condition;

import com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Evaluates {@link EnabledOnEclipseLink}, {@link DisabledOnEclipseLink}, {@link EnabledOnHibernateOrm} and
 * {@link DisabledOnHibernateOrm}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class PersistenceProviderCondition implements ExecutionCondition {

    private static final ConditionEvaluationResult ENABLED =
            ConditionEvaluationResult.enabled("no persistence provider condition disables it");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
        final var element = context.getElement();
        final var eclipseLink = JinahyaPersistenceProviderTestUtils.isEclipseLink();
        final var hibernateOrm = JinahyaPersistenceProviderTestUtils.isHibernateOrm();
        final var disabledOnEclipseLink = AnnotationSupport.findAnnotation(element, DisabledOnEclipseLink.class);
        if (disabledOnEclipseLink.isPresent() && eclipseLink) {
            return ConditionEvaluationResult.disabled("disabled on EclipseLink",
                                                      disabledOnEclipseLink.get().disabledReason());
        }
        final var enabledOnEclipseLink = AnnotationSupport.findAnnotation(element, EnabledOnEclipseLink.class);
        if (enabledOnEclipseLink.isPresent() && !eclipseLink) {
            return ConditionEvaluationResult.disabled("enabled on EclipseLink only",
                                                      enabledOnEclipseLink.get().disabledReason());
        }
        final var disabledOnHibernateOrm = AnnotationSupport.findAnnotation(element, DisabledOnHibernateOrm.class);
        if (disabledOnHibernateOrm.isPresent() && hibernateOrm) {
            return ConditionEvaluationResult.disabled("disabled on Hibernate ORM",
                                                      disabledOnHibernateOrm.get().disabledReason());
        }
        final var enabledOnHibernateOrm = AnnotationSupport.findAnnotation(element, EnabledOnHibernateOrm.class);
        if (enabledOnHibernateOrm.isPresent() && !hibernateOrm) {
            return ConditionEvaluationResult.disabled("enabled on Hibernate ORM only",
                                                      enabledOnHibernateOrm.get().disabledReason());
        }
        return ENABLED;
    }
}
