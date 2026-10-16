package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;

import java.util.ArrayList;
import java.util.ServiceLoader;

/**
 * Finds the one {@link EntityEncryptionService} an application provides through {@link ServiceLoader}.
 * <p>
 * The service found is kept for the JVM: every converter of every persistence unit which has no injected service
 * shares it, and the class loader it was found through is the context class loader of the first conversion. Two
 * persistence units in one JVM which need two services use the provider modules' property, or injection.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class EntityEncryptionServices {

    private static volatile @org.jspecify.annotations.Nullable EntityEncryptionService loaded;

    /**
     * Returns the service named by a {@code META-INF/services/} entry, loading it on the first call.
     *
     * @return the service.
     * @throws IllegalStateException when no implementation is named, or more than one is.
     */
    static EntityEncryptionService load() {
        var result = loaded;
        if (result == null) {
            synchronized (EntityEncryptionServices.class) {
                result = loaded;
                if (result == null) {
                    result = loaded = find(Thread.currentThread().getContextClassLoader());
                }
            }
        }
        return result;
    }

    /**
     * Finds the one service named in {@code META-INF/services/} through the specified class loader, without keeping it.
     *
     * @param loader the class loader to look through; {@code null} for the system class loader.
     * @return the service found.
     * @throws IllegalStateException when no implementation is named, or more than one is.
     */
    static EntityEncryptionService find(final @org.jspecify.annotations.Nullable ClassLoader loader) {
        final var found = new ArrayList<EntityEncryptionService>();
        ServiceLoader.load(EntityEncryptionService.class, loader).forEach(found::add);
        if (found.isEmpty()) {
            throw new IllegalStateException(
                    "no " + EntityEncryptionService.class.getName() + " found; provide one as a bean, when the"
                    + " persistence provider creates its attribute converters through a bean container, or name it in"
                    + " META-INF/services/" + EntityEncryptionService.class.getName());
        }
        if (found.size() > 1) {
            throw new IllegalStateException(
                    "more than one " + EntityEncryptionService.class.getName() + " named in META-INF/services/: "
                    + found.stream().map(s -> s.getClass().getName()).toList());
        }
        return found.get(0);
    }

    private EntityEncryptionServices() {
        throw new AssertionError("instantiation is not allowed");
    }
}
