/**
 * EclipseLink's own way into {@link com.github.jinahya.persistence.encryption}: one named converter,
 * {@link com.github.jinahya.persistence.encryption.eclipselink.EncryptedConverter}, for every supported type, the
 * column made binary by the converter, and the service from a persistence unit property. The rows are those of the
 * generic module's converters.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.encryption.eclipselink;
