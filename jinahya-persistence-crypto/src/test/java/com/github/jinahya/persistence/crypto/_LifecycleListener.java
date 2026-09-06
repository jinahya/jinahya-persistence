package com.github.jinahya.persistence.crypto;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * An entity listener which takes its {@link __EncryptionService} from a static holder rather than from CDI, so that the
 * life cycle can be observed without a container.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class _LifecycleListener extends __EncryptionListener {

    /**
     * The service every instance of this listener delegates to.
     */
    static volatile __EncryptionService SERVICE;

    /**
     * Creates a new instance.
     *
     * @see <a
     *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#entity-listeners">3.6.1.
     *         Entity Listeners</a> (Jakarta Persistence 3.2 Specification Document)
     */
    public _LifecycleListener() {
        super();
    }

    @Override
    protected __EncryptionService getEncryptionService() {
        return SERVICE;
    }

    @PrePersist
    @Override
    protected void onPrePersist(final Object entityInstance) {
        super.onPrePersist(entityInstance);
        encrypt(entityInstance);
    }

    @PreUpdate
    @Override
    protected void onPreUpdate(final Object entityInstance) {
        super.onPreUpdate(entityInstance);
        encrypt(entityInstance);
    }

    @PostLoad
    @Override
    protected void onPostLoad(final Object entityInstance) {
        super.onPostLoad(entityInstance);
        decrypt(entityInstance);
    }
}
