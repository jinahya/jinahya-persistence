package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.EntityEncryptor;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.internal.DefaultFlushEntityEventListener;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;

import java.lang.invoke.MethodHandles;

/**
 * Integrates the encryption of {@link com.github.jinahya.persistence.encryption.EncryptedAttribute encrypted
 * attributes} with Hibernate ORM, in place of
 * {@link com.github.jinahya.persistence.encryption.EntityEncryptionListener EntityEncryptionListener}.
 * <p>
 * Discovered by Hibernate ORM through {@link java.util.ServiceLoader}, it does nothing unless the persistence unit sets
 * {@value EntityEncryptor#PROPERTY_ENCRYPTION_SERVICE}: to an {@link EntityEncryptionService} instance, passed in the
 * properties of the entity manager factory, or to the name of its class. The service's
 * {@link EntityEncryptionService#getEntityManagerFactory() entity manager factory} is never asked for: the session
 * factory being integrated is the one whose entities are encrypted.
 * <pre>{@code
 * Persistence.createEntityManagerFactory("unit", Map.of(EntityEncryptor.PROPERTY_ENCRYPTION_SERVICE, service));
 * }</pre>
 * The mapping is the same as with the listener, which must not be registered as well. What differs:
 * <ul>
 *   <li>the plaintext of an entity is never nulled: it is nulled only in the state Hibernate ORM writes the row
 *       from;</li>
 *   <li>a read never writes: the snapshot Hibernate ORM checks an entity against holds the decrypted plaintext;</li>
 *   <li>a plaintext is encrypted again only when it changed, so an unrelated change keeps the stored ciphertext.</li>
 * </ul>
 * It replaces Hibernate ORM's default {@code FLUSH_ENTITY} listener, and fails when another one has been registered.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class EntityEncryptionIntegrator implements Integrator {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Creates a new instance.
     *
     * @implSpec A service loaded by {@link java.util.ServiceLoader} has to have a public no-arg constructor.
     */
    public EntityEncryptionIntegrator() {
        super();
    }

    @Override
    public void integrate(final Metadata metadata, final BootstrapContext bootstrapContext,
                          final SessionFactoryImplementor sessionFactory) {
        final var value = sessionFactory.getProperties().get(EntityEncryptor.PROPERTY_ENCRYPTION_SERVICE);
        if (value == null) {
            logger.log(System.Logger.Level.DEBUG, "no {0}; not integrating",
                       EntityEncryptor.PROPERTY_ENCRYPTION_SERVICE);
            return;
        }
        final var encryptor = new EntityEncryptor(sessionFactory, () -> EntityEncryptor.getEncryptionService(value));
        final var engine = new HibernateEntityEncryptor(encryptor);
        final var registry = sessionFactory.getEventListenerRegistry();
        for (final var listener : registry.getEventListenerGroup(EventType.FLUSH_ENTITY).listeners()) {
            if (listener.getClass() != DefaultFlushEntityEventListener.class) {
                throw new IllegalStateException(
                        "a FLUSH_ENTITY listener other than Hibernate ORM's default is registered, which this"
                        + " integration would replace: " + listener.getClass().getName());
            }
        }
        registry.setListeners(EventType.FLUSH_ENTITY, new EncryptingFlushEntityEventListener(engine));
        registry.appendListeners(EventType.PRE_INSERT, engine);
        registry.appendListeners(EventType.POST_UPDATE, engine);
        registry.appendListeners(EventType.POST_LOAD, engine);
    }
}
