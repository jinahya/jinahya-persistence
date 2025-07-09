package com.github.jinahya.persistence.mapped.tests.spi;

import jakarta.annotation.Nullable;

import java.util.Optional;
import java.util.ServiceLoader;

public interface __DatabaseMetaDataInfo {

    static Optional<__DatabaseMetaDataInfo> load() {
        return ServiceLoader.load(__DatabaseMetaDataInfo.class).findFirst();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the {@code catalog} of the table family.
     *
     * @return the {@code catalog} of the table family.
     */
    @Nullable
    String getCatalog();

    /**
     * Returns the {@code schema} of the table family.
     *
     * @return the {@code schema} of the table family.
     */
    @Nullable
    String getSchema();
}
