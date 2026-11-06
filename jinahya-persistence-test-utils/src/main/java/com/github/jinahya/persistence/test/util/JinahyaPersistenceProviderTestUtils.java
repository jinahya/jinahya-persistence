package com.github.jinahya.persistence.test.util;

import java.lang.invoke.MethodHandles;

/**
 * Utilities for telling which persistence provider the tests run on, for those tests which have to tell.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JinahyaPersistenceProviderTestUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of EclipseLink's persistence provider class. The value is {@value}.
     */
    public static final String ECLIPSELINK_PERSISTENCE_PROVIDER = "org.eclipse.persistence.jpa.PersistenceProvider";

    /**
     * The name of Hibernate ORM's persistence provider class. The value is {@value}.
     */
    public static final String HIBERNATE_ORM_PERSISTENCE_PROVIDER = "org.hibernate.jpa.HibernatePersistenceProvider";

    /**
     * The name of the interface which EclipseLink adds to a class it weaves. The value is {@value}.
     */
    public static final String ECLIPSELINK_PERSISTENCE_WEAVED =
            "org.eclipse.persistence.internal.weaving.PersistenceWeaved";

    // -----------------------------------------------------------------------------------------------------------------

    // looks the class up by its name, without initializing it, so that this compiles, and answers false, without the
    // class on the class path
    private static boolean isOnClassPath(final String className) {
        try {
            Class.forName(className, false, JinahyaPersistenceProviderTestUtils.class.getClassLoader());
            return true;
        } catch (final ClassNotFoundException cnfe) {
            return false;
        }
    }

    // ----------------------------------------------------------------------------------------------------- Eclipselink

    /**
     * Tells whether EclipseLink is on the class path.
     *
     * @return {@code true} when {@value #ECLIPSELINK_PERSISTENCE_PROVIDER} is on the class path; {@code false}
     *         otherwise.
     * @see #isHibernateOrm()
     */
    public static boolean isEclipseLink() {
        return isOnClassPath(ECLIPSELINK_PERSISTENCE_PROVIDER);
    }

    /**
     * Tells whether EclipseLink wove the specified class, or one of its superclasses.
     *
     * @param type the class to check.
     * @return {@code true} when the {@code type}, or one of its superclasses, implements
     *         {@value #ECLIPSELINK_PERSISTENCE_WEAVED}; {@code false} otherwise.
     * @implNote The interface is matched by its name, so this compiles, and answers {@code false}, without
     *         EclipseLink on the class path.
     */
    public static boolean isWovenByEclipseLink(final Class<?> type) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (final var i : c.getInterfaces()) {
                if (ECLIPSELINK_PERSISTENCE_WEAVED.equals(i.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    // --------------------------------------------------------------------------------------------------- Hibernate-ORM

    /**
     * Tells whether Hibernate ORM is on the class path.
     *
     * @return {@code true} when {@value #HIBERNATE_ORM_PERSISTENCE_PROVIDER} is on the class path; {@code false}
     *         otherwise.
     * @see #isEclipseLink()
     */
    public static boolean isHibernateOrm() {
        return isOnClassPath(HIBERNATE_ORM_PERSISTENCE_PROVIDER);
    }

    // -----------------------------------------------------------------------------------------------------------------

    private JinahyaPersistenceProviderTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
