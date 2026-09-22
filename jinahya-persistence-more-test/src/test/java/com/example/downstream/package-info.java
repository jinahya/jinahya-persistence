/**
 * A downstream, simulated.
 * <p>
 * Every other test in this module extends a base from that base's own package, which is the one arrangement a real
 * consumer never has: a downstream's entity and its test live in the downstream's package, and reach the published
 * bases across a package boundary. That boundary is not cosmetic. The assertions in those bases are
 * {@code protected @Test} methods, and whether JUnit discovers an inherited {@code protected} method declared in
 * another package is a property of JUnit, not something this module can assume — get it wrong and a consumer extends a
 * base, sees a green build, and has run nothing.
 * <p>
 * So these classes claim no privileges. They are in a package of their own, they extend the bases exactly as a
 * consumer would, and each asserts the count of inherited tests it expects to see run.
 */
package com.example.downstream;
