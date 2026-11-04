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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking the member which references the parent of a {@link __SelfReferencing} entity.
 * <p>
 * {@link __SelfReferencing} is an interface and an interface can not hold any state, so it can not map the association
 * itself. The implementing entity declares it, and this annotation says which member it is:
 * {@snippet lang = "java":
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @__SelfReferencingParent
 * @ManyToOne(fetch = FetchType.LAZY, optional = true)
 * @JoinColumn(name = "hierarchy_parent_id", nullable = true, insertable = true, updatable = false) private
 *         Category parent; }
 *         <p>
 *         The mark takes the place of a name convention. Without it, the only way to find the member is to agree on its
 *         name, which breaks, silently, the moment the member is renamed; with it, the member can be named whatever the
 *         implementing entity's own vocabulary calls for.
 *         <p>
 *         That freedom is not, however, a reason to pick an arbitrary name. Naming the member so that its accessor
 *         comes out as {@link __SelfReferencing#getHierarchyParent() getHierarchyParent()} — whether written by hand or
 *         generated — lets the entity satisfy the interface directly and never go near reflection. This mark is still
 *         worth carrying then: it is what says where the association lives.
 *
 *         <h2>Where it goes</h2>
 *         It goes on whichever member the implementing entity's own access type maps the association on — the
 *         <em>field</em> under {@link AccessType#FIELD FIELD} access, the <em>accessor</em> under
 *         {@link AccessType#PROPERTY PROPERTY} access:
 *         {@snippet lang = "java":
 * @__SelfReferencingParent
 * @ManyToOne(fetch = FetchType.LAZY, optional = true)
 * @JoinColumn(name = "hierarchy_parent_id", nullable = true, insertable = true, updatable = false) public
 *         Category getParent() { return parent; }}
 *         <p>
 *         Access type is the entity's to choose — for the whole entity or for a single attribute, with
 *         {@link Access @Access} — and this library neither imposes one nor has any way to see which one was chosen. So
 *         both members are accepted, and the mark is free to sit next to the mapping it describes rather than away from
 *         it.
 *         <p>
 *         A marked accessor has to take no arguments and return a value; anything else fails the lookup. Marking a
 *         field
 *         <em>and</em> its own accessor is allowed, and the accessor wins — reading through it is what lets an
 *         uninitialized proxy answer with its parent instead of with {@code null}, which is the one behavioural
 *         difference between the two placements; see
 *         {@link __SelfReferencingUtils#parentOf(__SelfReferencing) parentOf(instance)}.
 *         <p>
 *         A marked accessor does not become {@link jakarta.persistence.Transient @Transient}, and does not stop being a
 *         mapping candidate, by virtue of this mark. Under {@link AccessType#PROPERTY PROPERTY} access an entity which
 *         derives its parent rather than storing it has to annotate its own accessor; nothing declared on
 *         {@link __SelfReferencing} reaches it, because an implemented interface is not part of the mapping.
 *         <p>
 *         The retention is {@link RetentionPolicy#RUNTIME RUNTIME} because the member is looked up while the
 *         application runs. {@code CLASS}, the default, would hide the mark from
 *         {@link Class#getDeclaredFields() getDeclaredFields()} and
 *         {@link Class#getDeclaredMethods() getDeclaredMethods()} alike, with nothing to see at compile time and
 *         nothing to see until the lookup fails.
 *         <p>
 *         Do <em>not</em> add a {@link jakarta.persistence.Column @Column} to the marked member. Hibernate rejects it
 *         while building the mapping —
 *         {@code AnnotationException: ... is a '@ManyToOne' association and may not use '@Column' ...} — which is a
 *         failure to start, not an annotation quietly ignored. Use {@link jakarta.persistence.JoinColumn @JoinColumn}.
 * @see __SelfReferencing
 * @see __SelfReferencingOrdinal
 * @see __SelfReferencingUtils#parentOf(__SelfReferencing)
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
public @interface __SelfReferencingParent {

}
