package com.github.jinahya.persistence.mapped.tests.spi;

import jakarta.annotation.Nullable;

public class __DatabaseMetaDataInfoImpl implements __DatabaseMetaDataInfo {

    static final String CATALOG = "catalog";

    static final String SCHEMA = "schema";

    @Nullable
    @Override
    public String getCatalog() {
        return CATALOG;
    }

    @Nullable
    @Override
    public String getSchema() {
        return SCHEMA;
    }
}
