package com.github.jinahya.persistence.test.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;

/**
 * Produces, as CDI beans, the {@link EntityManagerFactory} of the persistence unit declared in
 * {@code META-INF/persistence.xml}, and the {@link EntityManager}s it creates.
 * <p>
 * The unit is bootstrapped the Java SE way, through {@link Persistence#createEntityManagerFactory(String)}, so the
 * provider, the JDBC properties, and the listed entity classes are whatever the descriptor says -- which is how the
 * same container works under either persistence provider profile without a change here.
 * <p>
 * The two scopes are deliberately different, and mirror the two lifetimes:
 * <ul>
 * <li>the factory is {@link ApplicationScoped}, because opening one is what costs -- the provider reads the
 * descriptor, builds the metamodel and generates the schema exactly once per container;</li>
 * <li>an entity manager is {@link Dependent}, and so is created afresh at each injection point and destroyed with
 * whatever it was injected into -- the persistence context is short-lived, and sharing one across tests would leak
 * managed instances from one into the next.</li>
 * </ul>
 * Each has a {@link Disposes disposer}, so neither is left open when the container shuts down.
 * <p>
 * Both are qualified with {@link __SpecPU}, which names the unit they belong to; a bare
 * {@code EntityManager} injection point resolves to nothing here, on purpose.
 * <p>
 * <strong>The disposers are the reason to produce these through CDI at all.</strong> A test which opens a factory by
 * hand has to close it by hand, in an {@code @AfterAll} which runs even when a test throws; here, destroying the
 * container does it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __SpecPU
 * @see <a href="https://jakarta.ee/specifications/cdi/4.1/">Jakarta Contexts and Dependency Injection 4.1</a>
 */
@ApplicationScoped
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class __PersistenceProducer {

    /**
     * A CDI qualifier for the beans of the {@value __PersistenceProducer#PERSISTENCE_UNIT_NAME} persistence unit.
     * <p>
     * An {@link jakarta.persistence.EntityManagerFactory} and an {@link jakarta.persistence.EntityManager} are types a
     * container may well hold more than one of -- one per persistence unit -- so producing them unqualified would make
     * a second unit an ambiguous-resolution failure rather than a second bean. The qualifier names the unit at both
     * ends: the producer declares which unit it produces, and an injection point declares which unit it wants.
     * <p>
     * Note that declaring it also removes {@link jakarta.enterprise.inject.Default @Default} from those beans: an
     * injection point which asks for a bare {@code EntityManager} is left unsatisfied, deliberately, so that a unit is
     * never picked by accident.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see __PersistenceProducer
     */
    @Documented
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
    public @interface __SpecPU {

        /**
         * An {@link AnnotationLiteral} of {@link __SpecPU}, for selecting a bean programmatically.
         *
         * @see jakarta.enterprise.inject.Instance#select(Class, java.lang.annotation.Annotation...)
         */
        final class Literal extends AnnotationLiteral<__SpecPU> implements __SpecPU {

            /**
             * The single instance of this literal; the annotation declares no member, so one is enough.
             */
            public static final Literal INSTANCE = new Literal();

            private static final long serialVersionUID = 1L;

            private Literal() {
                super();
            }
        }
    }

// ---------------------------------------------------------------------------------------------------------------------

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * The name of the persistence unit this producer bootstraps; {@value}.
     *
     * @see #produceEntityManagerFactory()
     */
    public static final String PERSISTENCE_UNIT_NAME = "__specPU";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Produces the factory of the {@value #PERSISTENCE_UNIT_NAME} persistence unit.
     *
     * @return a new factory, created from {@code META-INF/persistence.xml}.
     * @implNote {@link ApplicationScoped}: one factory per container. The returned type is an interface, so the
     *         client proxy Weld injects in its place is a plain one.
     * @see Persistence#createEntityManagerFactory(String)
     */
    @Produces
    @__SpecPU
    @ApplicationScoped
    EntityManagerFactory produceEntityManagerFactory() {
        logger.log(System.Logger.Level.DEBUG, "creating a factory of {0}", PERSISTENCE_UNIT_NAME);
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    /**
     * Closes the factory this producer produced, when the container destroys it.
     *
     * @param entityManagerFactory the factory to close.
     * @implNote Guarded by {@link EntityManagerFactory#isOpen()}: a test which closed it itself, which is
     *         allowed, must not make the shutdown fail.
     */
    void disposeEntityManagerFactory(@Disposes @__SpecPU final EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory.isOpen()) {
            logger.log(System.Logger.Level.DEBUG, "closing {0}", entityManagerFactory);
            entityManagerFactory.close();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Produces a new entity manager, from the factory of the {@value #PERSISTENCE_UNIT_NAME} persistence unit.
     *
     * @param entityManagerFactory the factory, injected by the {@link __SpecPU} qualifier; the parameter of a producer
     *                             method is an injection point.
     * @return a new entity manager.
     * @implNote {@link Dependent}, not {@link ApplicationScoped}: a persistence context is not something to
     *         share. Note that this makes the transaction the caller's to manage --
     *         {@link EntityManager#getTransaction()}, begun and committed around the work -- for a resource-local unit
     *         has no container transaction to join.
     * @see EntityManagerFactory#createEntityManager()
     */
    @Produces
    @__SpecPU
    @Dependent
    EntityManager produceEntityManager(@__SpecPU final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Closes an entity manager this producer produced, when the container destroys it.
     *
     * @param entityManager the entity manager to close.
     * @implNote Guarded by {@link EntityManager#isOpen()}, for the same reason the factory's disposer is: an
     *         entity manager used in a try-with-resources is already closed by the time it is destroyed.
     */
    void disposeEntityManager(@Disposes @__SpecPU final EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
