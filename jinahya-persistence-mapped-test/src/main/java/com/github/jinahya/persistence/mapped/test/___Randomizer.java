package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.DefaultClassInfoStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An abstract class for randomizing a specific class.
 *
 * @param <T> type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___Instantiator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class ___Randomizer<T> {

    protected static Collection<? super String> moreExcludedFields(
            final @Nonnull Collection<? super String> excludedFields,
            final @Nonnull Iterable<String> moreExcludedFields) {
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        Objects.requireNonNull(moreExcludedFields, "moreExcludedFields is null");
        moreExcludedFields.forEach(excludedFields::add);
        return excludedFields;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass    the class to be randomized.
     * @param excludedFields fields to be excluded from randomization.
     * @deprecated Use {@link #___Randomizer(Class, Iterable)}
     */
    @Deprecated(forRemoval = true)
    protected ___Randomizer(final @Nonnull Class<T> targetClass, final @Nonnull String... excludedFields) {
//        super();
//        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
//        this.excludedFields = Arrays
//                .stream(Objects.requireNonNull(excludedFields, "excludedFields is null"))
//                .filter(Objects::nonNull)
//                .map(String::strip)
//                .filter(v -> !v.isBlank())
//                .collect(Collectors.toSet());
        this(targetClass, Arrays.asList(excludedFields));
    }

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass    the class to be randomized.
     * @param excludedFields fields to be excluded from randomization.
     */
    protected ___Randomizer(final @Nonnull Class<T> targetClass, final @Nonnull Iterable<String> excludedFields) {
        super();
        Objects.requireNonNull(targetClass, "targetClass is null");
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        this.targetClass = targetClass;
        this.excludedFields = StreamSupport.stream(excludedFields.spliterator(), false)
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> !v.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new data provider strategy.
     *
     * @return a new data provider strategy.
     * @see RandomDataProviderStrategyImpl#RandomDataProviderStrategyImpl()
     */
    @Nonnull
    protected DataProviderStrategy getDataProviderStrategy() {
        return new RandomDataProviderStrategyImpl();
    }

    /**
     * Creates a new factory created with a data infor strategy from the
     * {@link #getDataProviderStrategy() dataProviderStrategy} method, and set with a class info strategy from the
     * {@link #getClassInfoStrategy() classInfoStrategy} method.
     *
     * @return a new factory.
     * @see PodamFactoryImpl#PodamFactoryImpl(DataProviderStrategy)
     * @see #getClassInfoStrategy()
     * @see PodamFactory#setClassStrategy(ClassInfoStrategy)
     */
    @Nonnull
    protected PodamFactory getPodamFactory() {
        final var factory = new PodamFactoryImpl(getDataProviderStrategy());
        factory.setClassStrategy(getClassInfoStrategy());
        return factory;
    }

    /**
     * Creates a new class info strategy which excludes {@link #excludedFields}.
     *
     * @return a new class info strategy which excludes {@link #excludedFields}
     */
    @Nonnull
    protected ClassInfoStrategy getClassInfoStrategy() {
        final var classInfoStrategy = DefaultClassInfoStrategy.getInstance();
        excludedFields.forEach(v -> classInfoStrategy.addExcludedField(targetClass, v));
        return classInfoStrategy;
    }

    /**
     * Returns a randomized instance of the {@link #targetClass}.
     *
     * @return a randomized instance of the {@link #targetClass}.
     */
    @Nonnull
    public T get() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(targetClass)
                .map(o -> getPodamFactory().populatePojo(o))
                .orElseGet(() -> getPodamFactory().manufacturePojo(targetClass));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The target type to randomized.
     */
    protected final Class<T> targetClass;

    /**
     * An unmodifiable set of field names ot exclude form the randomization.
     */
    protected final Set<String> excludedFields;
}
