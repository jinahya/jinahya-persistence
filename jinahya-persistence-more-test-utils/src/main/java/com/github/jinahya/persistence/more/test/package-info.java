/**
 * Abstract test classes and utilities for testing what
 * {@link com.github.jinahya.persistence.more} defines.
 * <p>
 * These types live in the main source set, rather than the test source set, so that they can be depended on from other
 * modules' tests.
 *
 * <h2>Attribute converters</h2>
 * {@link com.github.jinahya.persistence.more.test.__AttributeConverter_Test} is the base for testing any
 * {@link jakarta.persistence.AttributeConverter}: a subclass names the converter and the two types it converts
 * between, and each assertion takes an
 * {@link com.github.jinahya.persistence.more.test.__AttributeConverterTestCase}, which pairs an entity attribute with
 * the db data it should become.
 * {@link com.github.jinahya.persistence.more.test.__StringAttributeConverter_Test} and
 * {@link com.github.jinahya.persistence.more.test.__CollectionStringAttributeConverter_Test} specialize it for the
 * {@code String}-valued converters.
 *
 * <h2>Attribute enums</h2>
 * {@link com.github.jinahya.persistence.more.test.__AttributeEnum_Test} verifies that an
 * {@link com.github.jinahya.persistence.more.__AttributeEnum} has no {@code null} and no duplicate attribute values,
 * and {@link com.github.jinahya.persistence.more.test.__AttributeEnumConverter_Test} additionally round-trips every
 * constant through its converter. {@link com.github.jinahya.persistence.more.test.__AttributeEnum_TestUtils} offers
 * the same traversals, including random constant selection, to tests which do not extend those classes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
//@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.test;
