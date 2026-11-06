package com.github.jinahya.persistence.test.util.condition;

import com.github.jinahya.persistence.test.util.JinahyaPersistenceProviderTestUtils;
import com.github.jinahya.persistence.test.util.JinahyaPersistenceTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link DisabledIfWovenByEclipseLink} and {@link DisabledOnUnwovenEclipseLink}; each test asserts what its
 * own condition implies, so a condition which let it run when it should not have fails it.
 * <p>
 * The class is named, never given as a literal, so that nothing here loads it before the unit is open.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class WovenByEclipseLinkCondition_Test {

    private static final String PERSISTENCE_UNIT_NAME = "__specPU";

    private static final String ENTITY_CLASS_NAME = "com.github.jinahya.persistence.test.util.spec._Address";

    // opens the unit, so that EclipseLink, woven, registers its transformer before the entity class is loaded
    @BeforeAll
    static void openPersistenceUnit() {
        JinahyaPersistenceTestUtils.applyCachedEntityManagerFactory(PERSISTENCE_UNIT_NAME, f -> f);
    }

    private static boolean woven() throws ClassNotFoundException {
        return JinahyaPersistenceProviderTestUtils.isWovenByEclipseLink(Class.forName(ENTITY_CLASS_NAME));
    }

    @DisabledIfWovenByEclipseLink(value = ENTITY_CLASS_NAME,
                                  disabledReason = "verifying @DisabledIfWovenByEclipseLink")
    @DisplayName("@DisabledIfWovenByEclipseLink -> runs only when the class is not woven")
    @Test
    void disabledIfWovenByEclipseLink__() throws ClassNotFoundException {
        assertThat(woven()).isFalse();
    }

    @DisabledOnUnwovenEclipseLink(value = ENTITY_CLASS_NAME,
                                  disabledReason = "verifying @DisabledOnUnwovenEclipseLink")
    @DisplayName("@DisabledOnUnwovenEclipseLink -> runs only off EclipseLink, or when the class is woven")
    @Test
    void disabledOnUnwovenEclipseLink__() throws ClassNotFoundException {
        assertThat(!JinahyaPersistenceProviderTestUtils.isEclipseLink() || woven()).isTrue();
    }
}
