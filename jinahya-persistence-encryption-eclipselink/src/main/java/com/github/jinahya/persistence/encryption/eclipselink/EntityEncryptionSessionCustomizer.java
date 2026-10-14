package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.EntityEncryptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;

/**
 * Integrates the encryption of {@link com.github.jinahya.persistence.encryption.EncryptedAttribute encrypted
 * attributes} with EclipseLink, in place of
 * {@link com.github.jinahya.persistence.encryption.EntityEncryptionListener EntityEncryptionListener}.
 * <p>
 * Named by the persistence unit property {@code eclipselink.session.customizer}, it reads the encryption service from
 * {@value EntityEncryptor#PROPERTY_ENCRYPTION_SERVICE}: an {@link EntityEncryptionService} instance, passed in the
 * properties of the entity manager factory, its class, or the name of its class. The service's
 * {@link EntityEncryptionService#getEntityManagerFactory() entity manager factory} is asked for on the first event.
 * <pre>{@code
 * Persistence.createEntityManagerFactory("unit", Map.of(
 *         "eclipselink.session.customizer", EntityEncryptionSessionCustomizer.class.getName(),
 *         EntityEncryptor.PROPERTY_ENCRYPTION_SERVICE, service));
 * }</pre>
 * An application which has a customizer of its own calls this one from it.
 * <p>
 * The mapping is the same as with the listener, which must not be registered as well. What differs:
 * <ul>
 *   <li>the plaintext of an entity is never nulled: it is nulled only in the row EclipseLink writes;</li>
 *   <li>a read never writes: the backup clone EclipseLink compares an entity with holds the decrypted plaintext;</li>
 *   <li>a plaintext is encrypted again only when it changed, so an unrelated change keeps the stored ciphertext.</li>
 * </ul>
 * Attribute change tracking, which weaving may enable, is not supported, and fails on the first event.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class EntityEncryptionSessionCustomizer implements SessionCustomizer {

    /**
     * Creates a new instance.
     *
     * @implSpec EclipseLink instantiates a customizer named by class through its public no-arg constructor.
     */
    public EntityEncryptionSessionCustomizer() {
        super();
    }

    /**
     * Registers the encryption engine on every entity descriptor of the specified session.
     *
     * @param session the session being customized.
     * @throws IllegalArgumentException when {@value EntityEncryptor#PROPERTY_ENCRYPTION_SERVICE} is missing, or
     *                                  stands for no encryption service.
     */
    @Override
    public void customize(final Session session) {
        final var service = EntityEncryptor.getEncryptionService(
                session.getProperty(EntityEncryptor.PROPERTY_ENCRYPTION_SERVICE));
        final var engine = new EclipseLinkEntityEncryptor(new EntityEncryptor(service));
        for (final var descriptor : session.getDescriptors().values()) {
            if (!descriptor.isAggregateDescriptor() && !descriptor.isAggregateCollectionDescriptor()) {
                descriptor.getEventManager().addListener(engine);
            }
        }
    }
}
