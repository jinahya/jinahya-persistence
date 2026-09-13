/**
 * The persistence specification's own example entity model, wired to the three roles of
 * {@link com.github.jinahya.persistence.test.util}.
 * <p>
 * The classes here are the ones the Jakarta Persistence specification uses in its own examples -- an
 * {@link com.github.jinahya.persistence.test.util.spec._Employee Employee}, the
 * {@link com.github.jinahya.persistence.test.util.spec._Department Department} it belongs to, and the
 * {@link com.github.jinahya.persistence.test.util.spec._Address Address} embedded in it -- rather than shapes invented
 * for a test. They exist so that the {@link com.github.jinahya.persistence.test.util.__Instantiator instantiator}, the
 * {@link com.github.jinahya.persistence.test.util.__Randomizer randomizer} and the
 * {@link com.github.jinahya.persistence.test.util.__Persister persister} are exercised against a real provider, and a
 * real database, on an entity which actually has a generated identifier, a version, an embedded value, and an
 * association which may not be {@code null}.
 * <p>
 * Every class is named by the convention the {@code __*Utils} classes locate by: {@code _Employee} is served by
 * {@code _Employee_Instantiator}, {@code _Employee_Randomizer} and {@code _Employee_Persister}, declared beside it.
 * <p>
 * The three roles divide the work of producing a persistable instance, and the division is the point of this package:
 * <ul>
 * <li>the instantiator assigns what a constructor would -- here, the hire date;</li>
 * <li>the randomizer fills what is free to vary, and leaves alone everything it is told to exclude -- the identifier
 * and the version, which belong to the provider, the hire date, which belongs to the instantiator, and the
 * associations, which belong to the persister;</li>
 * <li>the persister supplies the association the database requires, persisting the parent first.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.persistence.test.util.__PersisterUtils#newPersistedInstanceOf(jakarta.persistence.EntityManager,
 *         Class)
 */
package com.github.jinahya.persistence.test.util.spec;
