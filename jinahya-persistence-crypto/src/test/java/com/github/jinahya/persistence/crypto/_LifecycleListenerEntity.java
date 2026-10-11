package com.github.jinahya.persistence.crypto;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * An entity listener which takes its {@link EntityEncryptionService} from a static holder rather than from a container,
 * so that the life cycle can be observed without one.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class _LifecycleListenerEntity extends EntityEncryptionListener {

    /**
     * The service every instance of this listener works through.
     */
    static volatile EntityEncryptionService SERVICE;

    /**
     * Creates a new instance.
     *
     * @see <a
     *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#entity-listeners">3.6.1.
     *         Entity Listeners</a> (Jakarta Persistence 3.2 Specification Document)
     */
    public _LifecycleListenerEntity() {
        super();
    }

    @Override
    EntityEncryptionService getEncryptionService() {
        return SERVICE;
    }

    // re-declared, calling super: providers ignore a callback inherited from a listener's superclass (#5)
    @PrePersist
    @Override
    void onPrePersist(final Object entityInstance) {
        super.onPrePersist(entityInstance);
    }

    @PreUpdate
    @Override
    void onPreUpdate(final Object entityInstance) {
        super.onPreUpdate(entityInstance);
    }

    @PostLoad
    @Override
    void onPostLoad(final Object entityInstance) {
        super.onPostLoad(entityInstance);
    }
}
