package com.github.jinahya.persistence.encryption.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.event.internal.DefaultFlushEntityEventListener;
import org.hibernate.event.spi.FlushEntityEvent;

import java.util.Objects;

/**
 * Hibernate ORM's default {@code FLUSH_ENTITY} listener, which also encrypts a dirty entity into the state its
 * {@code UPDATE} is built from.
 * <p>
 * The state is changed where Hibernate ORM itself lets an interceptor change it: after the entity is found dirty, and
 * before the update is scheduled, so that the columns changed here are the ones written, under
 * {@code @DynamicUpdate} too.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S110" // Inheritance tree of classes should not be too deep
})
class EncryptingFlushEntityEventListener extends DefaultFlushEntityEventListener {

    /**
     * Creates a new instance.
     *
     * @param engine the engine to encrypt with.
     */
    EncryptingFlushEntityEventListener(final HibernateEntityEncryptor engine) {
        super();
        this.engine = Objects.requireNonNull(engine, "engine is null");
    }

    /**
     * {@inheritDoc}
     *
     * @implNote When an interceptor, or a {@code @PreUpdate} callback, changed the state, Hibernate ORM copies
     *         the whole state back into the entity, the cleared plaintexts included; they are restored afterwards, as
     *         the callbacks left them.
     */
    @Override
    public void onFlushEntity(final FlushEntityEvent event) throws HibernateException {
        final var outer = restoration.get();
        restoration.remove();
        try {
            super.onFlushEntity(event);
        } finally {
            final var inner = restoration.get();
            if (outer == null) {
                restoration.remove();
            } else {
                restoration.set(outer);
            }
            if (inner != null) {
                inner.run();
            }
        }
    }

    @Override
    protected boolean invokeInterceptor(final FlushEntityEvent event) {
        // the interceptor, and the @PreUpdate callbacks, first: they may change the entity
        final var intercepted = super.invokeInterceptor(event);
        restoration.set(engine.encryptState(event));
        return intercepted;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final HibernateEntityEncryptor engine;

    // the restoration of the entity being flushed on this thread, set once its state is encrypted
    private final ThreadLocal<Runnable> restoration = new ThreadLocal<>();
}
