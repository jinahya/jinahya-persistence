package com.github.jinahya.persistence.mapped.tests.spi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class __DatabaseMetaDataInfoTest {

    @Test
    void __() {
        final var loaded = __DatabaseMetaDataInfo.load();
        assertThat(loaded).hasValueSatisfying(v -> {
            assertThat(v.getCatalog()).isEqualTo(__DatabaseMetaDataInfoImpl.CATALOG);
            assertThat(v.getCatalog()).isEqualTo(__DatabaseMetaDataInfoImpl.CATALOG);
        });
    }
}
