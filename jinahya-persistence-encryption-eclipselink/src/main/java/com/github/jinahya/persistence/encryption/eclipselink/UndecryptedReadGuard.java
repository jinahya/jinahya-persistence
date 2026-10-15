package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.EntityEncryptionEngine;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Refuses, in a persistence unit which has an encrypted entity, every query which would hand the application objects
 * that are never decrypted.
 * <p>
 * The engine decrypts working clones only, in a unit of work, and leaves every object outside one — the shared cache's
 * — as the row holds it. A query which is read-only, which doesn't maintain the cache, or which doesn't register its
 * results in the unit of work returns such objects, as does any query of a class read-only in the unit of work or in
 * its descriptor; their plaintexts would be {@code null}. The rule is unit-wide, not per entity: an unencrypted entity
 * so read may reach an encrypted one through a relationship, and the result of a report query can't be told safe
 * before it is prepared. So such a query is refused whatever it reads.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://github.com/jinahya/jinahya-persistence/issues/103">#103</a>
 */
class UndecryptedReadGuard extends SessionEventAdapter {

    /**
     * Creates a new instance.
     *
     * @param engine  the engine which tells the encrypted entities.
     * @param session the session being customized, whose descriptors are classified.
     */
    UndecryptedReadGuard(final EntityEncryptionEngine engine, final Session session) {
        super();
        this.engine = Objects.requireNonNull(engine, "engine is null");
        this.session = Objects.requireNonNull(session, "session is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void preExecuteQuery(final SessionEvent event) {
        if (!(event.getQuery() instanceof ObjectBuildingQuery query)) {
            return;
        }
        final var settings = new ArrayList<String>();
        if (query instanceof ObjectLevelReadQuery read && read.isReadOnly()) {
            settings.add("read-only");
        }
        if (!query.shouldMaintainCache()) {
            settings.add("not maintaining the cache");
        }
        if (!query.shouldRegisterResultsInUnitOfWork()) {
            settings.add("not registering its results in the unit of work");
        }
        if (event.getSession() instanceof UnitOfWorkImpl unitOfWork && !unitOfWork.getReadOnlyClasses().isEmpty()) {
            settings.add("run in a unit of work with read-only classes " + unitOfWork.getReadOnlyClasses());
        }
        // classified on the first object query; never while the session is being customized, when the engine's
        // entity manager factory is not there yet
        final var classification = classification();
        if (!classification.encrypted()) {
            return;
        }
        settings.addAll(classification.unsafeDescriptors());
        if (!settings.isEmpty()) {
            throw new IllegalStateException(
                    "a query " + describe(query) + " is refused: " + String.join(", ", settings)
                    + "; this persistence unit has encrypted entities, and such a query returns objects which are"
                    + " never decrypted. Read them through an ordinary query, in a unit of work");
        }
    }

    private static String describe(final DatabaseQuery query) {
        final var name = query.getName();
        return (name == null || name.isEmpty() ? "" : "'" + name + "' ")
               + "(" + query.getClass().getSimpleName()
               + (query.getReferenceClass() == null ? "" : " of " + query.getReferenceClass().getName()) + ")";
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Whether the persistence unit has an encrypted entity, and which of its descriptors would return undecrypted
     * objects whatever the query.
     *
     * @param encrypted         whether any entity has encrypted attributes.
     * @param unsafeDescriptors descriptions of descriptors which are read-only, or which don't register results.
     */
    private record Classification(boolean encrypted, List<String> unsafeDescriptors) {

    }

    private Classification classification() {
        var result = classification;
        if (result == null) {
            synchronized (this) {
                result = classification;
                if (result == null) {
                    result = classification = classify();
                }
            }
        }
        return result;
    }

    private Classification classify() {
        var encrypted = false;
        final var unsafe = new ArrayList<String>();
        for (final ClassDescriptor descriptor : session.getDescriptors().values()) {
            if (descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()
                || descriptor.isDescriptorForInterface()) {
                continue;
            }
            encrypted |= engine.hasEncryptedAttributes(descriptor.getJavaClass());
            if (descriptor.shouldBeReadOnly()) {
                unsafe.add(descriptor.getJavaClassName() + " read-only");
            }
            if (!descriptor.shouldRegisterResultsInUnitOfWork()) {
                unsafe.add(descriptor.getJavaClassName() + " not registering its results in the unit of work");
            }
        }
        return new Classification(encrypted, List.copyOf(unsafe));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final EntityEncryptionEngine engine;

    private final Session session;

    private volatile @org.jspecify.annotations.Nullable Classification classification;
}
