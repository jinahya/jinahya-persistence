package com.github.jinahya.persistence.encryption;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.ExcludeSuperclassListeners;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Finds the {@link PrePersist @PrePersist} and {@link PreUpdate @PreUpdate} callbacks which can run after an
 * {@link EntityEncryptionListener} for an entity class, as its annotations declare them.
 * <p>
 * Jakarta Persistence invokes, for each lifecycle event, the default listeners, then the listeners of the entity's
 * {@link EntityListeners @EntityListeners} — a superclass's before a subclass's, each in the order declared, unless
 * {@link ExcludeSuperclassListeners @ExcludeSuperclassListeners} cuts them off — and then the callback methods of the
 * entity class itself and its superclasses, a superclass's first. Anything after the encryption listener sees the
 * entity already encrypted, and anything it assigns to a plaintext then goes out in the {@code UPDATE} in the clear,
 * or leaves a stale ciphertext behind it.
 * <p>
 * Only annotations are read: callbacks declared in {@code orm.xml}, and an order rearranged there, are not seen. A
 * callback method is found whatever it does, and whether or not a subclass overrides it, so what is found may be more
 * than what runs.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/jinahya/jinahya-persistence/issues/101">#101</a>
 */
final class LateWriteCallbacks {

    /**
     * Returns a description of each {@code @PrePersist} or {@code @PreUpdate} callback which can run after the
     * specified listener class for the specified entity class.
     *
     * @param entityClass   the entity class.
     * @param listenerClass the class of the encryption listener at work.
     * @return descriptions of the callbacks found, such as {@code "@PreUpdate User.touch()"}; empty when there are
     *         none.
     */
    static List<String> find(final Class<?> entityClass, final Class<?> listenerClass) {
        final var found = new ArrayList<String>();
        // the listeners after the encryption listener; when it is not among those annotated, it was registered
        // elsewhere — as a default listener, or in orm.xml — and its position among them can't be told
        final var listeners = listeners(entityClass);
        final var position = listeners.indexOf(listenerClass);
        if (position >= 0) {
            for (final var listener : listeners.subList(position + 1, listeners.size())) {
                if (EntityEncryptionListener.class.isAssignableFrom(listener)) {
                    continue;
                }
                writeCallbacks(listener, found);
            }
        }
        // the entity's own callbacks always run after every listener
        writeCallbacks(entityClass, found);
        return List.copyOf(found);
    }

    /**
     * Returns the listener classes annotated for the specified entity class, in the order they are invoked.
     *
     * @param entityClass the entity class.
     * @return the listener classes, a superclass's first.
     */
    private static List<Class<?>> listeners(final Class<?> entityClass) {
        final var hierarchy = new ArrayList<Class<?>>();
        for (Class<?> c = entityClass; c != null && c != Object.class; c = c.getSuperclass()) {
            hierarchy.add(0, c);
        }
        final var listeners = new ArrayList<Class<?>>();
        for (final var c : hierarchy) {
            if (c.isAnnotationPresent(ExcludeSuperclassListeners.class)) {
                listeners.clear();
            }
            final var annotation = c.getAnnotation(EntityListeners.class);
            if (annotation != null) {
                listeners.addAll(Arrays.asList(annotation.value()));
            }
        }
        return listeners;
    }

    /**
     * Adds a description of each {@code @PrePersist} or {@code @PreUpdate} method of the specified class, or of any
     * of its superclasses, to the specified list.
     */
    private static void writeCallbacks(final Class<?> type, final List<String> found) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final Method method : c.getDeclaredMethods()) {
                if (method.isSynthetic() || method.isBridge()) {
                    continue;
                }
                if (method.isAnnotationPresent(PrePersist.class)) {
                    found.add("@PrePersist " + c.getName() + '.' + method.getName() + "()");
                }
                if (method.isAnnotationPresent(PreUpdate.class)) {
                    found.add("@PreUpdate " + c.getName() + '.' + method.getName() + "()");
                }
            }
        }
    }

    private LateWriteCallbacks() {
        throw new AssertionError("instantiation is not allowed");
    }
}
