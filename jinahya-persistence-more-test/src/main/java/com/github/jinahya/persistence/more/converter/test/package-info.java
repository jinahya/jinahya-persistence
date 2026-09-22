/**
 * Abstract test classes for testing what {@code com.github.jinahya.persistence.more.converter} defines.
 * <p>
 * {@link com.github.jinahya.persistence.more.converter.test.__AttributeConverter_Test} is the base for testing any
 * {@link jakarta.persistence.AttributeConverter}: a subclass names the converter and the two types it converts
 * between, and each assertion takes an
 * {@link com.github.jinahya.persistence.more.converter.test.__AttributeConverterTestCase}, which pairs an entity
 * attribute with the db data it should become. Overriding
 * {@link com.github.jinahya.persistence.more.converter.test.__AttributeConverter_Test#testCases() testCases()} is all
 * it takes to get both directions checked; the {@code TestTemplate} providers are the other path, for a subclass
 * wanting an invocation of its own per case.
 * {@link com.github.jinahya.persistence.more.converter.test.__StringAttributeConverter_Test} and
 * {@link com.github.jinahya.persistence.more.converter.test.__JoinedStringAttributeConverter_Test} specialize it for
 * the {@code String}-valued converters.
 * <p>
 * {@link com.github.jinahya.persistence.more.converter.test.__AttributeEnumConverter_Test} extends
 * {@link com.github.jinahya.persistence.more.test.__AttributeEnum_Test} next door — what it tests is a converter, but
 * what it converts is an enum, and both halves are worth checking at once — and round-trips every constant through
 * its converter. It mirrors the {@code __OfString}, {@code __OfNumber}, {@code __OfInteger} and {@code __OfLong}
 * nests of what it tests, so a subclass names only what those do not already fix.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.converter.test;
