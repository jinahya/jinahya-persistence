/**
 * Integrates {@link com.github.jinahya.persistence.encryption} with Hibernate ORM: an engine on Hibernate ORM's own
 * event listeners, in place of the entity listener, which never nulls a plaintext in an entity, and never writes on a
 * read.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.persistence.encryption.hibernate.EntityEncryptionIntegrator
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.encryption.hibernate;
