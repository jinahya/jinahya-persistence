package com.github.jinahya.persistence.mapped;

public abstract class __DatabaseConstants {

    // ----------------------------------------------------------------------------------------------------------- MySQL

    /**
     * Constants for MySQL.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/data-types.html">Data Types</a> (MySQL Reference Manual)
     */
    public abstract class __MySQLConstants {

        // --------------------------------------------------------------------------------------------------------- INT

        /**
         * The maximum value for {@code INT} columns, which is {@value}.
         */
        public static final long MIN_INT = Integer.MIN_VALUE;

        /**
         * The maximum value for {@code INT} columns,which is {@value}.
         */
        public static final long MAX_INT = Integer.MAX_VALUE;

        // ------------------------------------------------------------------------------------------------ INT_UNSIGNED

        /**
         * The minimum value for {@code INT UNSIGNED} columns,which is {@value}.
         */
        public static final long MIN_INT_UNSIGNED = 0L;

        /**
         * The maximum value for {@code INT UNSIGNED} columns, which is {@value}.
         */
        public static final long MAX_INT_UNSIGNED = 4_294_967_295L;

        // ------------------------------------------------------------------------------------------------ CONSTRUCTORS
        protected __MySQLConstants() {
            super();
        }
    }

    // ------------------------------------------------------------------------------------------------------ PostgreSQL

    /**
     * .
     *
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a> (PostgreSQL Reference Manual)
     */
    public abstract static class PostgreSQLConstants {

        public static final long MIN_SMALLINT = Short.MIN_VALUE;

        public static final long MAX_SMALLINT = Short.MAX_VALUE;

        // -------------------------------------------------------------------------------------------------------------
        public static final long MIN_INT = Integer.MIN_VALUE;

        public static final long MAX_INT = Integer.MAX_VALUE;

        // -------------------------------------------------------------------------------------------------------------
        public static final long MIN_BIGINT = Long.MIN_VALUE;

        public static final long MAX_BIGINT = Long.MAX_VALUE;

        // ------------------------------------------------------------------------------------------------- smallserial
        public static final long MIN_SMALLSERIAL = 1L;

        public static final long MAX_SMALLSERIAL = Short.MAX_VALUE;

        // ------------------------------------------------------------------------------------------------------ serial
        public static final long MIN_SERIAL = 1L;

        public static final long MAX_SERIAL = Integer.MAX_VALUE;

        // --------------------------------------------------------------------------------------------------- bigserial

        /**
         * The minimum value for {@code bigserial} columns, which is {@value}.
         */
        public static final long MIN_BIGSERIAL = 1L;

        /**
         * The maximum value for {@code bigserial} columns, which is {@value}.
         */
        public static final long MAX_BIGSERIAL = Long.MAX_VALUE;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __DatabaseConstants() {
        super();
    }
}
