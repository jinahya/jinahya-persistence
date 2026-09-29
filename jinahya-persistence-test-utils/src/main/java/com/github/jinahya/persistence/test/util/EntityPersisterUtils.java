package com.github.jinahya.persistence.test.util;

import com.github.jinahya.object.randomizer.ObjectRandomizer;
import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import jakarta.persistence.EntityManager;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link EntityPersister}.
 * <p>
 * The {@link #newPersistedInstanceOf(EntityManager, Class)} method chains the two roles: an entity instance is
 * instantiated and randomized by the {@link ObjectRandomizer} located for the entity class, then handed to the
 * {@link EntityPersister} located for it.
 *
 * <h2>The naming convention</h2>
 * A persister is located for an entity class by name. The persister class is a <em>sibling</em> of the entity class
 * &mdash; declared in the same package, beside it &mdash; which implements {@link EntityPersister} and carries a
 * postfix of either {@code "Persister"} or {@code "_Persister"}. For an entity class {@code Foo}, that is
 * {@code FooPersister}, probed first, and then {@code Foo_Persister}.
 * <p>
 * The convention spans source sets: a {@code Foo} declared in {@code main} and a {@code FooPersister} declared in
 * {@code test} are the same package, and both are on the test classpath.
 * <p>
 * A class which is not a sibling of the entity class is never located: neither a local nor an anonymous class, which
 * can not carry the required name, nor a class nested inside the entity class, which would have to be declared in the
 * source of the entity class itself. A class nested in an entity class of {@code main} therefore has to be declared as
 * a top-level class to be persistable here. Note that a subclass of a class which has a persister is located by the
 * convention, and not by the persister of its superclass &mdash; though a persister <em>declared</em> for a superclass
 * persists instances of the subclass perfectly well, which is what the acceptance check below allows for.
 * <p>
 * A located class is instantiated reflectively, and so has to declare an accessible no-argument constructor.
 *
 * <h2>Absence and failure</h2>
 * A lookup here returns an {@link Optional}, and an empty one carries no reason: the convention may name nothing at
 * all, or it may name a class which is not an {@link EntityPersister}. Neither is raised &mdash; a caller which needs
 * to tell them apart does its own checking &mdash; though each is logged where it is passed over, the one which looks
 * like a misconfiguration at {@link System.Logger.Level#WARNING WARNING}.
 * <p>
 * Two things do fail: a class which the convention locates, and which is therefore meant to be used, but which can not
 * be instantiated; and one which is located but accepts no instance of the class it was located for.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityPersister
 * @see #newPersistedInstanceOf(EntityManager, Class)
 */
public final class EntityPersisterUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * The postfixes of the {@linkplain EntityPersisterUtils naming convention}, in the order they are probed.
     */
    private static final String[] POSTFIXES = {"Persister", "_Persister"};

    /**
     * Locates, by the {@linkplain EntityPersisterUtils naming convention}, the persister class of the specified entity
     * class.
     *
     * @param targetClass the entity class whose persister class is located.
     * @return an optional of the persister class of the {@code targetClass}; {@code empty} when the convention names
     *         no usable one.
     * @throws NullPointerException when the {@code targetClass} is {@code null}.
     * @implNote The postfixes are probed in order, and the first candidate which exists <em>and</em> implements
     *         {@link EntityPersister} is taken; whether it can be instantiated is left to
     *         {@link _Utils#newInstance(Class)}.
     *         <p>
     *         A candidate is passed over, rather than failed on, when it does not exist, when it can not be loaded, and
     *         when it does not implement {@link EntityPersister} -- the probe simply continues with the next postfix,
     *         and an empty optional carries no reason. The last two are a misconfiguration all the same, so each is
     *         logged at {@link System.Logger.Level#WARNING WARNING}; a name which merely does not exist is logged at
     *         {@link System.Logger.Level#TRACE TRACE}.
     */
    static Optional<Class<?>> persisterClassOf(final Class<?> targetClass) {
        Objects.requireNonNull(targetClass, "targetClass is null");
        // the class loader of the targetClass, rather than the one of this class, for the targetClass may have been
        // loaded by an other class loader
        final var classLoader = Optional.ofNullable(targetClass.getClassLoader())
                .orElseGet(EntityPersisterUtils.class::getClassLoader);
        return Arrays.stream(POSTFIXES)
                .map(p -> targetClass.getName() + p)
                .<Class<?>>map(n -> {
                    final Class<?> clazz;
                    try {
                        // no initialization; the class is merely being probed
                        clazz = Class.forName(n, false, classLoader);
                    } catch (final ClassNotFoundException cnfe) {
                        logger.log(System.Logger.Level.TRACE, "no class named {0}; target: {1}", n, targetClass);
                        return null;
                    } catch (final LinkageError le) {
                        // Class.forName links the class, which resolves its superclass; a candidate whose supertype
                        // is missing, or is otherwise unloadable, therefore fails with an Error rather than an
                        // exception. A probe is speculative, so this does not end it -- but, unlike a name which
                        // simply does not exist, it is a misconfiguration worth seeing.
                        logger.log(System.Logger.Level.WARNING,
                                   "failed to load a candidate; className: " + n + "; probing further", le);
                        return null;
                    }
                    if (!EntityPersister.class.isAssignableFrom(clazz)) {
                        // the name claims a role which the class does not fill; the probe goes on, as it does for a
                        // name which does not exist, but -- unlike that one -- this is worth seeing
                        logger.log(System.Logger.Level.WARNING, "not an {0}; class: {1}, target: {2}",
                                   EntityPersister.class, clazz, targetClass);
                        return null;
                    }
                    logger.log(System.Logger.Level.TRACE, "located {0}; target: {1}", clazz, targetClass);
                    return clazz;
                })
                .filter(Objects::nonNull)
                .findFirst();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Requires that the specified persister, located for the specified entity class, accepts instances of it.
     *
     * @param targetClass the entity class the {@code persister} was located for.
     * @param persister   the located persister.
     * @param <T>         the entity type parameter.
     * @return the {@code persister}.
     * @throws RuntimeException when the class the {@code persister} is declared for is neither the
     *                          {@code targetClass} nor a superclass of it.
     * @apiNote A persister is a <em>consumer</em>, and so is contravariant in the class it is declared for:
     *         one declared for a superclass of the {@code targetClass} accepts instances of the {@code targetClass},
     *         while one declared for a subclass, or for an unrelated class, does not. This is what lets an entity
     *         hierarchy share one implementation -- a {@code _RgbaEntity_Persister extends __MappedRgbaPersister},
     *         declared for {@code __MappedRgba}, persists an {@code _RgbaEntity} -- without every subclass
     *         re-declaring its target class.
     *         <p>
     *         Unlike a {@link ObjectRandomizer randomizer}, which is a producer, and is judged on what it returned, a
     *         persister is judged on its declaration: applying it <em>is</em> the side effect, so there is nothing to
     *         inspect afterwards which has not already happened. Only an {@link AbstractEntityPersister} carries that
     *         declaration at runtime; one which implements {@link EntityPersister} directly leaves nothing but an
     *         erased type parameter, so there is nothing to check, and it is taken as it is.
     */
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
    private static <T> EntityPersister<T> requireAccepting(final Class<?> targetClass,
                                                           final EntityPersister<T> persister) {
        if (persister instanceof AbstractEntityPersister<?> abstractPersister
            && !abstractPersister.entityClass.isAssignableFrom(targetClass)) {
            throw new RuntimeException(
                    persister + ", located for " + targetClass + ", accepts only " + abstractPersister.entityClass
            );
        }
        return persister;
    }

    /**
     * Returns, an optional of, a new instance of the persister class located for the specified entity class by the
     * {@linkplain EntityPersisterUtils naming convention}.
     * <p>
     * An empty optional carries no reason: the convention names no class for the {@code targetClass}, or the class it
     * names is not an {@link EntityPersister}. A class which <em>is</em> located, though, is one the developer meant to
     * be used, so failing to instantiate it, or finding that it accepts no instance of the {@code targetClass}, is a
     * fault, and is raised rather than reported as an absence.
     *
     * @param targetClass the entity class.
     * @param <T>         the entity type parameter.
     * @return an optional of a new instance of the persister located for the {@code targetClass}; {@code empty} when
     *         the convention names no usable one.
     * @throws NullPointerException when the {@code targetClass} is {@code null}.
     * @throws RuntimeException     when the located persister can not be instantiated, or accepts no instance of the
     *                              {@code targetClass}.
     * @see #newPersistedInstanceOf(EntityManager, Class)
     */
    @SuppressWarnings({
            "unchecked"
    })
    public static <T> Optional<EntityPersister<T>> newPersisterInstanceOf(final Class<T> targetClass) {
        return persisterClassOf(targetClass)
                .map(c -> (EntityPersister<T>) _Utils.newInstance(c))
                .map(p -> requireAccepting(targetClass, p));
    }

    /**
     * Returns a new persisted instance of the specified entity class, using the randomizer and the persister located
     * for it.
     *
     * @param entityManager an entity manager.
     * @param entityClass   the entity class to instantiate, randomize, and persist.
     * @param <T>           the entity type parameter.
     * @return a new persisted instance of the {@code entityClass}; persisted, and not flushed.
     * @throws NullPointerException     when either argument is {@code null}.
     * @throws IllegalArgumentException when no usable persister, or no usable randomized instance, is obtained for the
     *                                  {@code entityClass}.
     * @throws RuntimeException         when the located persister can not be instantiated, or accepts no instance of
     *                                  the {@code entityClass}.
     * @apiNote Unlike the lookups above, an absence is an error here: this method exists to hand back an
     *         instance, and has nothing to hand back without both halves.
     * @see #newPersisterInstanceOf(Class)
     * @see ObjectRandomizerUtils#newRandomizedInstanceOf(Class)
     */
    public static <T> T newPersistedInstanceOf(final EntityManager entityManager, final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        // the persister is resolved first: randomizing an instance which can not then be persisted is wasted work
        final var persisterInstance = newPersisterInstanceOf(entityClass)
                .orElseThrow(() -> new IllegalArgumentException("no persister instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "persister instance: {0}", persisterInstance);
        final T entityInstance = ObjectRandomizerUtils.newRandomizedInstanceOf(entityClass)
                .orElseThrow(() -> new IllegalArgumentException("no randomized instance for " + entityClass));
        logger.log(System.Logger.Level.TRACE, "entity instance: {0}", entityInstance);
        return persisterInstance.apply(entityManager, entityInstance);
    }

    // ---------------------------------------------------------------------------------------------------------------------
    private EntityPersisterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
