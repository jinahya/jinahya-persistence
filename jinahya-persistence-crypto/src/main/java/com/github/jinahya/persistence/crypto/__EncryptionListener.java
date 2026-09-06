package com.github.jinahya.persistence.crypto;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.lang.invoke.MethodHandles;

/**
 * An abstract entity listener which encrypts an entity instance before it is written, and decrypts it after it is
 * read.
 * <p>
 * A subclass supplies an {@link __EncryptionService}, and is registered with
 * {@link jakarta.persistence.EntityListeners @EntityListeners} on an {@link __EncryptedEntity @__EncryptedEntity}
 * class. The life cycle callbacks here only log; override the ones an entity needs, and call {@link #encrypt(Object)}
 * or {@link #decrypt(Object)} from them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __EncryptionService
 * @see __EncryptedEntity
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __EncryptionListener {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected __EncryptionListener() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Called after this listener has been constructed and its injection points have been satisfied.
     *
     * @implSpec The implementation of this class only logs.
     */
    @PostConstruct
    protected void onPostConstruct() {
        logger.log(System.Logger.Level.TRACE, "onPostConstruct()");
    }

    // https://stackoverflow.com/a/72628439/330457

    /**
     * Observes the CDI container {@link Startup} event.
     *
     * @param startup the observed event.
     * @implSpec The implementation of this class only logs.
     */
    protected void onStartup(@Observes final Startup startup) {
        logger.log(System.Logger.Level.TRACE, "onStartup({0})", startup);
    }

    /**
     * Called before this listener is destroyed.
     *
     * @implSpec The implementation of this class only logs.
     */
    @PreDestroy
    protected void onPreDestroy() {
        logger.log(System.Logger.Level.TRACE, "onPreDestroy()");
    }

    // https://stackoverflow.com/a/72628439/330457

    /**
     * Observes the CDI container {@link Shutdown} event.
     *
     * @param shutdown the observed event.
     * @implSpec The implementation of this class only logs.
     */
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(System.Logger.Level.TRACE, "onShutdown({0})", shutdown);
    }

    // --------------------------------------------------------------------------------------------------------- PERSIST

    /**
     * Called before the entity instance is persisted.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs; override it, and call {@link #encrypt(Object)} or
     *         {@link #decrypt(Object)}, as the entity requires.
     */
    @PrePersist
    protected void onPrePersist(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPrePersist({0})", describe(entityInstance));
    }

    /**
     * Called after the entity instance has been persisted.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs; override it, and call {@link #encrypt(Object)} or
     *         {@link #decrypt(Object)}, as the entity requires.
     */
    @PostPersist
    protected void onPostPersist(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostPersist({0})", describe(entityInstance));
    }

    // ---------------------------------------------------------------------------------------------------------- REMOVE

    /**
     * Called before the entity instance is removed.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs; override it, and call {@link #encrypt(Object)} or
     *         {@link #decrypt(Object)}, as the entity requires.
     */
    @PreRemove
    protected void onPreRemove(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPreRemove({0})", describe(entityInstance));
    }

    /**
     * Called after the entity instance has been removed.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs; override it, and call {@link #encrypt(Object)} or
     *         {@link #decrypt(Object)}, as the entity requires.
     */
    @PostRemove
    protected void onPostRemove(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostRemove({0})", describe(entityInstance));
    }

    // ---------------------------------------------------------------------------------------------------------- UPDATE

    /**
     * Called before the entity instance is updated.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs; override it, and call {@link #encrypt(Object)} or
     *         {@link #decrypt(Object)}, as the entity requires.
     */
    @PreUpdate
    protected void onPreUpdate(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPreUpdate({0})", describe(entityInstance));
    }

    /**
     * Called after the entity instance has been updated.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs; override it, and call {@link #encrypt(Object)} or
     *         {@link #decrypt(Object)}, as the entity requires.
     */
    @PostUpdate
    protected void onPostUpdate(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostUpdate({0})", describe(entityInstance));
    }

    // ------------------------------------------------------------------------------------------------------------ LOAD

    /**
     * Called after the entity instance has been loaded.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs; override it, and call {@link #encrypt(Object)} or
     *         {@link #decrypt(Object)}, as the entity requires.
     */
    @PostLoad
    protected void onPostLoad(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostLoad({0})", describe(entityInstance));
    }

    // ----------------------------------------------------------------------------------------------- encryptionService

    /**
     * Returns the encryption service which this listener delegates to.
     *
     * @return the encryption service; never {@code null}.
     */
    protected abstract __EncryptionService getEncryptionService();

    /**
     * Returns a description of the specified entity instance which cannot carry an attribute value.
     *
     * @param entityInstance the entity instance to describe.
     * @return the instance's class name and identity hash.
     * @implNote An entity's {@code toString()} routinely prints its attributes, so an instance must never be
     *         handed to the logger: a decrypted instance would put the plaintext straight into the log.
     */
    private static String describe(final Object entityInstance) {
        return entityInstance.getClass().getName() + '@'
               + Integer.toHexString(System.identityHashCode(entityInstance));
    }

    /**
     * Encrypts the specified entity instance, through {@link #getEncryptionService() the encryption service}.
     *
     * @param entityInstance the entity instance to encrypt; instances of a class which is not annotated with
     *                       {@link __EncryptedEntity @__EncryptedEntity} are silently skipped.
     * @see __EncryptionService#encrypt(Object)
     */
    protected void encrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "encrypt({0})", describe(entityInstance));
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            logger.log(System.Logger.Level.TRACE, "skipping encrypting {0}, not annotated with {1}",
                       describe(entityInstance), __EncryptedEntity.class);
            return;
        }
        final var encryptionService = getEncryptionService();
        assert encryptionService != null;
        encryptionService.encrypt(entityInstance);
        logger.log(System.Logger.Level.TRACE, "encrypted: {0}", describe(entityInstance));
    }

    /**
     * Decrypts the specified entity instance, through {@link #getEncryptionService() the encryption service}.
     *
     * @param entityInstance the entity instance to decrypt; instances of a class which is not annotated with
     *                       {@link __EncryptedEntity @__EncryptedEntity} are silently skipped.
     * @see __EncryptionService#decrypt(Object)
     */
    protected void decrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "decrypt({0})", describe(entityInstance));
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            logger.log(System.Logger.Level.TRACE, "skipping decrypting {0}, not annotated with {1}",
                       describe(entityInstance), __EncryptedEntity.class);
            return;
        }
        final var encryptionService = getEncryptionService();
        assert encryptionService != null;
        encryptionService.decrypt(entityInstance);
        logger.log(System.Logger.Level.TRACE, "decrypted: {0}", describe(entityInstance));
    }
}
