/**
 * Interfaces and classes which complement Jakarta Persistence when creating a persistence unit.
 * <p>
 * Two themes live here.
 *
 * <h2>Attribute converters</h2>
 * {@link com.github.jinahya.persistence.more.__StringAttributeConverter} is the base for storing a value as a
 * {@code String}, and carries ready-made converters for the {@link java.lang.Number} types.
 * {@link com.github.jinahya.persistence.more.__CollectionStringAttributeConverter} stores a whole collection in one
 * delimited column. Converters can be composed rather than written from scratch:
 * {@link com.github.jinahya.persistence.more.__ChainingAttributeConverter} and
 * {@link com.github.jinahya.persistence.more.__AttributeConverterUtils} build one converter out of several, through
 * intermediate types.
 *
 * <h2>Enums with stable persisted values</h2>
 * {@link com.github.jinahya.persistence.more.__AttributeEnum} lets an enum constant declare the value actually written
 * to the database, so the persisted form survives renaming and reordering of constants;
 * {@link com.github.jinahya.persistence.more.__AttributeEnumConverter} is the converter for it, and
 * {@link com.github.jinahya.persistence.more.__AttributeEnumUtils} looks a constant up by its attribute value.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
//@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more;
