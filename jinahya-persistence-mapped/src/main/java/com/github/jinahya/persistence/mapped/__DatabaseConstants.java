package com.github.jinahya.persistence.mapped;

public abstract class __DatabaseConstants {

    // ----------------------------------------------------------------------------------------------------------- MySQL
    public abstract class __MySQL {

        public static final long MIN_INT = Integer.MIN_VALUE;

        public static final long MAX_INT = Integer.MAX_VALUE;

        public static final long MIN_INT_UNSIGNED = 0L;

        public static final long MAX_INT_UNSIGNED = 4_294_967_295L;

        // ------------------------------------------------------------------------------------------------ CONSTRUCTORS
        protected __MySQL() {
            super();
        }
    }

    // ------------------------------------------------------------------------------------------------------ PostgreSQL

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __DatabaseConstants() {
        super();
    }
}
