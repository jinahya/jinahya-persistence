package com.github.jinahya.persistence.encryption;

import java.util.ArrayList;
import java.util.ServiceLoader;

/**
 * Finds the one {@link EntityEncryptionService} an application provides through {@link ServiceLoader}.
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
                    result = loaded = find();
                }
            }
        }
        return result;
    }

    private static EntityEncryptionService find() {
        final var found = new ArrayList<EntityEncryptionService>();
        ServiceLoader.load(EntityEncryptionService.class).forEach(found::add);
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
