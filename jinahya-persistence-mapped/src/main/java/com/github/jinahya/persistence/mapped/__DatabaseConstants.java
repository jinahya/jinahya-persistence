package com.github.jinahya.persistence.mapped;

import java.math.BigInteger;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __DatabaseConstants {

    // ----------------------------------------------------------------------------------------------------------- MySQL

    /**
     * Constants for MySQL.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/data-types.html">Data Types</a> (MySQL Reference
     *         Manual)
     * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/storage-requirements.html">Data Type Storage
     *         Requirements</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/integer-types.html">Integer Types (Exact Value) -
     *         INTEGER, INT, SMALLINT, TINYINT, MEDIUMINT, BIGINT</a>
     */
    public static final class __MySQLConstants {

        // ----------------------------------------------------------------------------------------------------- TINYINT

        /**
         * The maximum value for {@code TINYINT} columns, which is {@value}.
         */
        public static final int MIN_TINYINT = Byte.MIN_VALUE;

        /**
         * The maximum value for {@code TINYINT} columns,which is {@value}.
         */
        public static final int MAX_TINYINT = Byte.MAX_VALUE;

        /**
         * The minimum value for {@code TINYINT UNSIGNED} columns,which is {@value}.
         */
        public static final int MIN_TINYINT_UNSIGNED = 0x00;

        /**
         * The maximum value for {@code TINYINT UNSIGNED} columns, which is {@value}.
         */
        public static final int MAX_TINYINT_UNSIGNED = 0xFF;

        // ---------------------------------------------------------------------------------------------------- SMALLINT

        /**
         * The maximum value for {@code SMALLINT} columns, which is {@value}.
         */
        public static final int MIN_SMALLINT = Short.MIN_VALUE;

        /**
         * The maximum value for {@code SMALLINT} columns,which is {@value}.
         */
        public static final int MAX_SMALLINT = Short.MAX_VALUE;

        /**
         * The minimum value for {@code SMALLINT UNSIGNED} columns,which is {@value}.
         */
        public static final int MIN_SMALLINT_UNSIGNED = 0x0000;

        /**
         * The maximum value for {@code SMALLINT UNSIGNED} columns, which is {@value}.
         */
        public static final int MAX_SMALLINT_UNSIGNED = 0xFFFF;

        // --------------------------------------------------------------------------------------------------- MEDIUMINT

        /**
         * The maximum value for {@code MEDIUMINT} columns, which is {@value}.
         */
        public static final int MIN_MEDIUMINT = -8388608;

        /**
         * The maximum value for {@code MEDIUMINT} columns,which is {@value}.
         */
        public static final int MAX_MEDIUMINT = +8388607;

        /**
         * The minimum value for {@code MEDIUMINT UNSIGNED} columns,which is {@value}.
         */
        public static final int MIN_MEDIUMINT_UNSIGNED = 0;

        /**
         * The maximum value for {@code MEDIUMINT UNSIGNED} columns, which is {@value}.
         */
        public static final int MAX_MEDIUMINT_UNSIGNED = 0xFFFFFF;

        // --------------------------------------------------------------------------------------------------------- INT

        /**
         * The maximum value for {@code INT} columns, which is {@value}.
         */
        public static final int MIN_INT = Integer.MIN_VALUE;

        /**
         * The maximum value for {@code INT} columns,which is {@value}.
         */
        public static final int MAX_INT = Integer.MAX_VALUE;

        /**
         * The minimum value for {@code INT UNSIGNED} columns,which is {@value}.
         */
        public static final int MIN_INT_UNSIGNED = 0x00000000;

        /**
         * The maximum value for {@code INT UNSIGNED} columns, which is {@value}.
         */
        public static final long MAX_INT_UNSIGNED = 0xFFFFFFFFL;

        // ------------------------------------------------------------------------------------------------------ BIGINT

        /**
         * The maximum value for {@code BIGINT} columns, which is {@value}.
         */
        public static final long MIN_BIGINT = Long.MIN_VALUE;

        /**
         * The maximum value for {@code BIGINT} columns,which is {@value}.
         */
        public static final long MAX_BIGINT = Long.MAX_VALUE;

        /**
         * The minimum value for {@code BIGINT UNSIGNED} columns,which is {@value}.
         *
         * @see #MAX_BIGINT_UNSIGNED()
         */
        public static final int MIN_BIGINT_UNSIGNED = 0;

        /**
         * The maximum value for {@code BIGINT UNSIGNED} columns, which is {@code 0xFFFFFFFFFFFF}.
         *
         * @see #MIN_BIGINT_UNSIGNED
         */
        @SuppressWarnings({
                "java:S100" // Method names should comply with a naming convention
        })
        public static BigInteger MAX_BIGINT_UNSIGNED() {
            return new BigInteger("FFFFFFFFFFFFFFFF", 16);
        }

        // ------------------------------------------------------------------------------------------------ CONSTRUCTORS

        /**
         * Creates a new instance.
         */
        private __MySQLConstants() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // ------------------------------------------------------------------------------------------------------ PostgreSQL

    /**
     * Constants for PostgreSQL.
     *
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a> (PostgreSQL Reference
     *         Manual)
     * @see <a href="https://www.postgresql.org/docs/current/datatype-numeric.html">Numberic Types</a>
     */
    public static final class __PostgreSQLConstants {

        // ---------------------------------------------------------------------------------------------------- smallint
        public static final int MIN_SMALLINT = Short.MIN_VALUE;

        public static final int MAX_SMALLINT = Short.MAX_VALUE;

        // --------------------------------------------------------------------------------------------------------- int
        public static final int MIN_INT = Integer.MIN_VALUE;

        public static final int MAX_INT = Integer.MAX_VALUE;

        // ------------------------------------------------------------------------------------------------------ bigint
        public static final long MIN_BIGINT = Long.MIN_VALUE;

        public static final long MAX_BIGINT = Long.MAX_VALUE;

        // ----------------------------------------------------------------------------------------------------- decimal
        public static final int MAX_SCALE_DECIMAL = 16383;

        public static final int MAX_PRECISION_DECIMAL = MAX_SCALE_DECIMAL + 131072;

        // ----------------------------------------------------------------------------------------------------- numeric
        public static final int MAX_SCALE_NUMERIC = MAX_SCALE_DECIMAL;

        public static final int MAX_PRECISION_NUMERIC = MAX_PRECISION_DECIMAL;

        // -------------------------------------------------------------------------------------------------------- real
        public static final int BYTES_REAL = Float.BYTES;

        // -------------------------------------------------------------------------------------------- double precision
        public static final int BYTES_DOUBLE_PRECISION = Double.BYTES;

        // ------------------------------------------------------------------------------------------------- smallserial
        public static final int BYTES_SMALLSERIAL = Short.BYTES;

        public static final int MIN_SMALLSERIAL = 1;

        public static final int MAX_SMALLSERIAL = Short.MAX_VALUE;

        // ------------------------------------------------------------------------------------------------------ serial
        public static final int BYTES_SERIAL = Integer.BYTES;

        public static final int MIN_SERIAL = 1;

        public static final int MAX_SERIAL = Integer.MAX_VALUE;

        // --------------------------------------------------------------------------------------------------- bigserial
        public static final int BYTES_BIGSERIAL = Long.BYTES;

        /**
         * The minimum value for {@code bigserial} columns, which is {@value}.
         */
        public static final int MIN_BIGSERIAL = 1;

        /**
         * The maximum value for {@code bigserial} columns, which is {@value}.
         */
        public static final long MAX_BIGSERIAL = Long.MAX_VALUE;

        // -------------------------------------------------------------------------------------------------------------

        /**
         * Creates a new instance.
         */
        private __PostgreSQLConstants() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    private __DatabaseConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
