package com.github.jinahya.persistence.test.util.condition;

import com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Evaluates {@link DisabledIfWovenByEclipseLink}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class WovenByEclipseLinkCondition implements ExecutionCondition {

    private static final ConditionEvaluationResult ENABLED =
            ConditionEvaluationResult.enabled("none of the named classes is woven by EclipseLink");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
        final var annotation = AnnotationSupport.findAnnotation(context.getElement(),
                                                                DisabledIfWovenByEclipseLink.class);
        if (annotation.isEmpty()) {
            return ENABLED;
        }
        final var classLoader = context.getRequiredTestClass().getClassLoader();
        for (final var name : annotation.get().value()) {
            final Class<?> type;
            try {
                type = Class.forName(name, false, classLoader);
            } catch (final ClassNotFoundException cnfe) {
                // a misspelt name would otherwise never disable anything
                throw new ExtensionConfigurationException("no class named " + name, cnfe);
            }
            if (JinahyaPersistenceProviderTestUtils.isWovenByEclipseLink(type)) {
                return ConditionEvaluationResult.disabled(name + " is woven by EclipseLink",
                                                          annotation.get().disabledReason());
            }
        }
        return ENABLED;
    }
}
