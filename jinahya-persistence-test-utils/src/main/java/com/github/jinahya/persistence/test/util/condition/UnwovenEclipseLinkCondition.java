package com.github.jinahya.persistence.test.util.condition;

import com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Evaluates {@link DisabledOnUnwovenEclipseLink}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class UnwovenEclipseLinkCondition implements ExecutionCondition {

    private static final ConditionEvaluationResult ENABLED =
            ConditionEvaluationResult.enabled("not on EclipseLink, or every named class is woven by it");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
        final var annotation = AnnotationSupport.findAnnotation(context.getElement(),
                                                                DisabledOnUnwovenEclipseLink.class);
        if (annotation.isEmpty() || !JinahyaPersistenceProviderTestUtils.isEclipseLink()) {
            return ENABLED;
        }
        for (final var name : annotation.get().value()) {
            final var type = WovenByEclipseLinkCondition.load(context, name);
            if (!JinahyaPersistenceProviderTestUtils.isWovenByEclipseLink(type)) {
                return ConditionEvaluationResult.disabled(name + " is not woven by EclipseLink",
                                                          annotation.get().disabledReason());
            }
        }
        return ENABLED;
    }
}
