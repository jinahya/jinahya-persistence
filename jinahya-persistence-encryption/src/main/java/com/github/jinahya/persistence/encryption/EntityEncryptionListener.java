package com.github.jinahya.persistence.encryption;

import jakarta.inject.Inject;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.lang.invoke.MethodHandles;

/**
 * An entity listener which encrypts an entity instance before it is written, and decrypts it after it is read.
 * <p>
 * Register this class itself on an entity class which has {@link EncryptedAttribute encrypted attributes}, and provide
 * an {@link EntityEncryptionService} as a bean; nothing else is required.
 * <pre>{@code
 * @EntityListeners(EntityEncryptionListener.class)
 * @Entity
 * class User { ... }
 * }</pre>
 * It encrypts on {@link PrePersist @PrePersist} and {@link PreUpdate @PreUpdate}, before the statement is built, and
 * decrypts on {@link PostLoad @PostLoad}, and again on {@link PostUpdate @PostUpdate}, restoring the plaintext which
 * an {@code UPDATE} had to go out without. An instance of a class with no encrypted attribute passes through untouched.
 * <p>
 * The {@link EntityEncryptionService encryption service} is injected, which happens when the persistence provider
 * creates its listeners through a bean container: a Jakarta EE container does; in Java SE, pass the CDI
 * {@code BeanManager} as the {@code jakarta.persistence.bean.manager} property of the {@code EntityManagerFactory};
 * Spring Boot does with Hibernate ORM. Which bean is injected follows the container's ordinary rules. A listener
 * created without one fails on its first callback.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionService
 * @see EncryptedAttribute
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
     * Encrypts the specified entity instance before it is inserted, keeping its plaintext: the {@code INSERT} never
     * carries the plaintext column, which is non-insertable.
     *
     * @param entityInstance the entity instance.
     */
    @PrePersist
    void onPrePersist(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPrePersist({0})", describe(entityInstance));
        getEncryptor().encrypt(entityInstance, false);
    }

    /**
     * Encrypts the specified entity instance before it is updated, setting each plaintext whose column is mapped
     * nullable to {@code null}: the {@code UPDATE} carries the plaintext column, which is updatable. A primitive
     * plaintext, or one whose column is mapped non-nullable, is left as it is.
     *
     * @param entityInstance the entity instance.
     */
    @PreUpdate
    void onPreUpdate(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPreUpdate({0})", describe(entityInstance));
        getEncryptor().encrypt(entityInstance, true);
    }

    /**
     * Restores the plaintext of the specified entity instance after it has been updated, from the ciphertext just
     * written, so that the application never sees one nulled.
     *
     * @param entityInstance the entity instance.
     */
    @PostUpdate
    void onPostUpdate(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostUpdate({0})", describe(entityInstance));
        getEncryptor().decrypt(entityInstance);
    }

    /**
     * Decrypts the specified entity instance after it has been loaded.
     *
     * @param entityInstance the entity instance.
     */
    @PostLoad
    void onPostLoad(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostLoad({0})", describe(entityInstance));
        getEncryptor().decrypt(entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the encryption service this listener was injected with.
     *
     * @return the encryption service.
     * @throws IllegalStateException when no service has been injected.
     */
    EntityEncryptionService getEncryptionService() {
        final var result = encryptionService;
        if (result == null) {
            throw new IllegalStateException(
                    "no EntityEncryptionService injected into " + getClass().getName()
                    + "; the persistence provider injects listeners only when it creates them through a bean container"
                    + " — in Java SE, pass the CDI BeanManager as the jakarta.persistence.bean.manager property");
        }
        return result;
    }

    /**
     * Returns the encryptor working through the {@link #getEncryptionService() encryption service}, creating it on the
     * first call.
     *
     * @return the encryptor.
     * @implNote Created on the first callback, never at construction: the persistence provider may create this
     *         listener while the entity manager factory the encryptor reads is still being built.
     */
    private EntityEncryptor getEncryptor() {
        var result = encryptor;
        if (result == null) {
            synchronized (this) {
                result = encryptor;
                if (result == null) {
                    result = encryptor = new EntityEncryptor(getEncryptionService());
                }
            }
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

    // -----------------------------------------------------------------------------------------------------------------
    // injected when the persistence provider creates listeners through a bean container; never assigned here
    @Inject
    private @org.jspecify.annotations.Nullable EntityEncryptionService encryptionService;

    // created from the encryption service on the first callback
    private volatile @org.jspecify.annotations.Nullable EntityEncryptor encryptor;
}
