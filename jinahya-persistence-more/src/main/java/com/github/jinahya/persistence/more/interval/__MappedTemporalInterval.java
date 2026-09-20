package com.github.jinahya.persistence.more.interval;

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
import jakarta.persistence.MappedSuperclass;

import java.time.temporal.Temporal;

/**
 * An abstract mapped superclass for an interval on a temporal axis, of any point type.
 * <p>
 * The two columns come from {@link __MappedInterval} and the measuring from {@link __TemporalInterval}. This class
 * adds neither a column nor a method; what it adds is that both arrive together, so a subclass naming its point type
 * extends one thing.
 *
 * <h2>This is the extension point</h2>
 * The classes beside it fix every {@code java.time} point type which can soundly bound an interval and which a
 * database can compare — see the package documentation for the set and for the two types deliberately left out. This
 * class is what to extend for anything outside it: a {@link Temporal} of a downstream's own, or one of the standard
 * ones under a storage arrangement the classes beside it do not take.
 * <p>
 * A subclass supplies the point type and the one method left abstract, narrowing its return to the amount that type
 * measures in. This is the shape each of the neighbouring classes is written in, {@link __MappedYearInterval} among
 * them:
 * {@snippet lang = "java":
 * @Access(AccessType.FIELD)
 * @MappedSuperclass
 * public abstract class __MappedSomeInterval extends __MappedTemporalInterval<Year> {
 *
 *     @Override
 *     @Transient
 *     public Period getTemporalAmount() {
 *         final Year startInclusive = getIntervalStart();
 *         final Year endExclusive = getIntervalEnd();
 *         if (startInclusive == null || endExclusive == null) {
 *             return null;
 *         }
 *         return Period.ofYears(endExclusive.getValue() - startInclusive.getValue());
 *     }
 * }
 *}
 * The point type has to be one Jakarta Persistence maps, or the subclass has to convert it — see
 * {@link __MappedLocalTimeInterval}, which works a conversion through. And the caution on {@link __MappedInterval}
 * about an order which is not the timeline applies to whatever is chosen here.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedInterval
 * @see __TemporalInterval
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedTemporalInterval<T extends Temporal & Comparable<? super T>>
        extends __MappedInterval<T>
        implements __TemporalInterval<T> {

    /**
     * Creates a new instance.
     */
    protected __MappedTemporalInterval() {
        super();
    }
}
