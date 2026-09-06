package com.github.jinahya.persistence.cryptoext;

import com.github.jinahya.persistence.crypto.__EncryptionManager;
import com.github.jinahya.persistence.crypto.__EncryptionService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;

import java.util.List;
import java.util.Set;

/**
 * A service which supplies the facts an {@code orm.xml} mapping would establish, from <em>outside</em> the
 * implementation package.
 * <p>
 * This class exists to prove that the extension point is usable by an ordinary application. An earlier revision
 * compiled only because every test lived in the implementation package: {@code ColumnRules} had an implicit
 * {@code protected} canonical constructor, which a subclass in another package inherits visibility of but cannot
 * invoke.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class _ExternalResolverService extends __EncryptionService {

    public _ExternalResolverService(final EntityManagerFactory entityManagerFactory,
                                    final __EncryptionManager encryptionManager,
                                    final Set<String> nonInsertableAttributeNames) {
        super(entityManagerFactory, encryptionManager);
        this.nonInsertableAttributeNames = Set.copyOf(nonInsertableAttributeNames);
    }

    @Override
    protected ColumnRules resolveColumnRules(final ManagedType<?> rootType,
                                             final List<Attribute<?, ?>> embeddingPath,
                                             final Attribute<?, ?> attribute) {
        if (nonInsertableAttributeNames.contains(attribute.getName())) {
            return new ColumnRules(MappingFlag.NO, MappingFlag.YES, MappingFlag.YES, "orm.xml (external)");
        }
        return super.resolveColumnRules(rootType, embeddingPath, attribute);
    }

    private final Set<String> nonInsertableAttributeNames;
}
