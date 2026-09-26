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
 * An annotation for marking the member which holds the ordinal of a {@link __SelfReferencingOrdered} entity among its
 * siblings, that is, among the entities sharing the same {@link __SelfReferencing#getHierarchyParent() parent}.
 * <p>
 * {@link __SelfReferencingOrdered} is an interface and an interface can not hold any state, so the value is declared by
 * the implementing entity, and this annotation says which member it is:
 * {@snippet lang = "java":
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @__SelfReferencingOrdinal
 * @NotNull
 * @PositiveOrZero
 * @Column(name = "sibling_ordinal", nullable = false, insertable = true, updatable = true) private Integer
 *         siblingOrdinal; }
 *         <p>
 *         Like {@link __SelfReferencingParent}, and unlike what this mark once was, it is <em>required</em> — of the
 *         types which carry it, which are the ones implementing {@link __SelfReferencingOrdered}. Ordering is declared
 *         by implementing that interface, not by whether this mark happens to be present, and an entity which does not
 *         order its siblings implements {@link __SelfReferencing} alone and never writes this annotation at all.
 *         {@link __SelfReferencingOrderedUtils#ordinalOf(__SelfReferencingOrdered) ordinalOf(instance)} will not take
 *         such an entity, so a missing mark on a type which does reach it is a broken implementation, and fails.
 *         <p>
 *         Type it {@code int} or {@link Integer}; both are read. The choice is the entity's, and it decides what a
 *         forgotten assignment looks like. An {@link Integer} constrained
 *         {@link jakarta.validation.constraints.NotNull
 * @NotNull}, as above, fails validation before the insert. An {@code int} can not fail: it reads {@code 0},
 *         which is the <em>head</em> of the sibling group, so the instance silently jumps ahead of every sibling which
 *         was placed on purpose.
 *         <p>
 *         Prefer the {@link Integer}, and give a newly persisted instance an ordinal past the end of its group rather
 *         than leaning on a default. It also lines up with
 *         {@link __SelfReferencingOrdered#getSiblingOrdinal() getSiblingOrdinal()}, which answers an {@link Integer}
 *         too, so an entity implementing that method by returning this member has nothing to unbox and no
 *         {@link NullPointerException} to throw from inside its own accessor.
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
 * @Transient public int getDisplayOrder() { return siblings().indexOf(this); }}
 *         <p>
 *         What counts as the siblings of a root — the instances with no parent, or nothing at all — is the entity's to
 *         settle there; this mark says only where the answer is read from.
 *         <p>
 *         The {@link Transient @Transient} there is the entity's to write, and is not optional under
 *         {@link AccessType#PROPERTY PROPERTY} access: a declared accessor is a mapping candidate, and this mark
 *         neither makes it one nor keeps it from becoming one. Nothing declared on {@link __SelfReferencing} supplies
 *         it either — an implemented interface is not part of the mapping, so the {@code @Transient} on
 *         {@link __SelfReferencingOrdered#getSiblingOrdinal() getSiblingOrdinal()} does not reach an implementation's
 *         own accessor. Omit it and the entity gains a column it never asked for.
 *         <p>
 *         A marked accessor has to take no arguments and return {@code int} or {@link Integer}; anything else fails the
 *         lookup. Marking a field <em>and</em> its own accessor is allowed, and the accessor wins.
 *         <p>
 *         The retention is {@link RetentionPolicy#RUNTIME RUNTIME} because the member is looked up while the
 *         application runs; see {@link __SelfReferencingParent __SelfReferencingParent}.
 * @see __SelfReferencingOrdered
 * @see __SelfReferencingParent
 * @see __SelfReferencingOrderedUtils#ordinalOf(__SelfReferencingOrdered)
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
