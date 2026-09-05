/**
 * Interfaces and classes for testing classes/interfaces created with {@code jinahya-persistence}.
 * <p>
 * A target class is instantiated, randomized, and persisted, respectively, by an {@link
 * com.github.jinahya.persistence.test.__Instantiator instantiator}, a {@link
 * com.github.jinahya.persistence.test.__Randomizer randomizer}, and a {@link
 * com.github.jinahya.persistence.test.__Persister persister}, each of which is located, for the target class, by a
 * corresponding locator; see {@link com.github.jinahya.persistence.test.__InstantiatorLocator#STANDARD}, {@link
 * com.github.jinahya.persistence.test.__RandomizerLocator#STANDARD}, and {@link
 * com.github.jinahya.persistence.test.__PersisterLocator#STANDARD} for the naming conventions applied, and specify a
 * custom locator for classes which do not follow them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
//@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.test;
