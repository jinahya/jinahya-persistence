package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.util.Objects;
import java.util.function.Function;

public abstract class __PersistenceCryptoListener {

    // -----------------------------------------------------------------------------------------------------------------
    @PrePersist
    protected void onPrePersist(final @Nonnull Object entityInstance) {
        // encrypt
    }

    @PostPersist
    protected void onPostPersist(final @Nonnull Object entityInstance) {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PreRemove
    protected void onPreRemove(final @Nonnull Object entityInstance) {
        // empty
    }

    @PostRemove
    protected void onPostRemove(final @Nonnull Object entityInstance) {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PreUpdate
    protected void onPreUpdate(final @Nonnull Object entityInstance) {
        // encrypt
    }

    @PostUpdate
    protected void onPostUpdate(final @Nonnull Object entityInstance) {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostLoad
    protected void onPostLoad(final @Nonnull Object entityInstance) {
        // decrypt
    }

    // --------------------------------------------------------------------------------------------------- entityManager
    protected <R> R applyEntityManager(@Nonnull final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply(entityManager);
    }

    protected <R> R applyEntityManagerFactory(
            @Nonnull final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManager(em -> function.apply(em.getEntityManagerFactory()));
    }

    // -----------------------------------------------------------------------------------------------------------------
//    @jakarta.inject.Inject
    @PersistenceContext
    private EntityManager entityManager;
}
