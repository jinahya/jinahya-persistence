package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Transient;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking the member which holds the ordinal of a {@link __SelfReferencing} entity among its
 * siblings, that is, among the entities sharing the same {@link __SelfReferencing#getHierarchyParent() parent}.
 * <p>
 * {@link __SelfReferencing} is an interface and an interface can not hold any state, so the value is declared by the
 * implementing entity, and this annotation says which member it is:
 * {@snippet lang = "java":
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @__SelfReferencingOrdinal
 * @Column(name = "sibling_ordinal", nullable = true, insertable = true, updatable = true) private Integer
 *         siblingOrdinal; }
 *         <p>
 *         Unlike {@link __SelfReferencingParent}, this mark is <em>optional</em>. An entity which does not order its
 *         siblings simply does not carry it, and
 *         {@link __SelfReferencingUtils#ordinalOf(__SelfReferencing) ordinalOf(instance)} answers {@code null} for such
 *         a type rather than failing.
 *         <p>
 *         Do not type it {@code int}. The absence of an ordering and the first position among siblings are two
 *         different answers, and {@code 0} can not carry both.
 *
 *         <h2>Where it goes</h2>
 *         It goes on whichever member the implementing entity's own access type maps the value on — the <em>field</em>
 *         under {@link AccessType#FIELD FIELD} access, the <em>accessor</em> under {@link AccessType#PROPERTY PROPERTY}
 *         access. Access type is the entity's to choose — for the whole entity or for a single attribute, with
 *         {@link Access @Access} — and this library neither imposes one nor has any way to see which one was chosen, so
 *         both members are accepted.
 *         <p>
 *         The value does not have to be stored at all. An entity which <em>derives</em> its ordinal has no field to
 *         mark, and marks the accessor which computes it:
 *         {@snippet lang = "java":
 * @__SelfReferencingOrdinal
 * @Transient public Integer getDisplayOrder() { return parent == null ? null :
 *         parent.getChildren().indexOf(this); }}
 *         <p>
 *         The {@link Transient @Transient} there is the entity's to write, and is not optional under
 *         {@link AccessType#PROPERTY PROPERTY} access: a declared accessor is a mapping candidate, and this mark
 *         neither makes it one nor keeps it from becoming one. Nothing declared on {@link __SelfReferencing} supplies
 *         it either — an implemented interface is not part of the mapping, so the {@code @Transient} on
 *         {@link __SelfReferencing#getSiblingOrdinal() getSiblingOrdinal()} does not reach an implementation's own
 *         accessor. Omit it and the entity gains a column it never asked for.
 *         <p>
 *         A marked accessor has to take no arguments and return {@link Integer}; anything else fails the lookup.
 *         Marking a field <em>and</em> its own accessor is allowed, and the accessor wins.
 *         <p>
 *         The retention is {@link RetentionPolicy#RUNTIME RUNTIME} because the member is looked up while the
 *         application runs; see {@link __SelfReferencingParent __SelfReferencingParent}.
 * @see __SelfReferencing
 * @see __SelfReferencingParent
 * @see __SelfReferencingUtils#ordinalOf(__SelfReferencing)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Documented
@Retention(value = RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.METHOD
})
public @interface __SelfReferencingOrdinal {

}
