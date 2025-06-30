package com.github.jinahya.persistence.mapped;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Slf4j
class __DatabaseConstantsTest {

    /**
     * A class for testing {@link __DatabaseConstants.__MySQLConstants}.
     */
    @Nested
    class __MySQLTest {

        @Test
        void __BIGINT() {
            log.debug("MAX_BIGINT: {}", __DatabaseConstants.__MySQLConstants.MAX_BIGINT);
        }

        @Test
        void __BIGINT_UNSIGNED() {
            log.debug("MAX_BIGINT_UNSIGNED: {}", __DatabaseConstants.__MySQLConstants.MAX_BIGINT_UNSIGNED);
        }
    }

    /**
     * A class for testing {@link __DatabaseConstants.__PostgreSQLConstants}.
     */
    @Nested
    class __PostgresTest {

    }
}