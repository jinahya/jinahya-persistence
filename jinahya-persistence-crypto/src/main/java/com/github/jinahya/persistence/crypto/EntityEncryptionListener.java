package com.github.jinahya.persistence.crypto;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;

/**
 * An entity listener which encrypts an entity instance before it is written, and decrypts it after it is read.
 * <p>
 * Register this class itself on an {@link EncryptedEntity @EncryptedEntity} class; nothing else is required.
 * <pre>{@code
 * @EncryptedEntity
 * @EntityListeners(EntityEncryptionListener.class)
 * @Entity
 * class User { ... }
 * }</pre>
 * It encrypts on {@link PrePersist @PrePersist} and {@link PreUpdate @PreUpdate}, before the statement is built, and
 * decrypts on {@link PostLoad @PostLoad}; the other callbacks only log, and a post-callback never encrypts, by which
 * time the row is already written. The {@link AbstractEntityEncryptionService encryption service} comes from CDI:
 * injected when the persistence provider creates listeners through a {@code BeanManager}, looked up from
 * {@link CDI#current() the current container} otherwise.
 * <p>
 * <strong>A subclass has to re-declare every callback it wants.</strong> Neither Hibernate ORM nor EclipseLink invokes a
 * callback annotation inherited from a listener's superclass, so a subclass which only overrides
 * {@link #getEncryptionService()} — to supply the service without CDI, say — encrypts nothing unless it also
 * re-declares {@code @PrePersist}, {@code @PreUpdate} and {@code @PostLoad} methods which call {@code super}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AbstractEntityEncryptionService
 * @see EncryptedEntity
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class EntityEncryptionListener {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     *
     * @implSpec An entity listener class has to have a public no-arg constructor.
     */
    public EntityEncryptionListener() {
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

    /**
     * Called before this listener is destroyed.
     *
     * @implSpec The implementation of this class only logs.
     */
    @PreDestroy
    protected void onPreDestroy() {
        logger.log(System.Logger.Level.TRACE, "onPreDestroy()");
    }

    // --------------------------------------------------------------------------------------------------------- PERSIST

    /**
     * Called before the entity instance is persisted.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class {@link #encrypt(Object) encrypts} the {@code entityInstance}, before
     *         the {@code INSERT} is built. A subclass which overrides this method, and does not call {@code super}, opts
     *         out of that.
     */
    @PrePersist
    protected void onPrePersist(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPrePersist({0})", describe(entityInstance));
        encrypt(entityInstance);
    }

    /**
     * Called after the entity instance has been persisted.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs.
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
     * @implSpec The implementation of this class only logs.
     */
    @PreRemove
    protected void onPreRemove(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPreRemove({0})", describe(entityInstance));
    }

    /**
     * Called after the entity instance has been removed.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs.
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
     * @implSpec The implementation of this class {@link #encrypt(Object) encrypts} the {@code entityInstance}, before
     *         the {@code UPDATE} is built. A subclass which overrides this method, and does not call {@code super}, opts
     *         out of that.
     */
    @PreUpdate
    protected void onPreUpdate(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPreUpdate({0})", describe(entityInstance));
        encrypt(entityInstance);
    }

    /**
     * Called after the entity instance has been updated.
     *
     * @param entityInstance the entity instance.
     * @implSpec The implementation of this class only logs.
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
     * @implSpec The implementation of this class {@link #decrypt(Object) decrypts} the {@code entityInstance}. A
     *         subclass which overrides this method, and does not call {@code super}, opts out of that.
     */
    @PostLoad
    protected void onPostLoad(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostLoad({0})", describe(entityInstance));
        decrypt(entityInstance);
    }

    // ----------------------------------------------------------------------------------------------- encryptionService

    /**
     * Returns the encryption service which this listener delegates to.
     *
     * @return the encryption service; never {@code null}.
     * @implSpec The implementation of this class returns the injected service, and otherwise looks one up from
     *         {@link CDI#current() the current container}, once.
     */
    protected AbstractEntityEncryptionService getEncryptionService() {
        if (encryptionService != null) {
            return encryptionService;
        }
        var result = lookedUpEncryptionService;
        if (result == null) {
            result = lookedUpEncryptionService = CDI.current().select(AbstractEntityEncryptionService.class).get();
        }
        return result;
    }

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
     * @param entityInstance the entity instance to encrypt; an instance of a class which is not annotated with
     *                       {@link EncryptedEntity @EncryptedEntity}, and has no encrypted attribute, passes through
     *                       untouched.
     * @throws RuntimeException when the {@code entityInstance}'s class has encrypted attributes but is not annotated
     *                          with {@link EncryptedEntity @EncryptedEntity}.
     * @see AbstractEntityEncryptionService#encrypt(Object)
     */
    protected void encrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "encrypt({0})", describe(entityInstance));
        // the service decides whether the instance is to be transformed, and rejects a forgotten @EncryptedEntity
        final var encryptionService = getEncryptionService();
        assert encryptionService != null;
        encryptionService.encrypt(entityInstance);
        logger.log(System.Logger.Level.TRACE, "encrypted: {0}", describe(entityInstance));
    }

    /**
     * Decrypts the specified entity instance, through {@link #getEncryptionService() the encryption service}.
     *
     * @param entityInstance the entity instance to decrypt; an instance of a class which is not annotated with
     *                       {@link EncryptedEntity @EncryptedEntity}, and has no encrypted attribute, passes through
     *                       untouched.
     * @throws RuntimeException when the {@code entityInstance}'s class has encrypted attributes but is not annotated
     *                          with {@link EncryptedEntity @EncryptedEntity}.
     * @see AbstractEntityEncryptionService#decrypt(Object)
     */
    protected void decrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "decrypt({0})", describe(entityInstance));
        // the service decides whether the instance is to be transformed, and rejects a forgotten @EncryptedEntity
        final var encryptionService = getEncryptionService();
        assert encryptionService != null;
        encryptionService.decrypt(entityInstance);
        logger.log(System.Logger.Level.TRACE, "decrypted: {0}", describe(entityInstance));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // injected when the provider creates listeners through a BeanManager; never assigned here
    @Inject
    private @Nullable AbstractEntityEncryptionService encryptionService;

    // looked up from the current container otherwise, once
    private volatile @Nullable AbstractEntityEncryptionService lookedUpEncryptionService;
}
